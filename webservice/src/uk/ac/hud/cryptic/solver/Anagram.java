package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;

public class Anagram extends Solver {

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Anagram(Clue clue) {
		super(clue);
	}

	/**
	 * Private (no-arg) constructor currently used to test the solver
	 */
	private Anagram() {
		super();
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		Anagram a = new Anagram();
		a.testSolver(a, Type.ANAGRAM);
	}

	public SolutionCollection solve(Clue c) {
		SolutionCollection sc = new SolutionCollection();
		// Do the solving here
		return sc;
	}

} // End of class Anagram
