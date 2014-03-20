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

		// First get abbreviations for the clue words
		Map<String, Set<String>> abbreviations = ABBR
				.getAbbreviationsForClue(clue);

		// Now get combinations of first / last letters of the clue which could
		// be used to construct a solution
		Map<String, Set<String>> substrings = new LinkedHashMap<>();
		constructSubstrings(c.getClueWords(), substrings);

		// Get synonyms of each clue word, which may also be used
		Map<String, Set<String>> synonyms = THESAURUS.getSynonymsForClue(clue);

		Map<MapType, Map<String, Set<String>>> components = new HashMap<>();
		components.put(MapType.ABBREVIATION, abbreviations);
		components.put(MapType.SUBSTRING, substrings);
		components.put(MapType.SYNONYM, synonyms);

		// Reduce map to only valid data
		// Separate maps to aid with trace messages
		reduceMaps(components, pattern);

		// Generate solutions
		solutions.addAll(generateSolutions(components, c, pattern));

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		return solutions;
	}

	private void reduceMaps(Map<MapType, Map<String, Set<String>>> components,
			SolutionPattern pattern) {
		// Min and max length a substring can be
		final int minSubstringLength = 1;
		final int maxSubstringLength = pattern.getTotalLength() > 1 ? pattern
				.getTotalLength() - 1 : 1;

		for (Entry<MapType, Map<String, Set<String>>> map : components
				.entrySet()) {
			// For each set of substrings
			for (Set<String> set : map.getValue().values()) {
				Iterator<String> it = set.iterator();
				// For each calculated substring
				while (it.hasNext()) {
					String substring = it.next();
					// 1 <= length <= pattern length ( - 2)
					if (substring.length() < minSubstringLength
							|| substring.length() > maxSubstringLength) {
						it.remove();
						continue;
					}
				}
			}
		}
	}

	private SolutionCollection generateSolutions(
			Map<MapType, Map<String, Set<String>>> components, Clue clue,
			SolutionPattern pattern) {

		SolutionCollection sc = new SolutionCollection();

		try {
			Set<Set<String>> powerSet = generatePowerSet(clue);
			for (Set<String> combination : powerSet) {
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

	private void generateSolutions(String string,
			Map<MapType, Map<String, Set<String>>> components,
			String[] clueWords, SolutionPattern pattern, SolutionCollection sc,
			List<String> trace) {

		// Integrate dictionary checking to check the generated String's
		// prefix
		if (!pattern.matchPrefix(string) || !DICTIONARY.prefixMatch(string)) {
			return;
		}

		// The base case
		if (string.length() >= pattern.getTotalLength()
				|| clueWords.length == 0) {
			if (pattern.match(string)) {
				Solution s = new Solution(string, NAME);
				for (String entry : trace) {
					s.addToTrace(entry);
				}
				s.addToTrace("The above components have been put together to form the proposed solution.");
				sc.add(s);
			}
		} else {
			// Take the first word of the current combination
			String currentWord = clueWords[0];

			// And remove it from the remaining words
			String[] remainingWords = Arrays.copyOfRange(clueWords, 1,
					clueWords.length);

			for (Entry<MapType, Map<String, Set<String>>> map : components
					.entrySet()) {
				Set<String> matches = map.getValue().get(currentWord);
				if (matches != null) {
					for (String match : matches) {
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

						generateSolutions(string + match, components,
								remainingWords, pattern, sc, newTrace);
					}
				}
			}
		}

	}

	private Set<Set<String>> generatePowerSet(Clue clue)
			throws IllegalArgumentException {
		Set<Set<String>> powerSet = Sets.powerSet(new LinkedHashSet<String>(
				Arrays.asList(clue.getClueWords())));

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

	private void constructSubstrings(String[] clue,
			Map<String, Set<String>> components) {

		// For each clue word
		for (String word : clue) {
			int length = word.length();
			if (length == 1 || length == 2) {
				Util.addToMap(components, word, word, HashSet.class);
			} else if (length > 1) {
				// For each substring, starting from the front
				for (int i = 1; i < length; i++) {
					String substring = word.substring(0, i);
					if (!substring.equals(word)) {
						Util.addToMap(components, word, substring,
								HashSet.class);
					}
				}
				// Now the ends of the words
				final int lastIndex = length;
				for (int i = lastIndex - 1; i > 0; i--) {
					String substring = word.substring(i, lastIndex);
					Util.addToMap(components, word, substring, HashSet.class);
				}
			}
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
	 * Stu's method to try and identify charade clues in the database
	 */
	private static void tagDB() {
		Charade c = new Charade();

		InputStream is = Settings.class.getResourceAsStream("/cryptic.csv");

		try (ICsvListReader reader = new CsvListReader(
				new InputStreamReader(is), CsvPreference.STANDARD_PREFERENCE)) {

			List<String> line;
			while ((line = reader.read()) != null) {
				String clue = WordUtils.normaliseInput(line.get(0), false);
				String solution = WordUtils.normaliseInput(line.get(1), true);

				SolutionCollection solutions = c.solve(new Clue(clue,
						SolutionPattern.toPattern(solution, false), solution,
						NAME));
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
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(Charade.class);
		// tagDB();
		// Clue c = new Clue("Outlaw leader managing money", "BANK???",
		// "BANKING",
		// "charade");
		Clue c = new Clue("Quiet bird has a sign on a strange occurrence",
				"PHENO?????", "PHENOMENON", "charade");
		Charade ch = new Charade();
		ch.solve(c);
		// ch.tagDB();
	}

} // End of class Charade
