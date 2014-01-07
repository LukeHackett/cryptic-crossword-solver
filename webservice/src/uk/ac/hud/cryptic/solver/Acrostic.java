package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.util.WordUtils;

public class Acrostic extends Solver {

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		new Thread(new Acrostic()).start();
	}

	@Override
	public void run() {
		Acrostic a = new Acrostic();
		a.solve(new Clue(
				"Some URLs recommended for beginners to explore online", "____")); // surf
		a.solve(new Clue(
				"Those biting heads off tarantulas, eating even tiny hairs",
				"_____")); // teeth
		a.solve(new Clue("What's seen at start of any road running one way?",
				"_____")); // arrow
		a.solve(new Clue("Black and white lamb starts to cry", "____")); // bawl
	}

	public SolutionCollection solve(Clue c) {

		SolutionCollection sc = new SolutionCollection();

		// Split the clue into array elements
		String[] words = c.getClue().split("\\s+");

		String termToSearch = "";

		// Get first letters
		for (String w : words) {
			// TODO This will depend on indicator where first, nth or last
			termToSearch += w.substring(0, 1);
		}

		int lengthOfClue = c.getPattern().getTotalLength();
		int lengthOfFodder = termToSearch.length();

		Collection<String> possibleWords = new ArrayList<String>();

		// Get all possible substrings
		for (int i = 0; i <= lengthOfFodder - lengthOfClue; i++) {
			String subStr = "";

			for (int y = i; y < i + lengthOfClue; y++) {
				subStr += termToSearch.substring(y, y + 1);
			}

			possibleWords.add(subStr);
		}

		// Remove words not in the dictionary
		WordUtils.dictionaryFilter(possibleWords, c.getPattern());

		// TODO Remove - Print out answers
		for (String answer : possibleWords) {
			System.out.println(answer);
			sc.add(new Solution(answer));
		}
		return null;
	}
} // End of class Acrostic
