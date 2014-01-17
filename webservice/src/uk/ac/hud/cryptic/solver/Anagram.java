package uk.ac.hud.cryptic.solver;

import java.util.Arrays;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Anagram solver algorithm
 * 
 * @author Stuart Leader
 * @version 0.1
 */
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

		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		String fodder = c.getClueNoPunctuation(true);

		// Clue length must be greater than solution length
		if (fodder.length() < c.getPattern().getTotalLength()) {
			return solutions;
		}

		// TODO Implement algorithm here

		// Remove risk of matching original words
		solutions.removeAllStrings(Arrays.asList(c.getClueWords()));

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		return solutions;
	}

} // End of class Anagram
