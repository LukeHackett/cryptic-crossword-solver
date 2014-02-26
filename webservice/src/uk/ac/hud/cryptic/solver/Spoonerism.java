package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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

		String[] words = c.getClueWords();

		List<String> synonymList = new ArrayList<String>();

		int synonymMinLength = pattern.getTotalLength();
		int synonymMaxLength = pattern.getTotalLength();

		if (pattern.hasMultipleWords()) {
			int[] indWordLengths = pattern.getIndividualWordLengths();
			int longest = indWordLengths[0];
			for (int i = 0; i < indWordLengths.length; i++) {
				if (indWordLengths[i] > longest) {
					longest = indWordLengths[i];
					synonymMaxLength = longest;
				}
				if (indWordLengths[i] < synonymMinLength) {
					synonymMinLength = indWordLengths[i];
				}
			}
		}

		for (String word : words) {
			if (!word.startsWith("spoon")) {
				if (word.length() > 2) {
					// Get all synonyms for words
					Collection<String> synonyms = THESAURUS.getSecondSynonyms(
							word, synonymMaxLength, synonymMinLength, true);

					// Put synonyms into list
					synonymList.addAll(synonyms);
				}
			}
		}

		// Match up synonyms
		sortSynonyms(pattern, synonymList, solutions);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		pattern.filterSolutions(solutions);

		return solutions;
	}

	public void sortSynonyms(SolutionPattern pattern, List<String> synonymList,
			SolutionCollection solutions) {
		Iterator<String> synonym = synonymList.iterator();
		while (synonym.hasNext()) {
			String syn = synonym.next();
			for (String nextSyn : synonymList) {
				if (!syn.equals(nextSyn)) {
					if (pattern.getTotalLength() == (syn.length() + nextSyn
							.length())) {
						swapFirstLetters(syn, nextSyn, pattern, solutions);
					}
				}
			}
			synonym.remove();
		}
	}

	public void swapFirstLetters(String firstWord, String secondWord,
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

	public void checkIfWords(String firstWord, String secondWord,
			SolutionPattern pattern, SolutionCollection solutions) {
		boolean firstIsWord = DICTIONARY.isWord(firstWord);
		boolean secondIsWord = DICTIONARY.isWord(secondWord);

		if (firstIsWord && secondIsWord) {
			if (pattern.hasMultipleWords()) {
				if (pattern.match(firstWord + " " + secondWord)) {
					solutions.add(new Solution(firstWord + " " + secondWord,
							NAME));
				} else if (pattern.match(secondWord + " " + firstWord)) {
					solutions.add(new Solution(secondWord + " " + firstWord,
							NAME));
				}
			} else {
				if (DICTIONARY.isWord(firstWord + secondWord)) {
					solutions.add(new Solution(firstWord + secondWord, NAME));
				}
				if (DICTIONARY.isWord(secondWord + firstWord)) {
					solutions.add(new Solution(secondWord + firstWord, NAME));
				}
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
