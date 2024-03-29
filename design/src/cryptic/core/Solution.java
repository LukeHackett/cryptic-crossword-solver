package uk.ac.hud.cryptic.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import uk.ac.hud.cryptic.util.Confidence;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Represents a potential solution for a given clue.
 * 
 * @author Stuart Leader
 * @version 0.2
 */
public class Solution implements Comparable<Solution> {

	// Formatter for the confidence value
	public static DecimalFormat CONF_FORMATTER = new DecimalFormat("#0");
	// A text representation of the solution
	private String solution;
	// The confidence score for this generated solution
	private double confidence;
	// A log of the steps involved to arrive at this solution
	private List<String> trace;
	// The algorithm which generated this solution
	private String type;

	/**
	 * Constructor which simply takes the solution
	 * 
	 * @param solution
	 *            - the potential solution to the corresponding clue which is
	 *            represented by this class
	 */
	public Solution(String solution) {
		this(solution, "unspecified");
	}

	/**
	 * Constructor where the solution string is passed in
	 * 
	 * @param solution
	 *            - the potential solution to the corresponding clue which is
	 *            represented by this class
	 * @param type
	 *            - the type of algorithm that generated this Solution
	 */
	public Solution(String solution, String type) {
		this(solution, Confidence.INITIAL, type);
	}

	/**
	 * Constructor where the solution string is passed in and the confidence
	 * 
	 * @param solution
	 *            - the potential solution to the corresponding clue which is
	 *            represented by this class
	 * @param confidence
	 *            - the confidence rating of this solutuon on this clue
	 * @param type
	 *            - the type of algorithm that generated this Solution
	 */
	public Solution(String solution, double confidence, String type) {
		trace = new ArrayList<>();
		// Standardise all potential solutions
		this.solution = WordUtils.normaliseInput(solution, false);
		// Default confidence rating
		this.confidence = confidence;
		// Assign the solver type
		this.type = type;
	}

	/**
	 * Append an entry to the end of the trace's stack
	 * 
	 * @param traceEntry
	 *            - the item to append to the solution's trace stack
	 */
	public void addToTrace(String traceEntry) {
		if (traceEntry != null && !traceEntry.isEmpty()) {
			trace.add(traceEntry);
		}
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
		int confidenceCompare = -1
				* Double.compare(confidence, o.getConfidence());

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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Solution other = (Solution) obj;
		if (solution == null) {
			if (other.solution != null) {
				return false;
			}
		} else if (!solution.equals(other.solution)) {
			return false;
		}
		return true;
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
	 * Get the solution text
	 * 
	 * @return the solution text
	 */
	public String getSolution() {
		return solution;
	}

	/**
	 * Get this solution's stack trace, representing the process followed to
	 * arrive at this potential solution
	 * 
	 * @return the trace list for this solution
	 */
	public List<String> getSolutionTrace() {
		return trace;
	}

	/**
	 * Get the type of solver used to generate this solution (e.g. hidden)
	 * 
	 * @return the solver type
	 */
	public String getSolverType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (solution == null ? 0 : solution.hashCode());
		return result;
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
	public boolean setConfidence(double value) {
		boolean valid;
		if (valid = value >= 0 && value <= 100) {
			confidence = value;
		}
		return valid;
	}

	/**
	 * Return a human readable representation of <code>Solution</code>
	 */
	@Override
	public String toString() {
		return solution + " [" + CONF_FORMATTER.format(confidence) + "%]";
	}

} // End of class Solution
