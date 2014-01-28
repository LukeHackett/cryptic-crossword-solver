package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

public class Thesaurus {
	// Thesaurus Instance
	private static Thesaurus instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();

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

	// Actual thesaurus data structure
	private Map<String, Collection<String>> thesaurus;

	/**
	 * Default Constructor
	 */
	private Thesaurus() {
		populateThesaurusFromFile();
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
		synonyms.addAll(thesaurus.get(word));
		// Remove the original word which was passed in (if present)
		synonyms.remove(word);
		return synonyms;
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
			if (thesaurus.containsKey(clueWord)) {
				Collection<String> synonyms = thesaurus.get(clueWord);
				if (synonyms.contains(solutions[0])
						|| (multipleWords && synonyms.contains(solutions[1]))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Load the thesaurus into a HashSet to allow for much faster access
	 */
	private void populateThesaurusFromFile() {
		InputStream is = settings.getThesaurusPath();

		// Instantiate the thesaurus object
		thesaurus = new HashMap<>();

		// Try-with-resources. Readers are automatically closed after use
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line = null;
			// For each entry of the thesaurus
			while ((line = br.readLine()) != null) {
				// Separate the individual words
				String[] words = line.split(",");
				// Get the key (look-up) word
				String lookupWord = words[0];
				// Rest of the words are synonyms
				words = Arrays.copyOfRange(words, 1, words.length);
				// Add words to a list
				Collection<String> entry = new ArrayList<>();
				for (String word : words) {
					entry.add(word.toLowerCase());
				}
				// And add them to the dictionary
				thesaurus.put(lookupWord, entry);
			}
		} catch (IOException e) {
			System.err.println("Exception in Thesaurus initialisation.");
		}
	}

	/**
	 * Retrieve specific synonyms of a given word
	 * 
	 * @param word
	 *            - the word to get synonyms for
	 * @param pattern
	 *            - the pattern the synonyms should match against
	 * @return the synonyms of the given word
	 */
	public Collection<String> getMatchingSynonyms(String word,
			SolutionPattern pattern) {
		// Use of HashSet prevents duplicates
		Collection<String> matchingSynonyms = new HashSet<>();

		int lengthOfSolution = pattern.getTotalLength();
		int numOfWords = pattern.getNumberOfWords();

		String[] knownChars = pattern.getKnownCharacters();

		// Get synonyms
		Collection<String> synonyms = thesaurus.get(word);

		if (synonyms != null) {
			for (String entry : synonyms) {
				String[] words = entry.split(" ");
				String singleWordEntry = entry.replaceAll(" ", "");
				if ((singleWordEntry.length() == lengthOfSolution)
						&& (numOfWords == words.length)
						&& (WordUtils.charactersPresentInWord(entry, knownChars))
						&& (pattern.match(entry))) {
					matchingSynonyms.add(entry);
				}
			}
		}
		// Remove the original word which was passed in (if present)
		matchingSynonyms.remove(word);
		return matchingSynonyms;
	}

	/**
	 * Get the words for which the passed parameter is a synonym of
	 * 
	 * @param synonym
	 *            - the synonym that should be present in the list of synonyms
	 *            of the words returned
	 * @return a list of words which contain the passed word as a synonym
	 */
	public Collection<String> getWordsContainingSynonym(String synonym) {
		// The collection that will be returned
		Collection<String> results = new HashSet<>();

		// Have to go through the entire thesaurus
		for (Entry<String, Collection<String>> entry : thesaurus.entrySet()) {
			// If an entry contains the specified word as a synonym
			if (entry.getValue().contains(synonym)) {
				// Return it
				results.add(entry.getKey());
			}
		}
		return results;
	}

} // End of class Thesaurus
