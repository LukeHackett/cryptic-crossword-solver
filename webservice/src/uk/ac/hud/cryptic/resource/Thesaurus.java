package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.util.SolutionPattern;

public class Thesaurus {
	// Thesaurus Instance
	private static Thesaurus instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();
	// Actual thesaurus data structure
	private Collection<Collection<String>> thesaurus;

	/**
	 * Default Constructor
	 */
	private Thesaurus() {
		populateThesaurusFromFile();
	}

	/**
	 * This method will return the current (and only) instance of the Thesaurus
	 * object.
	 * 
	 * @return the thesaurus
	 */
	public static Thesaurus getInstance() {
		if (instance == null) {
			instance = new Thesaurus();
		}
		return instance;
	}

	/**
	 * Load the thesaurus into a HashSet to allow for much faster access
	 */
	private void populateThesaurusFromFile() {
		InputStream is = settings.getThesaurusPath();

		// Instantiate the thesaurus object
		thesaurus = new HashSet<>();

		// Try-with-resources. Readers are automatically closed after use
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line = null;
			// For each entry of the thesaurus
			while ((line = br.readLine()) != null) {
				// Separate the individual words
				String[] words = line.split(",");
				// Add words to a list
				Collection<String> entry = new ArrayList<>();
				for (String word : words) {
					entry.add(word.toLowerCase());
				}
				// And add them to the dictionary
				thesaurus.add(entry);
			}
		} catch (IOException e) {
			System.err.println("Exception in Thesaurus initialisation.");
		}
	}

	/**
	 * See how closely matched a clue is with a proposed solution by the number
	 * of entries in the thesaurus which contain both words. This may not be
	 * very useful in practice, but we'll see
	 * 
	 * @param clue
	 *            - the clue being solved
	 * @param solution
	 *            - the proposed solution string
	 * @return the number of entries which exist in the thesaurus containing
	 *         both words
	 */
	public int getMatchCount(Clue clue, String solution) {
		// Populate an array with the separate words of the clue
		String[] clueWords = clue.getClueWords();
		solution = solution.toLowerCase();
		// Number of thesaurus matches
		int count = 0;
		for (Collection<String> entry : thesaurus) {
			for (String word : clueWords) {
				if (entry.contains(word) && entry.contains(solution)) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Check if a given solution (String) matches as a synonym against any of
	 * the words present in the clue.
	 * 
	 * @param clue
	 *            - the <code>Clue</code> object
	 * @param solution
	 *            - the potential solution word
	 * @return <code>true</code> if the thesaurus contains the specified word,
	 *         <code>false</code> otherwise
	 */
	public boolean match(Clue clue, String solution) {
		// Populate an array with the separate words of the clue
		String[] clueWords = clue.getClueWords();
		SolutionPattern pattern = clue.getPattern();
		// More than one word in the solution?
		boolean multipleWords = pattern.hasMultipleWords();

		// Solution might need to be 're-spaced'
		String[] solutions = new String[multipleWords ? 2 : 1];
		solutions[0] = solution.toLowerCase();
		if (multipleWords) {
			solutions[1] = pattern.recomposeSolution(solution);
		}
		for (String clueWord : clueWords) {
			for (Collection<String> entry : thesaurus) {
				if ((entry.contains(clueWord) && entry.contains(solutions[0]))
						|| (multipleWords && entry.contains(clueWord) && entry
								.contains(solutions[1]))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Retrieve all synonyms of a given word
	 * 
	 * @param word
	 *            - the word to get synonyms for
	 * @return the synonyms of the given word
	 */
	public Collection<String> getSynonyms(String word) {
		// Use of HashSet prevents duplicates
		Collection<String> synonyms = new HashSet<>();
		for (Collection<String> entry : thesaurus) {
			if (entry.contains(word)) {
				synonyms.addAll(entry);
			}
		}
		// Remove the original word which was passed in (if present)
		synonyms.remove(word);
		return synonyms;
	}

} // End of class Thesaurus
