package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;

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
		a.solve(new Clue("a singer sung a single note", "______")); // Tenner
	}

	public SolutionCollection solve(Clue c) {
		return null;
	}

} // End of class Anagram
