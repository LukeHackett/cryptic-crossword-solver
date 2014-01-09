package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;

public class Homophone extends Solver {

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Homophone(Clue clue) {
		super(clue);
	}

	/**
	 * Private (no-arg) constructor currently used to test the solver
	 */
	private Homophone() {
		super();
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		Homophone h = new Homophone();
		h.testSolver(h, Type.HOMOPHONE);
	}

	public SolutionCollection solve(Clue c) {
		SolutionCollection sc = new SolutionCollection();
		// Do the solving here
		return sc;
	}

} // End of class Homophone
