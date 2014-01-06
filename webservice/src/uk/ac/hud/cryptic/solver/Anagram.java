package uk.ac.hud.cryptic.solver;

import java.util.Collection;

public class Anagram extends Solver {

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		new Thread(new Anagram()).start();
	}

	@Override
	public void run() {
		Anagram a = new Anagram();
		a.solve("a singer sung a single note", "6"); // Tenner
	}

	public Collection<String> solve(String clue, String solutionLength) {
		return null;
	}

} // End of class Anagram