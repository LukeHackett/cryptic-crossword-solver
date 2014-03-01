package uk.ac.hud.cryptic.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.hud.cryptic.util.Confidence;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Maintains a set (no duplicates) of potential solutions to a clue, which are
 * ordered by their associated confidence rating.
 * 
 * @author Stuart Leader
 * @version 0.2
 */
public class SolutionCollection extends HashSet<Solution> {

	private static final long serialVersionUID = -4860282004897560415L;

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
		// Standardise the given solution
		String otherSolution = WordUtils.removeSpacesAndHyphens(solution);

		for (Solution s : this) {
			// Standardise each solution of the collection
			String thisSolution = WordUtils.removeSpacesAndHyphens(s
					.getSolution());

			// Check if they match!
			if (thisSolution.equals(otherSolution)) {
				return true;
			}
		}
		return false;
	}

	public Solution getBestSolution() {
		Solution best;

		// Populate the first solution (to be compared to)
		if (isEmpty()) {
			return null;
		} else {
			best = iterator().next();
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

	public void removeAllStrings(Collection<String> solutions) {
		for (String solution : solutions) {
			// Check if it is contained
			if (contains(solution)) {
				remove(new Solution(solution));
			}
		}
	}

	/**
	 * Return the set of solutions, sorted by their confidence ratings
	 * 
	 * @return a sorted set of solution, based on their confidence ratings
	 */
	public Set<Solution> sortSolutions() {
		// As simple as converting to a TreeSet
		return new TreeSet<>(this);
	}

	/**
	 * Get the Solution object which matches the passed String solution (if
	 * present)
	 * 
	 * @param solution
	 *            - a String representation of the Solution object to return
	 * @return the corresponding Solution object, <code>null</code> if not
	 *         present
	 */
	public Solution getSolution(String solution) {
		// Standardise the given solution
		String otherSolution = WordUtils.removeSpacesAndHyphens(solution);

		for (Solution s : this) {
			// Standardise each solution of the collection
			String thisSolution = WordUtils.removeSpacesAndHyphens(s
					.getSolution());

			// Check if they match!
			if (thisSolution.equals(otherSolution)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * Add a new solution to the collection. If an equivalent of this solution
	 * is already present, increase the confidence and add a message to the
	 * trace. If not, simply add it.
	 */
	@Override
	public boolean add(Solution s) {
		boolean added = false;

		// The solution text
		final String solutionText = s.getSolution();

		if (contains(s)) {
			// Solution already present. Make adjustment.
			Solution duplicateSolution = null;

			// Have to iterate
			for (Solution solution : this) {
				if (solutionText.equals(solution.getSolution())) {
					duplicateSolution = solution;
					break;
				}
			}

			// Don't accept the same solution from the same solver
			if (!s.getSolverType().equals(duplicateSolution.getSolverType())) {
				System.out.println("Duplicate solution: " + s);
				System.out.println("s solver type: " + s.getSolverType());
				System.out.println("dup solver type: "
						+ duplicateSolution.getSolverType());

				// Increase confidence and add message
				double confidence = Confidence.multiply(
						duplicateSolution.getConfidence(),
						Confidence.MULTI_SOLVER_MULTIPLIER);
				duplicateSolution.setConfidence(confidence);
				duplicateSolution
						.addToTrace("Confidence rating increased as this solution has also been found by the \""
								+ s.getSolverType() + "\" solver.");

				// Can return true to indicate solution has been added (kind of)
				added = true;
			}
		} else {
			// Solution not already contained in the set
			added = super.add(s);
		}
		return added;
	}

	/**
	 * Add all the given solutions to the set, following the rules explain in
	 * the <code>add()</code> method
	 * 
	 * @see SolutionCollection.add()
	 */
	@Override
	public boolean addAll(Collection<? extends Solution> c) {
		boolean changed = false;
		if (c != null && !c.isEmpty()) {
			for (Solution s : c) {
				boolean added = add(s);
				if (!changed && added) {
					changed = true;
				}
			}
		}
		return false;

	}

} // End of class SolutionCollection
