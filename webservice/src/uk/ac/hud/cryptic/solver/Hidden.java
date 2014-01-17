package uk.ac.hud.cryptic.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

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
		// TODO Clue length must be greater than or equal to solution length

		SolutionCollection solutions = new SolutionCollection();

		// Hidden words from left-to-right
		solutions.addAll(calculateHiddenWords(c, false));

		// Hidden words from right-to-left
		solutions.addAll(calculateHiddenWords(c, true));

		return solutions;
	}

	private Collection<Solution> calculateHiddenWords(Clue c, boolean reverse) {

		Collection<String> strings = new HashSet<>();
		SolutionCollection possibilities = new SolutionCollection();
		SolutionPattern pattern = c.getPattern();

		String clue = c.getClueNoPunctuation(true);
		if (reverse) {
			clue = new StringBuilder(clue).reverse().toString();
		}

		int totalLength = pattern.getTotalLength();

		int limit = clue.length() - totalLength;

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			strings.add(clue.substring(index, index + totalLength));
		}

		// Remove risk of matching original words
		strings.removeAll(Arrays.asList(c.getClueWords()));

		// Remove solutions which don't match the provided pattern
		pattern.filterStrings(strings);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(strings, pattern);

		// Match against the thesaurus
		// for (String clueWord : strings) {
		// THESAURUS.match(c, clueWord);
		// }

		// TODO If a solution has been taken from the front of a word of the
		// clue (e.g. HELL from HELLO), reduce the probability as this doesn't
		// happen often

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		for (String string : strings) {
			Solution s = new Solution(string);
			possibilities.add(s);
		}
		return possibilities;
	}

	private void calculateConfidence() {
		// TODO If solution starts with a word of the clue (e.g. "capita" for
		// clue word "capital") reduce confidence.
		// TODO Increase confidence using thesaurus matching
	}

} // End of class Hidden
