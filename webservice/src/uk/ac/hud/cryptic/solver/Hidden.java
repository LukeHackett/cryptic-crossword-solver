package uk.ac.hud.cryptic.solver;

import java.util.Arrays;
import java.util.Collection;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

public class Hidden extends Solver {

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Hidden(Clue clue) {
		super(clue);
	}

	/**
	 * Private (no-arg) constructor currently used to test the solver
	 */
	private Hidden() {
		super();
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		Hidden h = new Hidden();
		h.testSolver(h, Type.HIDDEN);
	}

	public SolutionCollection solve(Clue c) {

		SolutionCollection solutions = new SolutionCollection();

		// Clue length must be greater than solution length
		if (c.getClueNoPunctuation(true).length() <= c.getPattern()
				.getTotalLength()) {
			return solutions;
		}

		// Hidden words from left-to-right
		solutions.addAll(calculateHiddenWords(c, false));

		// Hidden words from right-to-left
		solutions.addAll(calculateHiddenWords(c, true));

		return solutions;
	}

	/**
	 * Search the clue in the direction specific for words which could be
	 * potential solutions
	 * 
	 * @param c
	 *            - the <code>Clue</code> in which to search for the solution
	 * @param reverse
	 *            <code>true</code> to search right-to-left and
	 *            <code>false</code> to search from left-to-right
	 * @return a collection of potential solutions which have been found
	 */
	private Collection<Solution> calculateHiddenWords(Clue c, boolean reverse) {

		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		String clue = c.getClueNoPunctuation(true);
		// Reverse the clue as searching will still be from left-to-right
		if (reverse) {
			clue = new StringBuilder(clue).reverse().toString();
		}

		int totalLength = pattern.getTotalLength();
		// How far into the clue to search before there is a lack of characters
		// available
		int limit = clue.length() - totalLength;

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			solutions.add(new Solution(clue.substring(index, index
					+ totalLength)));
		}

		// Remove risk of matching original words
		solutions.removeAllStrings(Arrays.asList(c.getClueWords()));

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		// Match against the thesaurus
		// for (String clueWord : strings) {
		// THESAURUS.match(c, clueWord);
		// }

		// TODO If a solution has been taken from the front of a word of the
		// clue (e.g. HELL from HELLO), reduce the probability as this doesn't
		// happen often

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		return solutions;
	}

	private void calculateConfidence() {
		// TODO If solution starts with a word of the clue (e.g. "capita" for
		// clue word "capital") reduce confidence.
		// TODO Increase confidence using thesaurus matching
	}

} // End of class Hidden
