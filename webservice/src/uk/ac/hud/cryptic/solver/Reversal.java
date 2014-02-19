package uk.ac.hud.cryptic.solver;

import java.util.Set;

import uk.ac.hud.cryptic.solver.Solver;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Reversal solver algorithm, currently does not support NLP.
 * 
 * @author Luke Hackett
 * @version 0.2
 */
public class Reversal extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "reversal";

	// TODO once NLP has been added this can be removed
	private static String FODDER;

	/**
	 * Default constructor for the reversal solver class
	 */
	public Reversal() {
		super();
	}

	/**
	 * Constructor for the reversal solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Reversal(Clue clue) {
		super(clue);
	}

	/**
	 * This method solves the give clue returning a collection of possible
	 * solutions, if any.
	 * 
	 * @param c
	 *            - the clue to solve
	 * @return a <code>Collection</code> of potential solutions
	 */
	@Override
	public SolutionCollection solve(Clue c) {
		// TODO NLP
		// NLP would be used here to obtain the definition, fodder and
		// indicator. At this point only the fodder is really used, but
		// the definition could be used to but the fodder into perspective
		// e.g. definition character, therefore Beta as an answer gets it's
		// confidence rating boosted

		// Get the Fodder
		String fodder = FODDER;

		// Contains all the possible solutions to the clue
		SolutionCollection collection = new SolutionCollection();

		// Reverse the pattern
		String reversedPattern = WordUtils.reverseWord(c.getPattern()
				.getPattern());
		SolutionPattern pattern = new SolutionPattern(reversedPattern);

		// Get all synonyms that match the reversed pattern
		Set<String> synonyms = THESAURUS.getMatchingSynonyms(fodder, pattern);

		// Add the fodder as a synonym as it could contain the answer
		synonyms.add(fodder);

		// Reverse all synonyms to try to create another word
		for (String synonym : synonyms) {
			// Reverse the String
			String reversedWord = WordUtils.reverseWord(synonym);

			// Add as a solution if the reversed word is a real word
			if (DICTIONARY.isWord(reversedWord)) {
				collection.add(new Solution(reversedWord));
			}
		}

		return collection;
	}

	/**
	 * Get the database name for this type of clue
	 * 
	 * @return the database name for this type of clue
	 */
	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {

		Clue clue = new Clue("Put back trap item", "p???");
		FODDER = "trap";

		// Clue clue = new Clue("Sketcher went up to get reward", "??????");
		// FODDER = "reward";

		// Clue clue = new Clue("Devil from the east existed", "?????");
		// FODDER = "devil";

		// Solve the clue
		Reversal reversal = new Reversal(clue);
		SolutionCollection solutions = reversal.solve(clue);

		// Output the results
		System.out.println("Found " + solutions.size() + " solutions");
		for (Solution s : solutions) {
			System.out.println(s.getSolution());
		}

	}

}
