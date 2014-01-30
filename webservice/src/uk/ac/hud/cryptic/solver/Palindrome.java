package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Palindrome solver algorithm
 * 
 * @author Leanne Butcher
 * @version 0.1
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
			// synonyms.addAll(THESAURUS.getWordsContainingSynonym(clueWord));
			Set<String> synonyms = THESAURUS.getSecondSynonyms(clueWord,
					pattern, true);

			filterNonePalindromes(synonyms);

			for (String sol : synonyms) {
				solutions.add(new Solution(sol, NAME));
			}
		}

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		return solutions;
	}

	/**
	 * Remove words from proposed solutions which aren't actually palindromes
	 * 
	 * @param solutions
	 *            - the solutions to filter
	 */
	public void filterNonePalindromes(Collection<String> solutions) {
		// Iterate through
		for (Iterator<String> it = solutions.iterator(); it.hasNext();) {
			String solution = it.next();
			String normal = WordUtils.removeSpacesAndHyphens(solution);
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
