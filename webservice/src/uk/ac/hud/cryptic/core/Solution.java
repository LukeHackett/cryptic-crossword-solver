package uk.ac.hud.cryptic.core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a potential solution for a given clue.
 * 
 * @author Stuart Leader
 * @version 0.1
 */
public class Solution implements Comparable<Solution> {

	private String solution;
	private int confidence;
	private Collection<String> trace;

	/**
	 * Default constructor
	 */
	public Solution() {
		trace = new ArrayList<>();
	}

	/**
	 * Constructor where the solution string is passed in
	 * 
	 * @param solution
	 *            - the potential solution to the corresponding clue which is
	 *            represented by this class
	 */
	public Solution(String solution) {
		this();
		this.solution = solution;
	}

	/**
	 * Append an entry to the end of the trace's stack
	 * 
	 * @param traceEntry
	 *            - the item to append to the solution's trace stack
	 */
	public void addToTrace(String traceEntry) {
		trace.add(traceEntry);
	}

	/**
	 * Get this solution's stack trace, representing the process followed to
	 * arrive at this potential solution
	 * 
	 * @return the trace list for this solution
	 */
	public Collection<String> getSolutionTrace() {
		return trace;
	}

	/**
	 * Get the solution text
	 * 
	 * @return the solution text
	 */
	public String getSolution() {
		return solution;
	}

	/**
	 * Get the confidence score associated with this potential solution
	 * 
	 * @return the confidence score of this solution
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * Set the confidence value for this solution. This must reside between 0 -
	 * 100 inclusive.
	 * 
	 * @param value
	 *            - the confidence rating
	 * @return <code>true</code> if successfully assigned, <code>false</code>
	 *         otherwise
	 */
	public boolean setConfidence(int value) {
		boolean valid;
		if (valid = (value >= 0 && value <= 100)) {
			confidence = value;
		}
		return valid;
	}

	@Override
	public String toString() {
		return solution + " [" + confidence + "%]";
	}

	@Override
	public int compareTo(Solution o) {
		int solutionCompare = solution.compareToIgnoreCase(o.getSolution());
		int confidenceCompare = Double.compare(confidence, o.getConfidence());

		if (solutionCompare == 0) {
			return 0;
		} else if (confidenceCompare == 0) {
			return solutionCompare;
		} else {
			return confidenceCompare;
		}
	}
} // End of class Solution
