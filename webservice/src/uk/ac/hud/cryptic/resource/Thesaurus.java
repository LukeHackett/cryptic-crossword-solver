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
import java.util.Set;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.Cache;
import uk.ac.hud.cryptic.util.Confidence;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * An interface to the thesaurus file(s)
 * 
 * @author Stuart Leader, Leanne Butcher
 * @version 0.2
 */
public class Thesaurus {
	// Thesaurus Instance
	private static Thesaurus instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();

	// Actual thesaurus data structure
	private Map<String, Collection<String>> thesaurus;

	// Cache to speed up common requests
	private Cache<String, Collection<String>> cache;

	/**
	 * Default Constructor
	 */
	private Thesaurus() {
		populateThesaurusFromFile();
		cache = new Cache<>();
	}

	/**
	 * Load the thesaurus into a HashSet to allow for much faster access
	 */
	private void populateThesaurusFromFile() {
		InputStream is = settings.getThesaurusStream();

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
					if (Dictionary.getInstance().areWords(word)) {
						entry.add(word.toLowerCase());
					}
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
	public Set<String> getMatchingSynonyms(String word, SolutionPattern pattern) {
		// Use of HashSet prevents duplicates
		Set<String> matchingSynonyms = new HashSet<>();

		if (thesaurus.containsKey(word)) {
			// Get synonyms
			Collection<String> synonyms = thesaurus.get(word);
			for (String entry : synonyms) {
				// Synonym must match specified pattern
				if (pattern.match(entry)) {
					// Match the word lengths
					if (WordUtils.wordLengthMatch(entry, pattern)) {
						// Add as a synonym if there is a match
						matchingSynonyms.add(entry);
					}
				}
			}
		}

		// Remove the original word which was passed in (if present)
		matchingSynonyms.remove(word);
		return matchingSynonyms;
	}

	/**
	 * Obtain a list of "synonyms of a word's synonyms" to increase the chances
	 * of finding the correct solution. These must match against a supplied
	 * pattern
	 * 
	 * @param word
	 *            - the word to find two levels of synonyms for
	 * @param pattern
	 *            - the pattern the synonyms should match against
	 * @param includeFirstLevel
	 *            - <code>true</code> to return the first level synonyms also,
	 *            <code>false</code> to only return the second level synonyms
	 * @return a set of synonyms of a word's synonyms
	 */
	public Set<String> getSecondSynonyms(String word, SolutionPattern pattern,
			boolean includeFirstLevel) {
		// Go and fetch the first level synonyms
		Set<String> firstLevelSynonyms = getSynonyms(word);
		// This will hold the results of this method
		Set<String> secondLevelSynonyms = new HashSet<>();

		// Include first level synonyms if requested
		if (includeFirstLevel) {
			// But only those that match the specified pattern
			Set<String> matchingFirstLevel = getMatchingSynonyms(word, pattern);
			secondLevelSynonyms.addAll(matchingFirstLevel);
		}

		// Get the synonyms for each first level synonym
		for (String synonym : firstLevelSynonyms) {
			// The second level synonyms for this first level synonym
			Set<String> newSynonyms = getMatchingSynonyms(synonym, pattern);
			secondLevelSynonyms.addAll(newSynonyms);
		}

		return secondLevelSynonyms;
	}

	/**
	 * Retrieve all synonyms of a given word
	 * 
	 * @param word
	 *            - the word to get synonyms for
	 * @return the synonyms of the given word
	 */
	public Set<String> getSynonyms(String word) {
		// Use of HashSet prevents duplicates
		Set<String> synonyms = new HashSet<>();
		if (thesaurus.containsKey(word)) {
			synonyms.addAll(thesaurus.get(word));
		}
		// Remove the original word which was passed in (if present)
		synonyms.remove(word);
		return synonyms;
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

		// Check the cache first to avoid a full thesaurus lookup
		if (cache.containsKey(synonym)) {
			results = cache.get(synonym);
		} else {
			// Have to go through the entire thesaurus
			for (Entry<String, Collection<String>> entry : thesaurus.entrySet()) {
				// If an entry contains the specified word as a synonym
				if (entry.getValue().contains(synonym)) {
					// Return it
					results.addAll(entry.getValue());
				}
			}
			cache.put(synonym, results);
		}
		return results;
	}

	/**
	 * Check if any of the words contained in a clue are present as synonyms to
	 * the given solution.
	 * 
	 * @param clue
	 *            - the <code>Clue</code> object
	 * @param solution
	 *            - the potential solution word
	 * @return <code>true</code> if the thesaurus contains the specified
	 *         solution and a clue word is present as a synonym,
	 *         <code>false</code> otherwise
	 */
	public boolean reverseMatch(Clue clue, String solution) {
		// Populate an array with the separate words of the clue
		String[] clueWords = clue.getClueWords();
		SolutionPattern pattern = clue.getPattern();
		// More than one word in the solution?
		boolean multipleWords = pattern.hasMultipleWords();

		// Solution might need to be 're-spaced'
		String[] solutions;
		if (multipleWords) {
			// e.g. "strain a muscle"
			String recomposed = pattern.recomposeSolution(solution);
			// e,g. { "strain" , "a" , "muscle" }
			String[] words = recomposed.split(WordUtils.REGEX_WHITESPACE);
			solutions = new String[words.length + 2];
			// i.e. "strainamuscle"
			solutions[0] = solution.toLowerCase();
			solutions[1] = recomposed;
			for (int i = 0; i < words.length; i++) {
				solutions[i + 1] = words[i];
			}
		} else {
			solutions = new String[] { solution.toLowerCase() };
		}
		for (String s : solutions) {
			if (thesaurus.containsKey(s)) {
				Collection<String> synonyms = thesaurus.get(s);
				for (String clueWord : clueWords) {
					if (synonyms.contains(clueWord)) {
						return true;
					}
				}
			}
		}
		return false;
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
				if (synonyms.contains(solutions[0])) {
					return true;
				} else if (multipleWords) {
					for (String word : solutions[1]
							.split(WordUtils.REGEX_WHITESPACE)) {
						if (synonyms.contains(word)) {
							return true;
						}
					}
				}
			}
		}
		return false;
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
	 * Adjust the confidence values for solution, based on whether there is a
	 * synonym link between the clue and the solution
	 * 
	 * @param c
	 *            - the clue that is being solved
	 * @param solutions
	 *            - the solutions which have been generated for this clue
	 */
	public void confidenceAdjust(Clue c, SolutionCollection solutions) {

		// See if clue definition word contains the solution as a synonym
		// TODO Can we pick out the definition word rather than check the entire
		// clue?
		for (Solution s : solutions) {
			if (match(c, s.getSolution())) {
				double confidence = Confidence.multiply(s.getConfidence(),
						Confidence.SYNONYM_MULTIPLIER);
				s.setConfidence(confidence);
			}
		}

		// Now check if the solution contains the clue definition word as a
		// synonym, giving a lower confidence rating than the above
		for (Solution s : solutions) {
			if (reverseMatch(c, s.getSolution())) {
				double confidence = Confidence.multiply(s.getConfidence(),
						Confidence.REVERSE_SYNONYM_MULTIPLIER);
				s.setConfidence(confidence);
			}
		}

	}

} // End of class Thesaurus
