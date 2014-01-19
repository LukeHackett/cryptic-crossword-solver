package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Anagram solver algorithm
 * 
 * @author Stuart Leader, Leanne Butcher
 * @version 0.1
 */
public class Anagram extends Solver {

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Anagram(Clue clue) {
		super(clue);
	}

	/**
	 * Private (no-arg) constructor currently used to test the solver
	 */
	private Anagram() {
		super();
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		Anagram a = new Anagram();
		a.testSolver(a, Type.ANAGRAM);
	}
	
	private SolutionCollection solutions;

	public SolutionCollection solve(Clue c) {
		// Solution collection
		solutions = new SolutionCollection();
		// Get clue pattern
		final SolutionPattern pattern = c.getPattern();
		
		// Get clue with no punctuation
		String fodder = c.getClueNoPunctuation(true);

		// Clue length must be greater than solution length
		if (fodder.length() < c.getPattern().getTotalLength()) {
			return solutions;
		}
		
		// Get length of full answer
		int lengthOfFodder = c.getPattern().getTotalLength();
		
		// Get words in clue
		String[] words = c.getClueWords();
		// Get length of words in clue
		int[] wordLengths = new int[words.length];
		
		// Get possible fodder
		ArrayList<String> possibleFodder = getPossibleFodder(words, wordLengths, lengthOfFodder);
		
		// For each potential fodder, find all potential solutions
		for(String p : possibleFodder)
		{
			char[] fodderEntry = p.toCharArray();
			findPotential(fodderEntry, 0);
		}
		
		// Remove risk of matching original words
		solutions.removeAllStrings(Arrays.asList(c.getClueWords()));
		
		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);	

		return solutions;
	}
	
	public ArrayList<String> getPossibleFodder(String[] words, int[] wordLengths, int lengthOfFodder)
	{	
		// Fill array with word lengths
		for(int i = 0; i < words.length; i++)
		{
			wordLengths[i] = words[i].length();
		}
				
		// Structure for potential fodder
		ArrayList<String> possibleFodder = new ArrayList<String>();
				
		// Find word lengths which add up to clue answer
		for(int i = 0; i < wordLengths.length; i++)
		{	
			int length = wordLengths[i];
					
			for(int j = i + 1; j < wordLengths.length; j++)
			{	
				// If a single word makes up the length of the fodder
				if(words[i].length() == lengthOfFodder)
				{
					// Add single word
					possibleFodder.add(words[i]);
				}
				else
				{
					// Increment length of potential fodder by next word
					length += wordLengths[j];
						
					// If length is equal to the length of fodder
					if(length == lengthOfFodder)
					{
						// Add words within potential fodder to list
						String possible = "";
								
						for(int x = i; x <= j; x++)
						{
						possible += words[x];
						}		
						possibleFodder.add(possible);
					}
					// If length has exceeded, stop
					else if(length > lengthOfFodder)
					{
						break;
					}
				}
			}
		}
		return possibleFodder;
	}
	
	public void swap(char[] fodderEntry, int pos1, int pos2)
	{
		// Swap array entries
		char temp = fodderEntry[pos1];
		fodderEntry[pos1] = fodderEntry[pos2];
		fodderEntry[pos2] = temp;
		// Add word to solution list
		String swapped = new String(fodderEntry);
		solutions.add(new Solution(swapped));
	}
	
	public void findPotential(char[] fodderEntry, int start) 
	{	
	      for (int i = start; i < fodderEntry.length; i++)
	      {
	         swap(fodderEntry, start, i);
	         findPotential(fodderEntry, start + 1);
	         swap(fodderEntry, start, i);
	      }
	}

} // End of class Anagram
