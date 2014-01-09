package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.util.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

public class Hidden extends Solver {

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		new Thread(new Hidden()).start();
	}

	@Override
	public void run() {
		testSolver(this, Type.HIDDEN);
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
		strings.removeAll(Arrays.asList(c.getClueNoPunctuation(false).split(
				WordUtils.REGEX_WHITESPACE)));

		// Remove solutions which don't match the provided pattern
		Collection<String> toRemove = new ArrayList<>();
		for (String string : strings) {
			if (!pattern.match(string)) {
				toRemove.add(string);
			}
		}
		strings.removeAll(toRemove);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(strings, pattern);

		// Match against the thesaurus
		// for (String clueWord : strings) {
		// THESAURUS.match(c, clueWord);
		// }

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		for (String string : strings) {
			Solution s = new Solution(string);
			possibilities.add(s);
		}
		return possibilities;
	}

} // End of class Hidden
