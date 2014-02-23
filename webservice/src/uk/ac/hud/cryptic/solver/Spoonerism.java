package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private List<String> synonymList;
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

		synonymList = new ArrayList<String>();

		for (String word : words) {
			if (!word.startsWith("Spoon")) {
				if (word.length() > 2) {
					// Get all synonyms for words
					Set<String> synonyms = THESAURUS.getSecondSynonyms(word,
							true);

					// Filter synonyms longer than length - not on pattern
					filterSynonyms(synonyms, pattern.getTotalLength());

					// Put synonyms into list
					synonymList.addAll(synonyms);
				}
			}
		}

		// Match up synonyms
		sortSynonyms(pattern, c);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		return solutions;
	}

	public SolutionCollection sortSynonyms(SolutionPattern pattern, Clue clue) {
		for (String synonym : synonymList) {
			for (String nextSyn : synonymList) {
				if (!synonym.equals(nextSyn)) {
					if (pattern.getTotalLength() == (synonym.length() + nextSyn
							.length())) {
						swapFirstLetters(synonym, nextSyn, pattern);
					}
				}
			}
		}

		return solutions;
	}

	public void matchSynonyms(Collection<String> synonyms,
			Collection<String> nextSynonyms, SolutionPattern pattern) {
		for (String syn : synonyms) {
			for (String nextSyn : nextSynonyms) {
				if (!syn.equals(nextSyn)) {
					if (pattern.getTotalLength() == (syn.length() + nextSyn
							.length())) {
						swapFirstLetters(syn, nextSyn, pattern);
					}
				}
			}
		}
	}

	public void swapFirstLetters(String firstWord, String secondWord,
			SolutionPattern pattern) {
		if (firstWord == "deep" || firstWord == "ship") {
			boolean here = true;
		}
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
		checkIfWords(swappedFirstWord, swappedSecondWord);

		if (firstWord.length() > 2 && secondWord.length() > 2) {
			// Swap first two letters with second one letter DALOWN CMP
			swappedFirstWord = secondWord.substring(0, 1)
					+ firstWord.substring(2);
			swappedSecondWord = firstWord.substring(0, 2)
					+ secondWord.substring(1);
			checkIfWords(swappedFirstWord, swappedSecondWord);

			// Swap first one letter with second two letters DOWN CLAMP
			swappedFirstWord = secondWord.substring(0, 2)
					+ firstWord.substring(1);
			swappedSecondWord = firstWord.substring(0, 1)
					+ secondWord.substring(2);
			checkIfWords(swappedFirstWord, swappedSecondWord);

			// Swap first two letters with second two letters DAOWN CLMP
			swappedFirstWord = secondWord.substring(0, 2)
					+ firstWord.substring(2);
			swappedSecondWord = firstWord.substring(0, 2)
					+ secondWord.substring(2);
			checkIfWords(swappedFirstWord, swappedSecondWord);
		}
	}

	public void checkIfWords(String firstWord, String secondWord) {
		boolean firstIsWord = DICTIONARY.isWord(firstWord);
		boolean secondIsWord = DICTIONARY.isWord(secondWord);

		if (firstIsWord && secondIsWord) {
			solutions.add(new Solution(firstWord + secondWord, NAME));
		}
	}

	public Set<String> filterSynonyms(Set<String> synonyms,
			int totalSolutionLength) {
		for (Iterator<String> it = synonyms.iterator(); it.hasNext();) {
			if (it.next().length() > totalSolutionLength) {
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
