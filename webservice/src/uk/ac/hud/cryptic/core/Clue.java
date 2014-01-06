package uk.ac.hud.cryptic.core;

/**
 * Represents an individual cryptic crossword clue, and maintains a list of
 * possible solutions which have been calculated.
 * 
 * @author Stuart Leader
 * @version 0.1
 */
public class Clue {

	private final String clue;
	private final String pattern;
	private SolutionCollection solutions;

	public Clue(String clue, String pattern) {
		this.clue = clue;
		this.pattern = pattern;
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

	/**
	 * Get the solution's pattern as defined by the user. This is indicative of
	 * the solution's length, number of words, and any known characters.
	 * 
	 * @return the solution's pattern
	 */
	public String getPattern() {
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

} // End of class Clue
