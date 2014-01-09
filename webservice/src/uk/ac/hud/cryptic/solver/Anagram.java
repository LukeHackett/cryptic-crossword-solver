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
		a.solve(new Clue("Manger apt to be shown transformed by star", "?????????")); // pentagram
		a.solve(new Clue("Not straight, with future too unsettled", "???,??,????")); // out of true
	}

	public SolutionCollection solve(Clue c) {
		return null;
	}

} // End of class Anagram
