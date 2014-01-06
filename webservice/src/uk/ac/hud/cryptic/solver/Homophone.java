package uk.ac.hud.cryptic.solver;

import java.util.Collection;

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
		h.solve("a singer sung a single note", "6"); // Tenner
		h.solve("A declared interest in meat", "5"); // Steak
		h.solve("Castle engaged in battle, reportedly", "4"); // Fort
	}

	public Collection<String> solve(String clue, String solutionLength) {
		return null;
	}

} // End of class Homophone
