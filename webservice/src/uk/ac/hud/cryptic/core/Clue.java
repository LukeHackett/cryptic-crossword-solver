package uk.ac.hud.cryptic.core;

import uk.ac.hud.cryptic.util.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Represents an individual cryptic crossword clue, and maintains a list of
 * possible solutions which have been calculated.
 * 
 * @author Stuart Leader
 * @version 0.1
 */
public class Clue {

	private final String clue;
	private final SolutionPattern pattern;
	private SolutionCollection solutions;

	public Clue(String clue, String pattern) {
		this.clue = clue.toLowerCase(); // Standardise clue
		this.pattern = new SolutionPattern(pattern);
		solutions = new SolutionCollection();
	}

	/**
	 * Retrieve the clue text
	 * 
	 * @return the clue text
	 */
	public String getClue() {
		return clue;
	}

	public String getClueNoPunctuation(boolean removeSpaces) {
		return WordUtils.removeNonAlphabet(clue, removeSpaces);
	}

	/**
	 * Get the solution's pattern as defined by the user. This is indicative of
	 * the solution's length, number of words, and any known characters.
	 * 
	 * @return the solution's pattern
	 */
	public SolutionPattern getPattern() {
		return pattern;
	}

	/**
	 * Get the set of potential solutions which have been calculated for this
	 * clue.
	 * 
	 * @return the set of potential solutions
	 */
	public SolutionCollection getSolutions() {
		return solutions;
	}

	/**
	 * Get the best solution for this clue that has been calculated. This is
	 * determined by the solution with the greatest confidence rating. If there
	 * is more than one clue with the same highest rating, only one of these
	 * will be returned.
	 * 
	 * @return the solution most likely to be correct
	 */
	public Solution getBestSolution() {
		return solutions.pollLast();
	}

} // End of class Clue
