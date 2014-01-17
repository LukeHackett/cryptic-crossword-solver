package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.util.SolutionPattern;

public class Pattern extends Solver {

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
	 * Private (no-arg) constructor currently used to test the solver
	 */
	private Pattern() {
		super();
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		Pattern p = new Pattern();
		p.testSolver(p, Type.PATTERN);
	}

	public SolutionCollection solve(Clue c) {
		// TODO Clue length must be greater than or equal to solution length

		SolutionCollection sc = new SolutionCollection();

		String oddCharacters = getEveryOtherChar(c, false);
		String evenCharacters = getEveryOtherChar(c, true);

		// Even words
		sc.addAll(calculateHiddenWords(evenCharacters, c.getPattern()));

		// Odd words
		sc.addAll(calculateHiddenWords(oddCharacters, c.getPattern()));

		// for (String clueComponent : c.getClueWords()) {
		// for (String possibleSolution : allWords) {
		// if (Thesaurus.match(clueComponent, possibleSolution)) {
		// System.out.println(possibleSolution
		// + " matches (synonym) with the clue word "
		// + clueComponent + ".");
		// }
		// }
		// }

		return sc;
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

	private SolutionCollection calculateHiddenWords(String clue,
			SolutionPattern p) {

		SolutionCollection sc = new SolutionCollection();
		Collection<String> possibilities = new HashSet<>();

		int limit = clue.length() - p.getTotalLength();

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			possibilities
					.add(clue.substring(index, index + p.getTotalLength()));
		}

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(possibilities, p);

		// TODO Don't match words that aren't hidden, for example, the word
		// STEER in Steerer or ALLOW in Allows.

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		for (String string : possibilities) {
			Solution s = new Solution(string);
			sc.add(s);
		}
		return sc;
	}

} // End of class Pattern
