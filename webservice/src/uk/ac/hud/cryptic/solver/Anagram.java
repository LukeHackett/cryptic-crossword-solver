package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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

	// The potential solutions found
	private SolutionCollection solutions;

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
		// a.testSolver(a, Type.ANAGRAM);
		Clue c = new Clue("Manger apt to be shown transformed by star",
				"?????????");
		SolutionCollection sc = a.solve(c);
		for (Solution s : sc) {
			System.out.println(s);
		}
	}

	public SolutionCollection solve(Clue c) {
		// Solution collection
		solutions = new SolutionCollection();
		// Get clue pattern
		final SolutionPattern pattern = c.getPattern();
		// Get length of full answer
		int solutionLength = pattern.getTotalLength();

		// Get clue with no punctuation
		String fodder = c.getClueNoPunctuation(true);

		// Clue length must be greater than solution length
		if (fodder.length() < solutionLength) {
			return solutions;
		}

		// Get words in clue
		String[] words = c.getClueWords();

		// Get possible fodder
		Collection<String> possibleFodder = getPossibleFodder(words,
				solutionLength);

		// For each potential fodder, find all potential solutions
		for (String p : possibleFodder) {
			char[] fodderEntry = p.toCharArray();
			generateAnagrams(fodderEntry, 0);
		}

		// Remove risk of matching original words
		solutions.removeAllStrings(Arrays.asList(c.getClueWords()));

		// TODO Do we need this with the new method?
		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		return solutions;
	}

	/**
	 * The solution should be composed of all the characters from one (or more)
	 * words contained within the clue. In other words, the anagram solution
	 * cannot be formed from characters that have been picked and chosen across
	 * all the words of the clue. This method creates a list of possible
	 * substrings of the entire clue, which match up with the length of the
	 * solution.
	 * 
	 * @param words
	 *            - all the words present in the clue
	 * @param solutionLength
	 *            - the target length of the solution
	 * @return a collection of all terms present in the clue which match up with
	 *         the length of the soluton
	 */
	public Collection<String> getPossibleFodder(String[] words,
			int solutionLength) {

		// Structure for potential fodder
		Collection<String> possibleFodder = new ArrayList<>();

		// For each word of the given clue
		for (int i = 0; i < words.length; i++) {
			// If a single word makes up the length of the fodder
			if (words[i].length() == solutionLength) {
				// Add single word
				possibleFodder.add(words[i]);
			} else {
				// The length of the current word
				int length = words[i].length();
				// For each subsequent word to the current word
				for (int j = i + 1; j < words.length; j++) {
					// Add the length of the next word
					length += words[j].length();

					// If length is equal to the length of fodder (or just
					// above)
					// TODO == is the general rule, not >=, but isn't set in concrete
					if (length == solutionLength) {
						// Add words within potential fodder to list
						String possible = "";

						for (int x = i; x <= j; x++) {
							possible += words[x];
						}
						possibleFodder.add(possible);
						break;
					} else if (length > solutionLength) {
						// If length has exceeded, stop
						break;
					}
				}
			}
		}
		return possibleFodder;
	}

	public void swap(char[] fodderEntry, int pos1, int pos2) {
		// Swap array entries
		char temp = fodderEntry[pos1];
		fodderEntry[pos1] = fodderEntry[pos2];
		fodderEntry[pos2] = temp;
		// Add word to solution list
		String swapped = new String(fodderEntry);
		solutions.add(new Solution(swapped));
	}

	public void generateAnagrams(char[] fodderEntry, int start) {
		for (int i = start; i < fodderEntry.length; i++) {
			swap(fodderEntry, start, i);
			generateAnagrams(fodderEntry, start + 1);
			swap(fodderEntry, start, i);
		}
	}

} // End of class Anagram
