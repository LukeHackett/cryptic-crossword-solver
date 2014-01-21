package uk.ac.hud.cryptic.core;

import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Maintains a set (no duplicates) of potential solutions to a clue, which are
 * ordered by their associated confidence rating.
 * 
 * @author Stuart Leader
 * @version 0.1
 */
public class SolutionCollection extends HashSet<Solution> {

	private static final long serialVersionUID = -4860282004897560415L;

	/**
	 * Get all solutions with a confidence score greater than that which is
	 * specified
	 * 
	 * @param confidence
	 *            - the confidence score which solutions must exceed
	 * @return a <code>SolutionCollection</code> of matching solutions
	 */
	public SolutionCollection getSolutionsGreaterThan(int confidence) {
		// The new collection which will be returned
		SolutionCollection sc = new SolutionCollection();

		for (Solution s : this) {
			if (s.getConfidence() > confidence) {
				// Confidence score is greater, return it
				sc.add(s);
			}
		}
		return sc;
	}

	/**
	 * Get all solutions with a confidence score less than that which is
	 * specified
	 * 
	 * @param confidence
	 *            - the confidence score which solutions must not exceed
	 * @return a <code>SolutionCollection</code> of matching solutions
	 */
	public SolutionCollection getSolutionsLessThan(int confidence) {
		// The new collection which will be returned
		SolutionCollection sc = new SolutionCollection();

		for (Solution s : this) {
			if (s.getConfidence() < confidence) {
				// Confidence score is lower, return it
				sc.add(s);
			}
			// this is from when SolutionCollection was a TreeSet
			// else {
			// // As Collection is ordered, no more matches
			// break;
			// }
		}
		return sc;
	}

	/**
	 * Determine if the <code>SolutionCollection</code> contains the given
	 * solution string
	 * 
	 * @param solution
	 *            - the solution to check exists in the collection
	 * @return <code>true</code> if the solution is contained,
	 *         <code>false</code> otherwise
	 */
	public boolean contains(String solution) {
		for (Solution s : this) {
			// Standardise each solution of the collection
			String thisSolution = WordUtils.removeNonAlphabet(s.getSolution(),
					true);
			// Standardise the given solution
			String otherSolution = WordUtils.removeNonAlphabet(solution, true);
			// Check if they match!
			if (thisSolution.equals(otherSolution)) {
				return true;
			}
		}
		return false;
	}

	public void removeAllStrings(Collection<String> solutions) {
		for (String solution : solutions) {
			// Check if it is contained
			if (contains(solution)) {
				remove(new Solution(solution));
			}
		}
	}

	public Solution getBestSolution() {
		Solution best;

		// Populate the first solution (to be compared to)
		if (isEmpty()) {
			return null;
		} else {
			best = this.iterator().next();
		}

		// Check the rest
		for (Solution s : this) {
			// Check if it is contained
			if (s.getConfidence() > best.getConfidence()) {
				best = s;
			}
		}
		return best;
	}

} // End of class SolutionCollection
