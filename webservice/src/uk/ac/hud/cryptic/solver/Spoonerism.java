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
 * @author Leanne Butcher
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

		if (pattern.hasMultipleWords()) {
			int[] indWordLengths = pattern.getIndividualWordLengths();
			int longest = indWordLengths[0];
			for (int indWordLength : indWordLengths) {
				if (indWordLength > longest) {
					longest = indWordLength;
					synonymMaxLength = longest;
				}
				if (indWordLength < synonymMinLength) {
					synonymMinLength = indWordLength;
				}
			}
		}

		// List<List<String>> temp = new ArrayList<>();
		// temp.add(clueWords);

		for (Collection<String> fod : fodder) {

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

		pattern.filterSolutions(solutions);

		return solutions;
	}

	private void removeShortWords(List<String> clueWords) {
		// Remove short words
		Iterator<String> it = clueWords.iterator();
		while (it.hasNext()) {
			String word = it.next();
			if (word.length() <= 2) {
				it.remove();
			}
		}
	}

	private List<List<String>> getWordsToLeftAndRight(int spoonerPos,
			List<String> clueWords) {
		List<List<String>> fodder = new ArrayList<>();
		// To the left
		if (spoonerPos >= 2) {
			List<String> foundFodder = new ArrayList<>();
			foundFodder.add(clueWords.get(spoonerPos - 2));
			foundFodder.add(clueWords.get(spoonerPos - 1));
			fodder.add(foundFodder);
		}

		// To the right
		if (clueWords.size() - spoonerPos > 2) {
			List<String> foundFodder = new ArrayList<>();
			foundFodder.add(clueWords.get(spoonerPos + 1));
			foundFodder.add(clueWords.get(spoonerPos + 2));
			fodder.add(foundFodder);
		}

		return fodder;
	}

	private void sortSynonyms(SolutionPattern pattern,
			Map<String, Collection<String>> synonymList,
			SolutionCollection solutions) {
		final int length = pattern.getTotalLength();

		for (Entry<String, Collection<String>> outer : synonymList.entrySet()) {
			for (Entry<String, Collection<String>> inner : synonymList
					.entrySet()) {
				if (outer.equals(inner)) {
					continue;
				}

				for (String firstSynonym : outer.getValue()) {
					for (String secondSynonym : inner.getValue()) {
						if (length == firstSynonym.length()
								+ secondSynonym.length()) {
							swapFirstLetters(firstSynonym, secondSynonym,
									pattern, solutions);
						}
					}
				}
			}
		}

	}

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

	private void checkIfWords(String firstWord, String secondWord,
			SolutionPattern pattern, SolutionCollection solutions) {

		if (pattern.hasMultipleWords()) {
			if (pattern.match(firstWord + " " + secondWord)) {
				solutions.add(new Solution(firstWord + " " + secondWord, NAME));
			}
		} else {
			if (DICTIONARY.isWord(firstWord + secondWord)) {
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
		// Clue c = new Clue("immerse beasts spooner's ocean liner",
		// "?????,???",
		// "sheep dip", NAME);
		// CopyOfSpoonerism s = new CopyOfSpoonerism();
		// s.solve(c);
	}

} // End of class Spoonerism
