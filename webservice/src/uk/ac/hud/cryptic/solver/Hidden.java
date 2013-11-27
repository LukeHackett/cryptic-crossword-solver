package uk.ac.hud.cryptic.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.util.SolutionStructure;
import uk.ac.hud.cryptic.util.WordUtils;

public class Hidden extends Solver {

	public static void main(String[] args) {
		new Thread(new Hidden()).start();
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	@Override
	public void run() {
		Hidden h = new Hidden();
		long start = System.currentTimeMillis();
		h.solve("Delia’s pickle contains jelly", "5");
		h.solve("As seen in jab, reach of pro miserably failing to meet expectations?",
				"6,2,7");
		h.solve("Some forget to get here for gathering", "3-8");
		h.solve("Guests in the country who use part – i.e. some, but not all",
				"5 7");
		h.solve("Composition from Bliss on a tape", "6");
		h.solve("What's in Latin sign, if I can translate, is of no importance",
				"13");
		h.solve("How some answers may be found in clues, some of which I'd denoted",
				"6");
		h.solve("In Fargo, rig amidst paper craft", "7");
		h.solve("Stop getting letters from friends", "3");
		h.solve("Some teachers get hurt", "4");
		h.solve("Metal concealed by environmentalist", "4");
		h.solve("Hide in Arthur's kingdom", "4");
		h.solve("Who means to reveal where the heart is?", "4");
		h.solve("Pole coming from Pakistan or Thailand", "5");
		h.solve("Motorcyclist perhaps steered irresponsibly when reversing? Not entirely",
				"5");
		h.solve("Cooking equipment taken back from heiress I tormented", "10");
		h.solve("Drama of mafioso a pope raised", "4,5");

		System.out.println("Time: " + (System.currentTimeMillis() - start)
				+ " ms");
	}

	public void solve(String clue, String solutionLength) {

		// Remove all non-alphabet characters
		String processedClue = WordUtils.removeNonAlphabet(clue, true);

		// TODO Clue length must be greater than or equal to solution length
		SolutionStructure ss = new SolutionStructure(solutionLength);

		// Hidden words from left-to-right
		Collection<String> forwardWords = calculateHiddenWords(processedClue,
				solutionLength, ss);

		// Hidden words from right-to-left
		String reverseClue = new StringBuilder(processedClue).reverse()
				.toString();
		Collection<String> backwardWords = calculateHiddenWords(reverseClue,
				solutionLength, ss);

		// Temporary print block
		System.out.print(clue + ": ");
		for (String word : forwardWords) {
			System.out.print("<F>" + ss.recomposeSolution(word) + ", ");
		}
		for (String word : backwardWords) {
			System.out.print("<B>" + ss.recomposeSolution(word) + ", ");
		}
		System.out.println();

	}

	private Collection<String> calculateHiddenWords(String clue,
			String solutionLength, SolutionStructure ss) {
		Collection<String> possibilities = new HashSet<>();

		int limit = clue.length() - ss.getTotalLength();

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			possibilities
					.add(clue.substring(index, index + ss.getTotalLength()));
		}

		// Filter out invalid words
		WordUtils.dictionaryFilter(possibilities, ss);

		// Remove risk of matching original words
		possibilities.removeAll(Arrays.asList(clue.toLowerCase().split(
				WordUtils.REGEX_SEPARATORS)));

		// TODO Don't match words that aren't hidden, for example, the word
		// STEER in Steerer or ALLOW in Allows.

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		return possibilities;
	}

} // End of class Hidden
