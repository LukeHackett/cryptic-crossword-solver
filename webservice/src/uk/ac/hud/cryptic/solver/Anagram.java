package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Anagram solver algorithm
 * 
 * @author Stuart Leader, Leanne Butcher
 * @version 0.1
 */
public class Anagram extends Solver {

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(Anagram.class);
	}

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
	 * Default constructor for solver class
	 */
	public Anagram() {
		super();
	}

	/**
	 * Entry point to the main anagram algorithm. This method will return a list
	 * of all anagrams from the given input, which match with the specific
	 * pattern.
	 * 
	 * @param input
	 *            - a <code>String</code> of the available input characters.
	 *            Each of these must be used only once in the generation of
	 *            solutions.
	 * @param pattern
	 *            - The <code>SolutionPattern</code> to match the proposed
	 *            solutions against
	 * @return A collection of anagrams, if any, which apply to the given input
	 */
	private SolutionCollection anagram(String input, SolutionPattern pattern) {
		// Collection to be returned
		SolutionCollection anagrams = new SolutionCollection();
		// Break the solution pattern down into the separate words
		String[] wordPatterns = pattern.splitPattern();
		// Go find anagrams!
		anagram("", input, wordPatterns, anagrams);
		return anagrams;
	}

	/**
	 * Beware of recursive algorithms, "cos they play with your mind" - Leanne
	 * Butcher, 2014.
	 * 
	 * @param str
	 *            - an empty String please, which will act as the starting point
	 *            to anagram creation
	 * @param characters
	 *            - the pool of characters available to use, as a
	 *            <code>String</code>
	 * @param patterns
	 *            - an array of the solution patterns for each word of the
	 *            solution
	 * @param anagrams
	 */
	private void anagram(String str, String characters, String[] patterns,
			SolutionCollection anagrams) {
		// The base case. If there are no more patterns left, the last word of
		// the solution has been found
		if (patterns.length == 0) {
			// If you're here, a potential solution has been found
			anagrams.add(new Solution(str));
		} else {
			// Get all words matching the specified pattern
			Collection<String> words = DICTIONARY.getMatchingWords(patterns[0]);
			// Remove the current pattern from the front of the remaining
			// word patterns
			String[] remainingPatterns = Arrays.copyOfRange(patterns, 1,
					patterns.length);
			// For each matching word
			for (String word : words) {
				// Check if the characters are available to create this word
				if (WordUtils.hasCharacters(word, characters)) {
					// If so, remove these from the pool of available chars for
					// the next word - only if needed
					String remainingCharacters = characters;
					if (remainingPatterns.length > 0) {
						for (char c : word.toCharArray()) {
							remainingCharacters = remainingCharacters
									.replaceFirst(String.valueOf(c), "");
						}
					}
					// Round and around we go! Anyone else getting dizzy?
					anagram(str + word, remainingCharacters, remainingPatterns,
							anagrams);
				}
			}
		}
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
	 *            - the target character length of the complete solution
	 * @return a collection of all terms present in the clue which match up with
	 *         the length of the solution
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
					// TODO == is the general rule, not >=, but isn't set in
					// concrete. (UPDATE: Now I'm not so sure...)
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

	@Override
	public SolutionCollection solve(Clue c) {

		SolutionCollection solutions = new SolutionCollection();

		// Get clue pattern
		final SolutionPattern pattern = c.getPattern();
		// Get length of full answer
		int solutionLength = pattern.getTotalLength();

		// Get clue with no punctuation
		String condensedClue = c.getClueNoPunctuation(true);

		// Clue length must be greater than solution length
		if (condensedClue.length() < solutionLength) {
			return solutions;
		}

		// Get possible fodder
		Collection<String> possibleFodder = getPossibleFodder(c.getClueWords(),
				solutionLength);

		// For each potential fodder, find all potential solutions
		for (String characters : possibleFodder) {
			solutions.addAll(anagram(characters, pattern));
		}

		// Remove risk of matching original words
		solutions.removeAllStrings(Arrays.asList(c.getClueWords()));

		// Don't "pattern.filterSolutions() as it's handled by anagram()

		// Don't dictionary filter as it's handled by anagram()

		return solutions;
	}

	/**
	 * Get the database name for this type of clue
	 * 
	 * @return the database name for this type of clue
	 */
	@Override
	public String toString() {
		return "anagram";
	}

} // End of class Anagram
