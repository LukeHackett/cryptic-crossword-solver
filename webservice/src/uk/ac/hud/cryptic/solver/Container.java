package uk.ac.hud.cryptic.solver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import uk.ac.hud.cryptic.resource.Categoriser;
import uk.ac.hud.cryptic.util.Util;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Container solver algorithm ------------Example------------- Clue - Stash or
 * put in stage (7) Answer - Storage --------------------------------
 * 
 * @author Mohammad Rahman
 * @version 0.1
 */
public class Container extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "container";
	private static final String LINR = "0linr0";
	private static final String RINL = "0rinl0";

	/**
	 * Default constructor for solver class
	 */
	public Container() {
		super();
	}

	/**
	 * Enum for the positions of letter to delete
	 */
	private enum Position {
		RINL("right in left"), LINR("left in right"), NONE("");

		// Position of letter
		private final String text;

		// Set position of letter to delete
		Position(String text) {
			this.text = text;
		}

		// Get position of letter to delete
		private String getText() {
			return text;
		}
	}

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Container(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		SolutionCollection solutions = new SolutionCollection();

		// Get clue pattern
		final SolutionPattern pattern = c.getPattern();

		// Get clue with no punctuation
		String clue = c.getClueNoPunctuation(true);

		// Read in indicators for container clue types
		Collection<String> indicators = Categoriser.getInstance()
				.getIndicators(NAME);

		// Set the initial position of letter to be deleted to none
		Position position = Position.NONE;

		// Look through indicators to determine the position of the letter which
		// should be removed
		loop: for (String indicator : indicators) {
			switch (indicator) {
				case RINL:
					// First letter
					position = Position.RINL;
					break;
				case LINR:
					// Last letter
					position = Position.LINR;
					break;
				default:
					// Check if the clue contains the indicator being checked
					if (clue.contains(indicator)) {
						// Find solutions
						solutions.addAll(findSynonyms(c, pattern, position,
								indicator));
						break loop;
					}
					break;
			}
		}

		pattern.filterSolutions(solutions);

		return solutions;
	}

	private SolutionCollection findSynonyms(Clue c, SolutionPattern pattern,
			Position position, String indicator) {

		SolutionCollection solutions = new SolutionCollection();

		// Separate the words for fast comparison
		String[] cluewords = c.getClueWords();

		Map<String, Set<String>> synonyms = new HashMap<>();
		for (String word : cluewords) {
			synonyms.put(word, THESAURUS.getSynonyms(word));
			synonyms.get(word).add(word);
		}

		filterSynonyms(synonyms, pattern);

		solutions = matchUpSynonyms(synonyms, position, pattern);

		return solutions;
	}

	private SolutionCollection matchUpSynonyms(
			Map<String, Set<String>> synonyms, Position position,
			SolutionPattern pattern) {

		SolutionCollection solutions = new SolutionCollection();

		for (Entry<String, Set<String>> outerEntry : synonyms.entrySet()) {
			for (Entry<String, Set<String>> innerEntry : synonyms.entrySet()) {
				if (!(outerEntry == innerEntry)) {
					for (String outerString : outerEntry.getValue()) {
						for (String innerString : innerEntry.getValue()) {
							containWord(solutions, outerEntry.getKey(),
									outerString, innerEntry.getKey(),
									innerString, position, pattern);
						}
					}
				}
			}
		}

		return solutions;
	}

	private void containWord(SolutionCollection solutions, String firstClue,
			String firstWord, String secondClue, String secondWord,
			Position position, SolutionPattern pattern) {

		if (firstWord.length() + secondWord.length() == pattern
				.getTotalLength()) {
			// If first word is going into second word
			if (position == Position.LINR) {
				for (int i = 1; i < secondWord.length(); i++) {
					StringBuilder sb = new StringBuilder(secondWord);
					sb.insert(i, firstWord);
					if (DICTIONARY.isWord(sb.toString())) {
						Solution s = new Solution(sb.toString(), NAME);
						s.addToTrace(generateTrace(secondClue, secondWord));
						s.addToTrace(generateTrace(firstClue, firstWord));
						s.addToTrace("Insert the word \"" + firstWord
								+ "\" into the middle of \"" + secondWord
								+ "\".");
						solutions.add(s);
					}
				}
			}
			// If second word is going into first word
			else if (position == Position.RINL) {
				for (int i = 1; i < firstWord.length(); i++) {
					StringBuilder sb = new StringBuilder(firstWord);
					sb.insert(i, secondWord);
					if (DICTIONARY.isWord(sb.toString())) {
						Solution s = new Solution(sb.toString(), NAME);
						s.addToTrace(generateTrace(firstClue, firstWord));
						s.addToTrace(generateTrace(secondClue, secondWord));
						s.addToTrace("Insert the word \"" + secondWord
								+ "\" into the middle of \"" + firstWord
								+ "\".");
						solutions.add(s);
					}
				}
			}
		}
	}

	private String generateTrace(String clueWord, String synonym) {
		boolean same = clueWord.equals(synonym);
		String message = "Take the ";
		message += same ? "clue word " : "synonym ";
		message += "\"" + synonym + "\"";
		if (!same) {
			message += " of clue word \"" + clueWord + "\".";
		} else {
			message += ".";
		}
		return message;
	}

	private void filterSynonyms(Map<String, Set<String>> synonyms,
			SolutionPattern pattern) {
		// This way rather than iterators to avoid
		// ConcurrentModificationExceptions :(
		Map<String, Set<String>> toRemove = new HashMap<>();

		for (Entry<String, Set<String>> entry : synonyms.entrySet()) {
			for (String synonym : entry.getValue()) {
				String[] syn = synonym.split(WordUtils.SPACE_AND_HYPHEN);
				if (synonym.length() > pattern.getTotalLength()
						|| syn.length > 1) {
					Util.addToMap(toRemove, entry.getKey(), synonym,
							HashSet.class);
				}
			}
		}

		// Now remove those which are not valid
		for (Entry<String, Set<String>> entry : toRemove.entrySet()) {
			for (String synonym : entry.getValue()) {
				synonyms.get(entry.getKey()).remove(synonym);
			}
		}

	}

	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Stu's method to try and identify charade clues in the database
	 */
	private static void tagDB() {
		Container c = new Container();

		final Collection<String> indicators = Categoriser.getInstance()
				.getIndicators("container");

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

				Clue clueObj = new Clue(clue, SolutionPattern.toPattern(
						solution, false), solution, NAME);

				for (String word : clueObj.getClueWords()) {
					if (indicators.contains(word)) {
						// Solve it, but all the characters are "known" to speed
						// up
						// solving
						SolutionCollection solutions = c.solve(clueObj);
						if (solutions.contains(solution)) {
							System.out.println("-- " + clue + ": " + solution);
							for (String entry : solutions.getSolution(solution)
									.getSolutionTrace()) {
								System.out.println("-- " + entry);
							}
							System.out
									.println("UPDATE `cryptic_clues` SET `type`='container' WHERE `clue` = \""
											+ line.get(0).trim()
											+ "\" AND `solution` = \""
											+ line.get(1).trim()
											+ "\" AND `type` IS NULL;");
						}

						break;
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
		testSolver(Container.class);
		// tagDB();
		// Clue c = new Clue("Wear around the brave", "???????");
		// Container co = new Container();
		// co.solve(c);
	}

} // End of class Container
