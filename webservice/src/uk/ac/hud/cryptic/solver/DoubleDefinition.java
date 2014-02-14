package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Palindrome solver algorithm
 * 
 * @author Leanne Butcher
 * @version 0.2
 */
public class DoubleDefinition extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "double definition";
	private SolutionCollection solutions;

	/**
	 * Default constructor for solver class
	 */
	public DoubleDefinition() {
		super();
	}

	/**
	 * Constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public DoubleDefinition(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		// Get all clue words
		String[] words = c.getClueWords();
		
		// List for all synonyms
		List<String> allSynonyms = new ArrayList<String>();

		for (String clueWord : words) {
			// Get second synonyms for each word
			Set<String> synonyms = THESAURUS.getSecondSynonyms(clueWord,
					pattern, true);
			// Add all synonyms to list
			allSynonyms.addAll(synonyms);
		}

		// Check for synonyms added to list twice
		checkForDoubles(allSynonyms, pattern);

		return solutions;
	}
	
	public void checkForDoubles(List<String> synonyms, SolutionPattern pattern)
	{
		// Set to check for synonyms that have already been added as possible solutions
		// meaning it is likely to be a double definition
	    Set<String> testSet = new HashSet<String>();
		
		for(String synonym : synonyms) {
			// If synonym is already in list, it's a synonym for two words in the clue
			if(!testSet.add(synonym)) {
			   // Add to solutions 
			   solutions.add(new Solution(synonym));
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
		testSolver(DoubleDefinition.class);
	}

} // End of class Double Definition