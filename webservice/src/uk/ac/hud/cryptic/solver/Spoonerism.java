package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Thesaurus;

/**
 * Spoonerisms solver algorithm
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.2
 */
public class Spoonerism extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "spoonerism";

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
		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		// Get clue words
		String[] words = c.getClueWords();
		List<String> clueWords = new ArrayList<>(Arrays.asList(words));
		// Remove words with length less than or equal to 2
		removeShortWords(clueWords);

		// Find index of Spooner word
		String spoonerWord = null;
		for (String word : clueWords) {
			if (word.startsWith("spoon")) {
				spoonerWord = word;
				break;
			}
		}
		// If Spooner word not found, not a spoonerism clue, return solutions
		if (spoonerWord == null) {
			return solutions;
		}

		int spoonerPos = clueWords.indexOf(spoonerWord);

		// Get words next to Spooner word
		List<List<String>> fodder = getWordsToLeftAndRight(spoonerPos,
				clueWords);

		// Get max and min length of synonyms to get
		int synonymMinLength = 1;
		int synonymMaxLength = pattern.getTotalLength();

		// If pattern has multiple words
		if (pattern.hasMultipleWords()) {
			// Get individual word lengths
			int[] indWordLengths = pattern.getIndividualWordLengths();
			int longest = indWordLengths[0];
			// Find longest word and shortest word for minimum
			// and maximum synonym lengths
			for (int indWordLength : indWordLengths) {
				// If length is longer than the current longest
				if (indWordLength > longest) {
					// Set length to longest
					longest = indWordLength;
					synonymMaxLength = longest;
				}
				// If word is less than the minimum synoynm length
				if (indWordLength < synonymMinLength) {
					// Set length to shortest
					synonymMinLength = indWordLength;
				}
			}
		}

		// Get synonyms for fodder found in clue
		for (Collection<String> fod : fodder) {
			// List for synonyms
			Map<String, Collection<String>> synonymList = new HashMap<>();

			for (String word : fod) {
				// Get all synonyms for words
				Collection<String> synonyms = THESAURUS.getSecondSynonyms(word,
						synonymMaxLength, synonymMinLength, true);

				// Put synonyms into map
				synonymList.put(word, synonyms);
			}

			// Match up synonyms
			sortSynonyms(pattern, synonymList, solutions);
		}

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		// Filter solutions on solution pattern
		pattern.filterSolutions(solutions);

		return solutions;
	}

	/**
	 * Remove any words in the clue that have a length less than two
	 * 
	 * @param clueWords - clue words to check 
	 */
	private void removeShortWords(List<String> clueWords) {
		// Remove short words
		Iterator<String> it = clueWords.iterator();
		while (it.hasNext()) {
			String word = it.next();
			// If length of word is less than or equal to two
			if (word.length() <= 2) {
				// Remove it
				it.remove();
			}
		}
	}

	/**
	 * Get words to the left and the right of the indicator
	 * 
	 * @param spoonerPos - position of the indicator
	 * @param clueWords - clue words to check
	 * @return - list of fodder to find synonyms for
	 */
	private List<List<String>> getWordsToLeftAndRight(int spoonerPos,
			List<String> clueWords) {
		List<List<String>> fodder = new ArrayList<>();
		// To the left
		if (spoonerPos >= 2) {
			List<String> foundFodder = new ArrayList<>();
			// Get words in clue one or two positions to the left 
			foundFodder.add(clueWords.get(spoonerPos - 2));
			foundFodder.add(clueWords.get(spoonerPos - 1));
			// Add words to list
			fodder.add(foundFodder);
		}

		// To the right
		if (clueWords.size() - spoonerPos > 2) {
			List<String> foundFodder = new ArrayList<>();
			// Get words in clue one or two positions to the right
			foundFodder.add(clueWords.get(spoonerPos + 1));
			foundFodder.add(clueWords.get(spoonerPos + 2));
			// Add words to list
			fodder.add(foundFodder);
		}
		
		return fodder;
	}

	/**
	 * For all the synonyms that have been found, take two at a time
	 * and swap letters from each to find potential solutions
	 * 
	 * @param pattern - pattern to check against
	 * @param synonymList - list of synonyms found
	 * @param solutions - collection to add potential solutions to
	 */
	private void sortSynonyms(SolutionPattern pattern,
			Map<String, Collection<String>> synonymList,
			SolutionCollection solutions) {
		final int length = pattern.getTotalLength();

		// Loop through synonyms to find two synonyms that are not the
		// same word and swap their first one or two letters around
		for (Entry<String, Collection<String>> outer : synonymList.entrySet()) {
			for (Entry<String, Collection<String>> inner : synonymList
					.entrySet()) {
				// If synonyms being checked are not the same
				if (outer.equals(inner)) {
					continue;
				}

				for (String firstSynonym : outer.getValue()) {
					for (String secondSynonym : inner.getValue()) {
						// If length of first and second word is equal to the 
						// length of the pattern
						if (length == firstSynonym.length()
								+ secondSynonym.length()) {
							// Swap first one or two letters around
							swapFirstLetters(firstSynonym, secondSynonym,
									pattern, solutions);
						}
					}
				}
			}
		}

	}

	/**
	 * Swap first one or two letters from each word passed in and check
	 * whether they are potential solutions
	 * 
	 * @param firstWord - first word to swap letters from
	 * @param secondWord - second word to swap letters from
	 * @param pattern - pattern to check against
	 * @param solutions - collection to add potential solutions to
	 */
	private void swapFirstLetters(String firstWord, String secondWord,
			SolutionPattern pattern, SolutionCollection solutions) {
		// First word, first letter
		String fWfL = firstWord.substring(0, 1);
		// Second word, first letter
		String sWfL = secondWord.substring(0, 1);

		String swappedFirstWord = "";
		String swappedSecondWord = "";

		// DAMP CLOWN

		// Swap first two letters CAMP DLOWN
		swappedFirstWord = firstWord.replace(fWfL, sWfL);
		swappedSecondWord = secondWord.replace(sWfL, fWfL);
		checkIfWords(swappedFirstWord, swappedSecondWord, pattern, solutions);

		if (firstWord.length() > 2 && secondWord.length() > 2) {
			// Swap first two letters with second one letter DALOWN CMP
			swappedFirstWord = secondWord.substring(0, 1)
					+ firstWord.substring(2);
			swappedSecondWord = firstWord.substring(0, 2)
					+ secondWord.substring(1);
			checkIfWords(swappedFirstWord, swappedSecondWord, pattern,
					solutions);

			// Swap first one letter with second two letters DOWN CLAMP
			swappedFirstWord = secondWord.substring(0, 2)
					+ firstWord.substring(1);
			swappedSecondWord = firstWord.substring(0, 1)
					+ secondWord.substring(2);
			checkIfWords(swappedFirstWord, swappedSecondWord, pattern,
					solutions);

			// Swap first two letters with second two letters DAOWN CLMP
			swappedFirstWord = secondWord.substring(0, 2)
					+ firstWord.substring(2);
			swappedSecondWord = firstWord.substring(0, 2)
					+ secondWord.substring(2);
			checkIfWords(swappedFirstWord, swappedSecondWord, pattern,
					solutions);
		}
	}
	
	/**
	 * Check if the words passed together match the pattern or appear
	 * in the dictionary
	 * 
	 * @param firstWord - first word to check
	 * @param secondWord - second word to check
	 * @param pattern - pattern to check against
	 * @param solutions - collection to add potential solutions to
	 */
	private void checkIfWords(String firstWord, String secondWord,
			SolutionPattern pattern, SolutionCollection solutions) {

		// If pattern has multiple words, check that the words match the
		// pattern
		if (pattern.hasMultipleWords()) {
			if (pattern.match(firstWord + " " + secondWord)) {
				// Add solution with a space
				solutions.add(new Solution(firstWord + " " + secondWord, NAME));
			}
		} else {
			// If pattern does not have multiple words, check first word and
			// second word connected together is in the dictionary
			if (DICTIONARY.isWord(firstWord + secondWord)) {
				// Add solution without a space
				solutions.add(new Solution(firstWord + secondWord, NAME));
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
		testSolver(Spoonerism.class);
	}

} // End of class Spoonerism
