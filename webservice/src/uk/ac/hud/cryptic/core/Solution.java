package uk.ac.hud.cryptic.core;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Represents a potential solution for a given clue.
 * 
 * @author Stuart Leader
 * @version 0.1
 */
public class Solution implements Comparable<Solution> {

	// Each generated solution will start with this confidence score
	private static final int DEFAULT_CONFIDENCE = 50;
	// A text representation of the solution
	private String solution;
	// The confidence score for this generated solution
	private int confidence;
	// A log of the steps involved to arrive at this solution
	private Collection<String> trace;

	/**
	 * Constructor where the solution string is passed in
	 * 
	 * @param solution
	 *            - the potential solution to the corresponding clue which is
	 *            represented by this class
	 */
	public Solution(String solution) {
		trace = new ArrayList<>();
		// Standardise all potential solutions
		this.solution = WordUtils.normaliseInput(solution, false);
		// Default confidence rating
		confidence = DEFAULT_CONFIDENCE;
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

	/**
	 * Return a human readable representation of <code>Solution</code>
	 */
	@Override
	public String toString() {
		return solution + " [" + confidence + "%]";
	}

	/**
	 * Allows <code>Solution</code>s which are held in a sorted collected to be
	 * sorted based on their confidence score. For example, those with a greater
	 * confidence score will be found at the top of the collection. This method
	 * override also needs to ensure that multiple instances of the same
	 * solution cannot exist.
	 */
	@Override
	public int compareTo(Solution o) {
		int solutionCompare = solution.compareTo(o.getSolution());
		int confidenceCompare = Double.compare(confidence, o.getConfidence());

		if (solutionCompare == 0) {
			// Compare the actual solution text. If they are the same, return
			// this!
			return 0;
		} else if (confidenceCompare == 0) {
			// If two solutions have the same confidence, return a comparison of
			// the solution text. This cannot be equal (0) at this point.
			return solutionCompare;
		} else {
			// Otherwise if the solutions and confidences of the two given
			// solutions are different, just compare them based on their
			// confidence.
			return confidenceCompare;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((solution == null) ? 0 : solution.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Solution other = (Solution) obj;
		if (solution == null) {
			if (other.solution != null)
				return false;
		} else if (!solution.equals(other.solution))
			return false;
		return true;
	}

} // End of class Solution
