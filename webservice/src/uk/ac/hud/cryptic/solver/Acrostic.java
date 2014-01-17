package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Acrostic solver algorithm
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.1
 */
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

		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		// Number of clue words must be >= solution length
		if (c.getClueWords().length < pattern.getTotalLength()) {
			return solutions;
		}

		// Split the clue into array elements
		String[] words = c.getClueWords();

		String termToSearch = "";

		// Get first letters
		for (String w : words) {
			// TODO This will depend on indicator where first, nth or last
			termToSearch += w.substring(0, 1);
		}

		int solutionLength = pattern.getTotalLength();
		int lengthOfFodder = termToSearch.length();

		// Get all possible substrings
		for (int i = 0; i <= lengthOfFodder - solutionLength; i++) {
			String subStr = "";

			for (int j = i; j < i + solutionLength; j++) {
				subStr += termToSearch.substring(j, j + 1);
			}
			solutions.add(new Solution(subStr));
		}

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Remove words not in the dictionary
		DICTIONARY.dictionaryFilter(solutions, pattern);

		return solutions;
	}

} // End of class Acrostic
