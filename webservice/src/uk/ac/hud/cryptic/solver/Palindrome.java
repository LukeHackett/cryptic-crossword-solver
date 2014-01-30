package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.Iterator;

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
	public SolutionCollection solve(Clue c)
	{
		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();
		
		String[] words = c.getClueWords();
		
		for(String clueWord : words)
		{
			Collection<String> synonyms = THESAURUS.getMatchingSynonyms(clueWord, pattern);
			synonyms.addAll(THESAURUS.getWordsContainingSynonym(clueWord));
			
			synonyms = filterNonePalindromes(synonyms);
			
			for(String sol : synonyms)
			{
				solutions.add(new Solution(sol));
			}
		}
		
		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);
				
		return solutions;
	}
	
	public Collection<String> filterNonePalindromes(Collection<String> solutions)
	{
		for(Iterator<String> it = solutions.iterator(); it.hasNext();)
		{
			String solution = it.next();
			String normal = WordUtils.removeSpacesAndHyphens(solution);
			String reverse = new StringBuilder(normal).reverse().toString();
			
			if(!normal.equals(reverse))
			{
				it.remove();
			}
		}
		
		return solutions;
	}

	@Override
	public String toString() {
		return NAME;
	}
}
