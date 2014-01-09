package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;

public class Homophone extends Solver {

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		new Thread(new Homophone()).start();
	}

	@Override
	public void run() {
		Homophone h = new Homophone();
		h.solve(new Clue("a singer sung a single note", "??????")); // Tenner
		h.solve(new Clue("A declared interest in meat", "?????")); // Steak
		h.solve(new Clue("Castle engaged in battle, reportedly", "????")); // Fort
	}

	public SolutionCollection solve(Clue c) {
		return null;
	}

} // End of class Homophone
