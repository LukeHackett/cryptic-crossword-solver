package uk.ac.hud.cryptic.solver;

import java.util.Collection;

public class Pattern extends Solver {

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		new Thread(new Pattern()).start();
	}

	@Override
	public void run() {
		Pattern p = new Pattern();
		p.solve("'Pardon me!', I asked, regularly breaking vow", "7"); // Promise
		p.solve("Beasts in tree sinned, we hear - nothing odd there", "8"); // Reindeer
	}

	public Collection<String> solve(String clue, String solutionLength) {
		return null;
	}

} // End of class Pattern
