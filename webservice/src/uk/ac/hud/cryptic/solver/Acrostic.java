package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;

public class Acrostic extends Solver {

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Acrostic(Clue clue) {
		super(clue);
	}

	/**
	 * Private (no-arg) constructor currently used to test the solver
	 */
	private Acrostic() {
		super();
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		Acrostic a = new Acrostic();
		a.testSolver(a, Type.ACROSTIC);
	}

	public SolutionCollection solve(Clue c) {

		SolutionCollection sc = new SolutionCollection();

		// Split the clue into array elements
		String[] words = c.getClueWords();

		String termToSearch = "";

		// Get first letters
		for (String w : words) {
			// TODO This will depend on indicator where first, nth or last
			termToSearch += w.substring(0, 1);
		}

		int lengthOfClue = c.getPattern().getTotalLength();
		int lengthOfFodder = termToSearch.length();

		Collection<String> possibleWords = new ArrayList<>();

		// Get all possible substrings
		for (int i = 0; i <= lengthOfFodder - lengthOfClue; i++) {
			String subStr = "";

			for (int y = i; y < i + lengthOfClue; y++) {
				subStr += termToSearch.substring(y, y + 1);
			}

			possibleWords.add(subStr);
		}

		// Remove words not in the dictionary
		DICTIONARY.dictionaryFilter(possibleWords, c.getPattern());

		// TODO Remove - Print out answers
		for (String answer : possibleWords) {
			// System.out.println(answer);
			sc.add(new Solution(answer));
		}
		return sc;
	}

} // End of class Acrostic
