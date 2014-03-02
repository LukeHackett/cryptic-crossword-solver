package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Thesaurus;

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
	private SolutionCollection calculateHiddenWords(Clue c, boolean even) {
		final SolutionPattern pattern = c.getPattern();
		final String[] clueWords = c.getClueWords();
		String text = getEveryOtherChar(c, even);

		SolutionCollection solutions = new SolutionCollection();

		int totalLength = pattern.getTotalLength();
		int limit = text.length() - totalLength;

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			Solution s = new Solution(
					text.substring(index, index + totalLength), NAME);

			// First word used, to include in the trace
			String word = null;
			int wordIndex = 0;
			int cumulative = 0;
			do {
				String currentWord = clueWords[wordIndex++];
				if (index * 2 + (even ? 0 : 1) < currentWord.length()
						+ cumulative) {
					word = currentWord;
				} else {
					cumulative += currentWord.length();
				}
			} while (word == null);

			s.addToTrace("Every other character taken from the clue, starting with the clue word \""
					+ word + "\".");
			solutions.add(s);
		}

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

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

		// Even words
		solutions.addAll(calculateHiddenWords(c, true));

		// Odd words
		solutions.addAll(calculateHiddenWords(c, false));

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

} // End of class Pattern
