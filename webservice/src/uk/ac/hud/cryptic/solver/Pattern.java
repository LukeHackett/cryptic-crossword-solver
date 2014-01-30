package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Pattern solver algorithm
 * 
 * @author Stuart Leader
 * @version 0.1
 */
public class Pattern extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "pattern";

	/**
	 * Default constructor for solver class
	 */
	public Pattern() {
		super();
	}

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Pattern(Clue clue) {
		super(clue);
	}

	/**
	 * Search in the provided text for words which match against the dictionary.
	 * 
	 * @param text
	 *            - the text in which to search for words
	 * @param pattern
	 *            - the <code>SolutionPattern</code> to match against
	 * @return a collection of words which match against the solution pattern
	 */
	private SolutionCollection calculateHiddenWords(String text,
			SolutionPattern pattern) {

		SolutionCollection solutions = new SolutionCollection();

		int limit = text.length() - pattern.getTotalLength();

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			solutions.add(new Solution(text.substring(index,
					index + pattern.getTotalLength()), NAME));
		}

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		// TODO Don't match words that aren't hidden, for example, the word
		// STEER in Steerer or ALLOW in Allows.

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		return solutions;
	}

	private String getEveryOtherChar(Clue c, boolean even) {
		final String text = c.getClueNoPunctuation(true);
		String newString = "";
		int i;
		for (i = even ? 0 : 1; i < text.length(); i += 2) {
			newString += text.charAt(i);
		}
		return newString;
	}

	@Override
	public SolutionCollection solve(Clue c) {
		SolutionCollection solutions = new SolutionCollection();

		// (Clue length / 2) must be greater than solution length
		int maxLength = (int) Math.ceil((double) c.getClueNoPunctuation(true)
				.length() / 2);
		if (c.getPattern().getTotalLength() > maxLength) {
			return solutions;
		}

		String oddCharacters = getEveryOtherChar(c, false);
		String evenCharacters = getEveryOtherChar(c, true);

		// Even words
		solutions.addAll(calculateHiddenWords(evenCharacters, c.getPattern()));

		// Odd words
		solutions.addAll(calculateHiddenWords(oddCharacters, c.getPattern()));

		// for (String clueComponent : c.getClueWords()) {
		// for (String possibleSolution : allWords) {
		// if (Thesaurus.match(clueComponent, possibleSolution)) {
		// System.out.println(possibleSolution
		// + " matches (synonym) with the clue word "
		// + clueComponent + ".");
		// }
		// }
		// }

		return solutions;
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
		testSolver(Pattern.class);
	}

} // End of class Pattern
