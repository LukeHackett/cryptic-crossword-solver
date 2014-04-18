package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Thesaurus;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Palindrome solver algorithm
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.2
 */
public class Palindrome extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "palindrome";

	/**
	 * Default constructor for solver class
	 */
	public Palindrome() {
		super();
	}

	/**
	 * Constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Palindrome(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		String[] words = c.getClueWords();

		for (String clueWord : words) {
			Set<String> synonyms = THESAURUS.getSecondSynonyms(clueWord,
					pattern, true);

			filterNonePalindromes(synonyms);

			for (String sol : synonyms) {
				Solution s = new Solution(sol, NAME);
				s.addToTrace(String.format(
						"This is a synonym of the clue word \"%s\".", clueWord));
				solutions.add(s);
			}
		}

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		return solutions;
	}

	/**
	 * Remove words from proposed solutions which aren't actually palindromes
	 * 
	 * @param solutions
	 *            - the solutions to filter
	 */
	private void filterNonePalindromes(Collection<String> solutions) {
		// Iterate through
		for (Iterator<String> it = solutions.iterator(); it.hasNext();) {
			String normal = WordUtils.removeSpacesAndHyphens(it.next());
			String reverse = new StringBuilder(normal).reverse().toString();

			// If the word isn't "symmetrical" from both sides, remove it
			if (!normal.equals(reverse)) {
				it.remove();
			}
		}
	}

	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(Palindrome.class);
	}

} // End of class Palindrome
