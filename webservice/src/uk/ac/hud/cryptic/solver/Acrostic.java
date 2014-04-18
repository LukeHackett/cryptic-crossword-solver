package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Thesaurus;

/**
 * Acrostic solver algorithm
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.1
 */
public class Acrostic extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "acrostic";

	/**
	 * Default constructor for solver class
	 */
	public Acrostic() {
		super();
	}

	/**
	 * Constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Acrostic(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {

		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		// Number of clue words must be >= solution length
		if (c.getClueWords().length < pattern.getTotalLength()) {
			return solutions;
		}

		// Split the clue into array elements
		String[] words = c.getClueWords();

		String termToSearch = "";

		// Get first letters
		for (String w : words) {
			// TODO This will depend on indicator where first, nth or last
			termToSearch += w.substring(0, 1);
		}

		int solutionLength = pattern.getTotalLength();
		int lengthOfFodder = termToSearch.length();

		// Get all possible substrings
		for (int i = 0; i <= lengthOfFodder - solutionLength; i++) {
			String subStr = "";

			for (int j = i; j < i + solutionLength; j++) {
				subStr += termToSearch.substring(j, j + 1);
			}
			Solution s = new Solution(subStr, NAME);
			s.addToTrace(String
					.format("Initial letters taken from clue words, starting with \"%s\", to clue word \"%s\".",
							words[i], words[i + solutionLength - 1]));
			solutions.add(s);
		}

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Remove words not in the dictionary
		DICTIONARY.dictionaryFilter(solutions, pattern);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		return solutions;
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
		testSolver(Acrostic.class);
	}

} // End of class Acrostic
