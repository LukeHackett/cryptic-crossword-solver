package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.List;

import uk.ac.hud.cryptic.util.SolutionStructure;
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
		a.solve("Some URLs recommended for beginners to explore online", "4");
		// Not solved because answer is HIJAB (not in dictionary)
		// a.solve("What chiefly hides in Jordan and Bahrain?", "5");
		a.solve("Those biting heads off tarantulas, eating even tiny hairs",
				"5");
		a.solve("What's seen at start of any road running one way?", "5");
		a.solve("Starts to serve time in Russian prison", "4");
		// Gives two answers BAWL and AWLS
		// TODO make sure definitions are taken into consideration
		a.solve("Black and white lamb starts to cry", "4");
	}

	public void solve(String clue, String solutionLength) {
		SolutionStructure ss = new SolutionStructure(solutionLength);

		// Split the clue into array elements
		String[] words = clue.split("\\s+");

		String termToSearch = "";

		// Get first letters
		for (String w : words) {
			// TODO This will depend on indicator where first, nth or last
			termToSearch += w.substring(0, 1);
		}

		int lengthOfClue = Integer.parseInt(solutionLength);
		int lengthOfFodder = termToSearch.length();

		List<String> possibleWords = new ArrayList<String>();

		// Get all possible substrings
		for (int i = 0; i <= lengthOfFodder - lengthOfClue; i++) {
			String subStr = "";

			for (int y = i; y < i + lengthOfClue; y++) {
				subStr += termToSearch.substring(y, y + 1);
			}

			possibleWords.add(subStr);
		}

		// Remove words not in the dictionary
		WordUtils.dictionaryFilter(possibleWords, ss);

		// TODO Remove - Print out answers
		for (String answer : possibleWords) {
			System.out.println(answer);
		}
	}
} // End of class Acrostic
