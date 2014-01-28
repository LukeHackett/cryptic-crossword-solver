package uk.ac.hud.cryptic.core;

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
	private String actualSolution;
	private String type;
	private SolutionCollection solutions;

	/**
	 * Constructor for Clue. Takes the clue text and a pattern which corresponds
	 * to the format of the solution, along with any known characters.
	 * 
	 * @param clue
	 *            - the crossword clue text
	 * @param pattern
	 *            - the solution pattern
	 */
	public Clue(String clue, String pattern) {
		this.clue = clue.toLowerCase().trim(); // Standardise clue
		this.pattern = new SolutionPattern(pattern);
		solutions = new SolutionCollection();
	}

	/**
	 * For testing purposes, the solution to the clue may be provided. This
	 * allows generated solutions to be evaluated on whether they contain the
	 * actual solution.
	 * 
	 * @param clue
	 *            - the crossword clue text
	 * @param pattern
	 *            - the solution pattern
	 * @param solution
	 *            - the actual solution to the clue
	 * @param type
	 *            - the type of clue
	 */
	public Clue(String clue, String pattern, String solution, String type) {
		this(clue, pattern);
		actualSolution = solution.toLowerCase();
		this.type = type;
	}

	/**
	 * Retrieve the known solution of the clue
	 * 
	 * @return the solution to the clue
	 */
	public String getActualSolution() {
		return actualSolution;
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
		return solutions.getBestSolution();
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
	 * Obtain a clue with any punctuation removed. Spaces between the words may
	 * optionally be removed.
	 * 
	 * @param removeSpaces
	 *            - <code>true</code> if spaces should be removed from the clue,
	 *            <code>false</code> otherwise
	 * @return the clue with no punctuation
	 */
	public String getClueNoPunctuation(boolean removeSpaces) {
		return WordUtils.normaliseInput(clue, removeSpaces);
	}

	/**
	 * Get the clue as an array of its words
	 * 
	 * @return a <code>String</code> array of the words of the clue
	 */
	public String[] getClueWords() {
		// Remove unwanted characters
		String clue = getClueNoPunctuation(false).trim();
		// Split around whitespace
		return clue.split(WordUtils.REGEX_WHITESPACE);
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
	 * Get the type of clue this is (if available)
	 * 
	 * @return the type of clue (db friendly name)
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the actual solution to this clue
	 * @param actualSolution the actualSolution to set
	 */
	public void setActualSolution(String actualSolution) {
		this.actualSolution = actualSolution;
	}

	/**
	 * Set the Type of Clue this is
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Set the solutions this clue can have
	 * @param solutions the solutions to set
	 */
	public void setSolutions(SolutionCollection solutions) {
		this.solutions = solutions;
	}

} // End of class Clue
