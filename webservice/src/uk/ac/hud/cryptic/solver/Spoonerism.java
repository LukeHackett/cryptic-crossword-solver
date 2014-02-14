package uk.ac.hud.cryptic.solver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.Collection;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Spoonerisms solver algorithm
 * 
 * @author Leanne Butcher
 * @version 0.2
 */
public class Spoonerism extends Solver {
	
	// A readable (and DB-valid) name for the solver
	private static final String NAME = "spoonerism";
	private Map<String, Collection<String>> synonymList;
	private SolutionCollection solutions;
	
	/**
	 * Default constructor for solver class
	 */
	public Spoonerism() {
		super();
	}

	/**
	 * Constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Spoonerism(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();
		
		String[] words = c.getClueWords();

		synonymList = new HashMap<>();
		
		for(String word : words) {
			if(!word.startsWith("Spoon")) {
				// Get all synonyms for words
				Set<String> synonyms = THESAURUS.getSynonyms(word);
				
				// Filter synonyms longer than length - not on pattern
				filterSynonyms(synonyms, pattern.getTotalLength());
				
				// Put synonyms into list
				synonymList.put(word, synonyms);
			}
		}
		
		// Match up synonyms
		sortSynonyms(pattern, c);
		
		return solutions;
	}
	
	public SolutionCollection sortSynonyms(SolutionPattern pattern, Clue clue)
	{		
		// Pair up all synonyms from words next to each other
		for(String word : clue.getClueWords()) {
			if(!word.startsWith("Spoon")) {
				Collection<String> synonyms = synonymList.get(word);
				
				for(String nextWords : clue.getClueWords()) {
					if(!nextWords.startsWith("Spoon") || (!nextWords.equals(word))) {
						Collection<String> nextSynonyms = synonymList.get(nextWords);
						
						matchSynonyms(synonyms, nextSynonyms, pattern);
					}
				}
			}
		}
		
		//DICTIONARY.dictionaryFilter(solutions, pattern);
		
		return solutions;
	}
	
	public SolutionCollection matchSynonyms(Collection<String> synonyms, Collection<String> nextSynonyms, SolutionPattern pattern)
	{
		for(String syn : synonyms) {
			for(String nextSyn : nextSynonyms)
			{
				if(pattern.match(syn.concat(nextSyn)) || (pattern.match(syn + " " + nextSyn))) {
					String possibleSpooner = swapFirstLetters(syn, nextSyn, pattern);
					solutions.add(new Solution(possibleSpooner));
				}
			}
		}
		return solutions;
	}
	
	public String swapFirstLetters(String firstWord, String secondWord, SolutionPattern pattern)
	{
		char firstLetter = firstWord.charAt(0);
		char secondLetter = secondWord.charAt(0);
		
		firstWord.replace(firstLetter, secondLetter);
		secondWord.replace(secondLetter, firstLetter);
		
		if(pattern.hasMultipleWords()) {
			return firstWord + " " + secondWord;
		} 
		
		return firstWord.concat(secondWord);
	}

	public Set<String> filterSynonyms(Set<String> synonyms, int totalSolutionLength)
	{
		for(Iterator<String> it = synonyms.iterator(); it.hasNext();) {
			if(it.next().length() > totalSolutionLength) {
				it.remove();
			}
		}
		
		return synonyms;
	}
	
	@Override
	public String toString() {
		return NAME;
	}
	
	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(Spoonerism.class);
	}
} // End of class Spoonerism 
