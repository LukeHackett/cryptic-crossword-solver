package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.util.WordUtils;

public class Thesaurus {
	// Thesaurus Instance
	private static Thesaurus instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();
	// Actual thesaurus data structure
	private Collection<Collection<String>> thesaurus = new HashSet<>();

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
		// BufferReader to read the file
		BufferedReader br;
		// Path to local dictionary path
		String thesaurusPath = settings.getThesaurusPath();

		try {
			br = new BufferedReader(new FileReader(thesaurusPath));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(",");

				Collection<String> entry = new ArrayList<>();
				for (String word : words) {
					entry.add(word.toLowerCase());
				}
				thesaurus.add(entry);
			}
			br.close();
		} catch (IOException e) {
			System.err.println("Exception in Thesaurus initialisation.");
		}
	}

	public int getMatchCount(Clue clue, String solution) {
		// Populate an array with the separate words of the clue
		String[] clueWords = clue.getClueNoPunctuation(false).split(
				WordUtils.REGEX_WHITESPACE);
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
	 * Check if a specified word is present in the dictionary being used.
	 * 
	 * @param word
	 *            - the word to check against the dictionary
	 * @return true if the dictionary contains the specified word, false
	 *         otherwise
	 */
	public boolean match(String clueWord, String solution) {
		solution = solution.toLowerCase();
		for (Collection<String> entry : thesaurus) {
			if (entry.contains(clueWord) && entry.contains(solution)) {
				return true;
			}
		}
		return false;
	}

	public Collection<String> getSynonyms(String word) {
		Collection<String> synonyms = new HashSet<>();
		for (Collection<String> entry : thesaurus) {
			if (entry.contains(word)) {
				synonyms.addAll(entry);
			}
		}
		synonyms.remove(word);
		return synonyms;
	}

} // End of class Thesaurus
