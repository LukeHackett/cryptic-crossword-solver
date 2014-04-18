package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Categoriser;
import uk.ac.hud.cryptic.util.Util;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Deletion solver algorithm
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.3
 */
public class Deletion extends Solver {
	// A readable (and DB-valid) name for the solver
	private static final String NAME = "deletion";

	// Indicator headings
	private static final String HEAD = "0head0";
	private static final String TAIL = "0tail0";
	private static final String EDGE = "0edges0";

	/**
	 * Enum for the positions of letter to delete
	 */
	private enum Position {
		HEAD("the first letter"), TAIL("the last letter"), EDGE(
				"the first and last letters"), NONE("");

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
	 */
	public Deletion() {
		super();
	}

	/**
	 * Constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Deletion(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		// Solutions for clue
		SolutionCollection solutions = new SolutionCollection();
		// Pattern which solutions must match
		final SolutionPattern pattern = c.getPattern();
		// Read in indicators for deletion clue types
		Collection<String> indicators = Categoriser.getInstance()
				.getIndicators(NAME);
		String clue = c.getClue();

		// Set the initial position of letter to be deleted to none
		Position position = Position.NONE;

		// Look through indicators to determine the position of the letter which
		// should be removed
		loop: for (String indicator : indicators) {
			switch (indicator) {
				case HEAD:
					// First letter
					position = Position.HEAD;
					break;
				case TAIL:
					// Last letter
					position = Position.TAIL;
					break;
				case EDGE:
					// First and last letter
					position = Position.EDGE;
					break;
				default:
					// Check if the clue contains the indicator being checked
					if (clue.contains(indicator)) {
						// Find solutions
						solutions.addAll(findSynonymsToDeleteFrom(c, pattern,
								position, indicator));
						break loop;
					}
					break;
			}
		}

		// Filter solutions against pattern
		pattern.filterSolutions(solutions);

		return solutions;
	}

	/**
	 * Get synonyms from the thesaurus and delete the correct letters from the
	 * correct position
	 * 
	 * @param clue
	 *            - clue
	 * @param pattern
	 *            - clue pattern
	 * @param position
	 *            - position of letter that needs deleting
	 * @param indicator
	 *            - indicator for trace information
	 * @return
	 */
	private SolutionCollection findSynonymsToDeleteFrom(Clue clue,
			SolutionPattern pattern, Position position, String indicator) {
		Map<String, Set<String>> synonyms = new HashMap<>();
		// Retrieve synonyms from the thesaurus
		// Reduce the number of results if no character are supplied
		boolean unknown = pattern.isAllUnknown();
		for (String word : clue.getClueWords()) {
			if (unknown) {
				synonyms.put(word,
						THESAURUS.getEntriesContainingSynonym(word, false));
			} else {
				synonyms.put(word,
						THESAURUS.getEntriesContainingSynonym(word, true));
			}
		}

		// Filter synonyms which will not fit the pattern once letters are
		// removed
		filterSynonyms(synonyms, pattern);

		// Solutions
		SolutionCollection solutions = new SolutionCollection();

		// Loop through synonyms for each word in the clue
		for (Entry<String, Set<String>> entry : synonyms.entrySet()) {
			for (final String synonym : entry.getValue()) {
				String solution = synonym;
				// If solution is less than or equal to 2 edges of the word
				// cannot be removed and therefore not a possible solution
				if (solution.length() > 2) {
					// Remove head/head edge of a word
					if (position == Position.HEAD || position == Position.EDGE) {
						solution = solution.substring(1);
					}

					// Remove tail/tail edge of a word
					if (position == Position.TAIL || position == Position.EDGE) {
						solution = solution.substring(0, solution.length() - 1);
					}

					// Check solution is in the dictionary and matches the
					// pattern
					if (DICTIONARY.isWord(solution) && pattern.match(solution)) {
						Solution s = new Solution(solution, NAME);
						// Create solution trace
						s.addToTrace(String.format(
								"Synonym is \"%s\", from clue word \"%s\"",
								synonym, entry.getKey()));
						s.addToTrace(String
								.format("Take the clue word \"%s\" and get its synonym \"%s\".",
										entry.getKey(), synonym));
						s.addToTrace(String
								.format("The clue contains the indicator \"%s\", which means to remove %s of \"%s\".",
										indicator, position.getText(), synonym));

						// Add the solution
						solutions.add(s);
					}
				}
			}
		}
		return solutions;
	}

	/**
	 * Filter synonyms which will not match the solution pattern when letters
	 * have been removed
	 * 
	 * @param synonyms
	 *            - synonyms to check
	 * @param pattern
	 *            - solution pattern to check against
	 */
	private void filterSynonyms(Map<String, Set<String>> synonyms,
			SolutionPattern pattern) {
		// This way rather than iterators to avoid
		// ConcurrentModificationExceptions :(
		Map<String, Set<String>> toRemove = new HashMap<>();
		for (Entry<String, Set<String>> entry : synonyms.entrySet()) {
			for (String synonym : entry.getValue()) {
				String[] syn = synonym.split(WordUtils.SPACE_AND_HYPHEN);
				if (synonym.length() >= pattern.getTotalLength() + 2
						&& synonym.length() <= pattern.getTotalLength()
						|| syn.length != 1) {
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
		testSolver(Deletion.class);
	}

} // End of class Deletion
