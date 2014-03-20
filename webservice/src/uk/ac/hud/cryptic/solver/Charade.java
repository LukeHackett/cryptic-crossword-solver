package uk.ac.hud.cryptic.solver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Abbreviations;
import uk.ac.hud.cryptic.resource.Thesaurus;
import uk.ac.hud.cryptic.util.Util;
import uk.ac.hud.cryptic.util.WordUtils;

import com.google.common.collect.Sets;

/**
 * Charade solver algorithm
 * 
 * @author Stuart Leader, Leanne Butcher
 * @version 0.3
 */
public class Charade extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "charade";

	// The abbreviations resource
	private static final Abbreviations ABBR = Abbreviations.getInstance();

	// The type of components that make up a charade solution
	private enum MapType {
		ABBREVIATION, SUBSTRING, SYNONYM
	}

	/**
	 * Default constructor for solver class
	 */
	public Charade() {
		super();
	}

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Charade(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		final String clue = c.getClueNoPunctuation(false);
		final SolutionPattern pattern = c.getPattern();

		SolutionCollection solutions = new SolutionCollection();

		// Charades are made up of abbreviations, substrings and synonyms. Get
		// all potential components and store in a map.
		Map<MapType, Map<String, Set<String>>> components = getComponents(clue);

		// Reduce map to only valid data
		reduceMaps(components, pattern);

		// Generate solutions
		solutions.addAll(generateSolutions(components, c, pattern));

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		return solutions;
	}

	/**
	 * Generate all the sub-components which can be used to construct a
	 * solution. These will be made up of abbreviations, substrings of clue
	 * words, and synonyms of clue words.
	 * 
	 * @param clue
	 *            - The given clue as a String (sans punctuation)
	 * @return a maps of maps of the calculated components
	 */
	private Map<MapType, Map<String, Set<String>>> getComponents(String clue) {
		// First get abbreviations for the clue words
		Map<String, Set<String>> abbreviations = ABBR
				.getAbbreviationsForClue(clue);

		// Now get combinations of first / last letters of the clue which could
		// be used to construct a solution
		Map<String, Set<String>> substrings = constructSubstrings(clue
				.split(WordUtils.REGEX_WHITESPACE));

		// Get synonyms of each clue word, which may also be used
		Map<String, Set<String>> synonyms = THESAURUS.getSynonymsForClue(clue);

		// Add all these separate components into a single map
		// The separate maps to aid with trace messages
		Map<MapType, Map<String, Set<String>>> components = new HashMap<>();
		components.put(MapType.ABBREVIATION, abbreviations);
		components.put(MapType.SUBSTRING, substrings);
		components.put(MapType.SYNONYM, synonyms);

		return components;
	}

	/**
	 * Remove components from the maps if it isn't suitable for use. For
	 * example, Charades are made up of two or more subcomponents which are
	 * fused together. Therefore a single subcomponent must not exceed the total
	 * length of the solution, minus one.
	 * 
	 * @param components
	 *            - the components map to filter
	 * @param pattern
	 *            - the solution pattern for the current clue
	 */
	private void reduceMaps(Map<MapType, Map<String, Set<String>>> components,
			SolutionPattern pattern) {
		// Min and max length a substring can be
		final int minSubstringLength = 1;
		final int maxSubstringLength = pattern.getTotalLength() > 1 ? pattern
				.getTotalLength() - 1 : 1;

		// For each component map (e.g. abbreviations)
		for (Entry<MapType, Map<String, Set<String>>> map : components
				.entrySet()) {
			// For the components for each clue word
			for (Set<String> set : map.getValue().values()) {
				Iterator<String> it = set.iterator();
				// For each calculated component
				while (it.hasNext()) {
					String substring = it.next();
					// 1 <= length <= pattern length ( - 2)
					if (substring.length() < minSubstringLength
							|| substring.length() > maxSubstringLength) {
						// Remove if not fit for use
						it.remove();
					}
				}
			}
		}
	}

	/**
	 * Attempt to construct solutions from the found components.
	 * 
	 * @param components
	 *            - a map of all the components which can be used to create a
	 *            solution
	 * @param clue
	 *            - the clue which is being solved
	 * @param pattern
	 *            - the solution pattern
	 * @return a collection of all potential solutions that have been found
	 */
	private SolutionCollection generateSolutions(
			Map<MapType, Map<String, Set<String>>> components, Clue clue,
			SolutionPattern pattern) {

		SolutionCollection sc = new SolutionCollection();

		try {
			// Get all sequential combinations of clue words
			Set<Set<String>> powerSet = generatePowerSet(clue);
			// For each of these, find potential solutions
			for (Set<String> combination : powerSet) {
				// Entry to the recursive method
				generateSolutions("", components,
						combination.toArray(new String[combination.size()]),
						pattern, sc, new ArrayList<String>());
			}
		} catch (IllegalArgumentException e) {
			System.err.println("Too many clue words to look for charades.");
			return sc;
		}

		return sc;
	}

	/**
	 * A recursive method which attempts to construct the solutions from the
	 * calculated sub-components
	 * 
	 * @param string
	 *            - an incremental solution which is constructed bit by bit.
	 *            Starts with an empty string.
	 * @param components
	 *            - the components from which a solution can be build from
	 * @param clueWords
	 *            - a set of sequential clue words which has been previously
	 *            calculated
	 * @param pattern
	 *            - the solution pattern
	 * @param sc
	 *            - potential solutions are added to the collection
	 * @param trace
	 *            - a list of trace messages which are generated as the solution
	 *            is constructed
	 */
	private void generateSolutions(String string,
			Map<MapType, Map<String, Set<String>>> components,
			String[] clueWords, SolutionPattern pattern, SolutionCollection sc,
			List<String> trace) {

		// Integrate dictionary checking to check the generated String's
		// prefix
		if (!pattern.matchPrefix(string)) { // TODO Makes it super slow? ||
											// !DICTIONARY.prefixMatch(string))
											// {
			return;
		}

		// The base case. Stop if the generated solution is bigger than or equal
		// to the target solution length, or if there are no remaining clue
		// words to use.
		if (string.length() >= pattern.getTotalLength()
				|| clueWords.length == 0) {
			// If the generated solution matches with the clue's solution
			// pattern
			if (pattern.match(string)) {
				// Create a new Solution object and add the trace messages which
				// have been generated
				Solution s = new Solution(string, NAME);
				for (String entry : trace) {
					s.addToTrace(entry);
				}
				s.addToTrace("The above components have been put together to form the proposed solution.");
				sc.add(s);
			}
		} else {
			// The recursive case. Take the first word of the current
			// combination of clue words.
			String currentWord = clueWords[0];

			// And remove it from the remaining words. This will be passed to
			// further iterations of the recursive method
			String[] remainingWords = Arrays.copyOfRange(clueWords, 1,
					clueWords.length);

			// For each type of component - abbreviation, substring or synonym
			for (Entry<MapType, Map<String, Set<String>>> map : components
					.entrySet()) {
				// Get the components for the current clue word
				Set<String> matches = map.getValue().get(currentWord);
				// If there are components which have been calculated
				if (matches != null) {
					// For each of these components
					for (String match : matches) {
						// Add a trace message saying how it has been used
						List<String> newTrace = new ArrayList<>(trace);
						String message = "\"" + match + "\" ";
						if (map.getKey() == MapType.ABBREVIATION) {
							message += "is an abbreviation";
						} else if (map.getKey() == MapType.SUBSTRING) {
							message += "has been taken as a substring";
						} else if (map.getKey() == MapType.SYNONYM) {
							message += "is a synonym";
						}
						message += " of the clue word \"" + currentWord + "\".";
						newTrace.add(message);

						// Round and around we go! Anyone else getting dizzy?
						generateSolutions(string + match, components,
								remainingWords, pattern, sc, newTrace);
					}
				}
			}
		}
	}

	/**
	 * Charades can be constructed from any sequential components from the clue.
	 * For example, if the clue words are "one two three", then a solution may
	 * be constructed by using components for clue words "one, two, three",
	 * "one, three", "one, two" or "two, three". This method generates these
	 * combinations of clue words which will be processed one by one in an
	 * attempt to find the solution.
	 * 
	 * @param clue
	 *            - the given clue to solve
	 * @return a set of all the sequential combinations of the clue words
	 * @throws IllegalArgumentException
	 */
	private Set<Set<String>> generatePowerSet(Clue clue)
			throws IllegalArgumentException {

		// Use an existing library to perform this task
		Set<Set<String>> powerSet = Sets.powerSet(new LinkedHashSet<String>(
				Arrays.asList(clue.getClueWords())));

		// Need to remove combinations with less than two words, as Charades
		// always use at least two components put together.
		Set<Set<String>> newSet = new LinkedHashSet<>(powerSet);
		Iterator<Set<String>> it = newSet.iterator();
		while (it.hasNext()) {
			Set<String> next = it.next();
			if (next.size() < 2) {
				it.remove();
			}
		}

		return newSet;
	}

	/**
	 * Generate substrings of each of the clue words
	 * 
	 * @param clue
	 *            - an array of the clue words
	 * @param components
	 *            - the map
	 * @return a map of the substrings for each clue word
	 */
	private Map<String, Set<String>> constructSubstrings(String[] clue) {

		Map<String, Set<String>> substrings = new LinkedHashMap<>();

		// For each clue word
		for (String word : clue) {
			int length = word.length();
			if (length == 1 || length == 2) {
				Util.addToMap(substrings, word, word, HashSet.class);
			} else if (length > 1) {
				// For each substring, starting from the front
				for (int i = 1; i < length; i++) {
					String substring = word.substring(0, i);
					if (!substring.equals(word)) {
						Util.addToMap(substrings, word, substring,
								HashSet.class);
					}
				}
				// Now the ends of the words
				final int lastIndex = length;
				for (int i = lastIndex - 1; i > 0; i--) {
					String substring = word.substring(i, lastIndex);
					Util.addToMap(substrings, word, substring, HashSet.class);
				}
			}
		}
		return substrings;
	}

	/**
	 * Stu's method to try and identify charade clues in the database
	 */
	private static void tagDB() {
		Charade c = new Charade();

		// CSV file from the database
		// SELECT `clue`, `solution` FROM `cryptic_clues` WHERE `type` IS NULL;
		InputStream is = Settings.class.getResourceAsStream("/cryptic.csv");

		try (ICsvListReader reader = new CsvListReader(
				new InputStreamReader(is), CsvPreference.STANDARD_PREFERENCE)) {

			List<String> line;
			// For each "unmarked" clue
			while ((line = reader.read()) != null) {
				String clue = WordUtils.normaliseInput(line.get(0), false);
				String solution = WordUtils.normaliseInput(line.get(1), true);

				// Solve it, but all the characters are "known" to speed up
				// solving
				SolutionCollection solutions = c.solve(new Clue(clue,
						SolutionPattern.toPattern(solution, false), solution,
						NAME));
				// If this solver has found the solution, print out an SQL
				// update statement
				// But this will need manual verification first as not all are
				// Charades
				if (solutions.contains(solution)) {
					System.out
							.println("UPDATE `cryptic_clues` SET `type`='charade' WHERE `clue` = \""
									+ line.get(0).trim()
									+ "\" AND `solution` = \""
									+ line.get(1).trim()
									+ "\" AND `type` IS NULL;");
					for (String entry : solutions.getSolution(solution)
							.getSolutionTrace()) {
						System.out.println("-- " + entry);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the database name for this type of clue
	 * 
	 * @return the database name for this type of clue
	 */
	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(Charade.class);
	}

} // End of class Charade
