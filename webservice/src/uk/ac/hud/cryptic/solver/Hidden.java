package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
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
		Hidden h = new Hidden();
		h.solve(new Clue("Delia’s pickle contains jelly", "_____"));
		h.solve(new Clue(
				"As seen in jab, reach of pro miserably failing to meet expectations?",
				"_____,__,_______"));
		h.solve(new Clue("Some forget to get here for gathering",
				"___-________"));
		h.solve(new Clue(
				"Guests in the country who use part – i.e. some, but not all",
				"_____,_______"));
	}

	public SolutionCollection solve(Clue c) {

		// TODO Clue length must be greater than or equal to solution length

		SolutionCollection solutions = new SolutionCollection();

		// Hidden words from left-to-right
		solutions.addAll(calculateHiddenWords(c, false));

		// Hidden words from right-to-left
		solutions.addAll(calculateHiddenWords(c, true));

		// Temporary print block
		System.out.print(c.getClue() + ": ");
		for (Solution s : solutions) {
			System.out.print(s + ", ");
		}
		System.out.println();

		return solutions;

	}

	private Collection<Solution> calculateHiddenWords(Clue c, boolean reverse) {

		Collection<String> strings = new HashSet<>();
		SolutionCollection possibilities = new SolutionCollection();

		String clue = c.getClueNoPunctuation(true);
		if (reverse) {
			clue = new StringBuilder(clue).reverse().toString();
		}

		int totalLength = c.getPattern().getTotalLength();

		int limit = clue.length() - totalLength;

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			strings.add(clue.substring(index, index + totalLength));
		}

		// Filter out invalid words
		WordUtils.dictionaryFilter(strings, c.getPattern());

		// Remove risk of matching original words
		// possibilities.removeAll(Arrays.asList(clue.toLowerCase().split(
		// WordUtils.REGEX_SEPARATORS)));

		// TODO Don't match words that aren't hidden, for example, the word
		// STEER in Steerer or ALLOW in Allows.

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		for (String string : strings) {
			Solution s = new Solution(string);
			possibilities.add(s);
		}
		return possibilities;
	}
} // End of class Hidden
