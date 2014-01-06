package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.resource.Thesaurus;
import uk.ac.hud.cryptic.util.SolutionStructure;
import uk.ac.hud.cryptic.util.WordUtils;

public class Pattern extends Solver {

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		new Thread(new Pattern()).start();
	}

	@Override
	public void run() {
		Pattern p = new Pattern();
		p.solve("'Pardon me!', I asked, regularly breaking vow", "7"); // Promise
		p.solve("Beasts in tree sinned, we hear - nothing odd there", "8"); // Reindeer
	}

	public Collection<String> solve(String clue, String solutionLength) {
		// Remove all non-alphabet characters
		String processedClue = WordUtils.removeNonAlphabet(clue, true);

		// TODO Clue length must be greater than or equal to solution length
		SolutionStructure ss = new SolutionStructure(solutionLength);

		String oddCharacters = getEveryOtherChar(processedClue, false);
		String evenCharacters = getEveryOtherChar(processedClue, true);

		// Even words
		Collection<String> evenWords = calculateHiddenWords(evenCharacters,
				solutionLength, ss);

		// Odd words
		Collection<String> oddWords = calculateHiddenWords(oddCharacters,
				solutionLength, ss);

		Collection<String> allWords = new ArrayList<>();
		allWords.addAll(evenWords);
		allWords.addAll(oddWords);

		// Temporary print block
		System.out.print(clue + ": ");
		for (String word : evenWords) {
			System.out.print("<E>" + ss.recomposeSolution(word) + ", ");
		}
		for (String word : oddWords) {
			System.out.print("<O>" + ss.recomposeSolution(word) + ", ");
		}
		System.out.println();
		String clueLower = WordUtils.removeNonAlphabet(clue, false);
		for (String clueComponent : clueLower.split("\\s")) {
			for (String possibleSolution : allWords) {
				if (Thesaurus.match(clueComponent, possibleSolution)) {
					System.out.println(possibleSolution
							+ " matches (synonym) with the clue word "
							+ clueComponent + ".");
				}
			}
		}
		System.out.println();
		return allWords;
	}

	private String getEveryOtherChar(String text, boolean even) {
		String newString = "";
		int i;
		for (i = even ? 0 : 1; i < text.length(); i += 2) {
			newString += text.charAt(i);
		}
		return newString;
	}

	private Collection<String> calculateHiddenWords(String clue,
			String solutionLength, SolutionStructure ss) {
		Collection<String> possibilities = new HashSet<>();

		int limit = clue.length() - ss.getTotalLength();

		// Generate substrings
		int index;
		for (index = 0; index <= limit; index++) {
			possibilities
					.add(clue.substring(index, index + ss.getTotalLength()));
		}

		// Filter out invalid words
		WordUtils.dictionaryFilter(possibilities, ss);

		// TODO Don't match words that aren't hidden, for example, the word
		// STEER in Steerer or ALLOW in Allows.

		// TODO Assign probabilities to each. This could try to use the
		// word definition component of the clue.

		return possibilities;
	}

} // End of class Pattern
