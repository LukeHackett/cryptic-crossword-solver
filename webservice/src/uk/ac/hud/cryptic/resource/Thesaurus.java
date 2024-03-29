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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
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
import uk.ac.hud.cryptic.util.Util;
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
	private Map<String, Set<String>> thesaurus;
	// Cache to speed up some operations
	private Cache<String, Set<String>> cache;

	/**
	 * Default Constructor
	 */
	private Thesaurus() {
		cache = new Cache<>();
		populateThesaurusFromFile();
	}

	public static void main(String[] args) {
		Thesaurus t = Thesaurus.getInstance();
		// Go and fetch the first level synonyms
		Set<String> firstLevelSynonyms = t.getSynonyms("beer");
		// This will hold the results of this method
		Set<String> secondLevelSynonyms = new HashSet<>();

		// Include first level synonyms if requested
		if (true) {
			// But only those that match the specified pattern
			Set<String> matchingFirstLevel = t.getSynonyms("beer");
			secondLevelSynonyms.addAll(matchingFirstLevel);
		}

		// Get the synonyms for each first level synonym
		for (String synonym : firstLevelSynonyms) {
			// The second level synonyms for this first level synonym
			Set<String> newSynonyms = t.getSynonyms(synonym);
			secondLevelSynonyms.addAll(newSynonyms);
		}

		for (String syn : secondLevelSynonyms) {
			System.out.println(syn);
		}
	}

	/**
	 * Load the thesaurus into a HashSet to allow for much faster access
	 */
	private void populateThesaurusFromFile() {
		InputStream[] is = { settings.getThesaurusStream(),
				settings.getCustomThesaurusStream() };

		// Instantiate the thesaurus object
		thesaurus = new HashMap<>();

		// Read specified dictionary to internal data structure
		for (InputStream element : is) {
			readFile(element);
		}
	}

	/**
	 * Read an <code>InputStream</code> and add the contents to the thesaurus.
	 * 
	 * @param element
	 *            - the Stream to read in
	 */
	private void readFile(InputStream element) {
		// Try-with-resources. Readers are automatically closed after use
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				element))) {
			String line = null;
			// For each entry of the thesaurus
			while ((line = br.readLine()) != null) {
				// Separate the individual words
				String[] words = line.split(",");
				// Get the key (look-up) word
				String lookupWord = words[0].toLowerCase();
				// Rest of the words are synonyms
				words = Arrays.copyOfRange(words, 1, words.length);
				// Add words to a list
				Set<String> entry = new HashSet<>();
				for (String word : words) {
					if (Dictionary.getInstance().areWords(word)) {
						entry.add(word.toLowerCase());
					}
				}
				Util.addAllToMap(thesaurus, lookupWord, entry, HashSet.class);
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
	 * Obtain a list of "synonyms of a word's synonyms" to increase the chances
	 * of finding the correct solution.
	 * 
	 * @param word
	 *            - the word to find two levels of synonyms for
	 * @param includeFirstLevel
	 *            - <code>true</code> to return the first level synonyms also,
	 *            <code>false</code> to only return the second level synonyms
	 * @return a set of synonyms of a word's synonyms
	 */
	public Set<String> getSecondSynonyms(String word, int maxLength,
			int minLength, boolean includeFirstLevel) {
		// Go and fetch the first level synonyms
		Set<String> firstLevelSynonyms = getSynonyms(word);
		// This will hold the results of this method
		Set<String> secondLevelSynonyms = new HashSet<>();

		// Include first level synonyms if requested
		if (includeFirstLevel) {
			// But only those that match the specified pattern
			Set<String> matchingFirstLevel = filterSynonyms(firstLevelSynonyms,
					maxLength, minLength);
			secondLevelSynonyms.addAll(matchingFirstLevel);
		}

		// Get the synonyms for each first level synonym
		for (String synonym : firstLevelSynonyms) {
			// The second level synonyms for this first level synonym
			Set<String> newSynonyms = getSynonymsWithMinMaxLength(synonym,
					maxLength, minLength);
			secondLevelSynonyms.addAll(newSynonyms);
		}

		return secondLevelSynonyms;
	}

	/**
	 * Retrieve all single word synonyms of a given word with a maximum and
	 * minimum length
	 * 
	 * @param word
	 *            - the word to get synonyms for
	 * @return the synonyms of the given word
	 */
	private Set<String> getSynonymsWithMinMaxLength(String word, int maxLength,
			int minLength) {
		// Use of HashSet prevents duplicates
		Set<String> synonyms = new HashSet<>();
		if (thesaurus.containsKey(word)) {
			Collection<String> wordSyns = thesaurus.get(word);
			for (String synonym : wordSyns) {
				String[] checkForMultipleWords = synonym
						.split(WordUtils.SPACE_AND_HYPHEN);
				if (checkForMultipleWords.length == 1) {
					if (synonym.length() <= maxLength
							&& synonym.length() >= minLength) {
						synonyms.add(synonym);
					}
				}
			}
		}
		// Remove the original word which was passed in (if present)
		synonyms.remove(word);
		return synonyms;
	}

	/**
	 * Retrieve all single word synonyms of a given word with a maximum and
	 * minimum length
	 * 
	 * @param word
	 *            - the word to get synonyms for
	 * @return the synonyms of the given word
	 */
	private Set<String> filterSynonyms(Set<String> synonyms, int maxLength,
			int minLength) {
		// Use of HashSet prevents duplicates
		Set<String> filteredSynonyms = new HashSet<>();
		for (String synonym : synonyms) {
			String[] checkForMultipleWords = synonym
					.split(WordUtils.SPACE_AND_HYPHEN);
			if (checkForMultipleWords.length == 1) {
				int length = synonym.length();
				if (length >= minLength && length <= maxLength) {
					filteredSynonyms.add(synonym);
				}
			}
		}
		return filteredSynonyms;
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
	 * Get the synonyms for as many words as possible in the given clue. For
	 * example, in the clue "help the medic", look for synonyms of
	 * "help the medic", "help the", "the medic", "help", "the", "medic".
	 * 
	 * @param clue
	 *            - the clue to look for synonyms
	 * @return a LinkedHashMap of all the synonyms that have been found
	 */
	public synchronized Map<String, Set<String>> getSynonymsForClue(String clue) {
		// This will be returned and will contain any found abbreviations
		LinkedHashMap<String, Set<String>> synonymMap = new LinkedHashMap<>();

		// Synonyms can span across multiple words
		// Convert clue to List
		List<String> clueList = new ArrayList<>(Arrays.asList(WordUtils
				.getWords(clue)));

		// The index of the last clue word
		int maxIndex = clueList.size() - 1;

		// Starting FROM the first word of the clue for the beginning of the
		// substring
		for (int i = 0; i <= maxIndex; i++) {
			// Start with the biggest index (TO) for the end of the
			// substring
			for (int j = maxIndex; j >= i; j--) {
				// Create a string from the current indexes
				String clueWords = composeClueSubstring(clueList, i, j);
				// If this String has registered abbreviations, note them!
				if (thesaurus.containsKey(clueWords)) {
					synonymMap.put(clueWords, thesaurus.get(clueWords));
					// break;
				}

			}
		}
		return synonymMap;
	}

	/**
	 * Create a String of words using the indexes of a List of words.
	 * 
	 * @param clue
	 *            - A clue represented as a list, with an entry for each word
	 * @param fromIndex
	 *            - The first index to create a substring from
	 * @param toIndex
	 *            - The last index to create a substring to
	 * @return the complete substring
	 */
	private String composeClueSubstring(List<String> clue, int fromIndex,
			int toIndex) {
		// The initial substring. This will be returned
		String substring = "";

		// From the start index to the end index
		for (int i = fromIndex; i <= toIndex; i++) {
			// Add the corresponding word
			substring += clue.get(i) + " ";
		}
		return substring.trim();
	}

	/**
	 * Retrieve all synonyms in the same entry in the thesaurus as a given word
	 * 
	 * @param word
	 *            - the word to get synonyms for
	 * @return the synonyms in the same entry as the given word
	 */
	public Set<String> getEntriesContainingSynonym(String word,
			boolean includeSiblings) {
		if (cache.containsKey(word)) {
			return cache.get(word);
		}
		Set<String> synonyms = new HashSet<>();
		for (Entry<String, Set<String>> entry : thesaurus.entrySet()) {
			if (entry.getKey().equals(word) || entry.getValue().contains(word)) {
				if (includeSiblings) {
					synonyms.addAll(entry.getValue());
				}
				synonyms.add(entry.getKey());
			}
		}
		synonyms.remove(word);
		cache.put(word, synonyms);

		return synonyms;
	}

	/**
	 * Retrieve all synonyms in the same entry in the thesaurus as a given word
	 * which match against the given pattern
	 * 
	 * @param word
	 *            - the word to get synonyms for
	 * @param pattern
	 *            - the pattern the synonyms should match against
	 * @return the synonyms in the same entry as the given word which match the
	 *         given pattern
	 */
	public Set<String> getEntriesContainingSynonym(String word,
			SolutionPattern pattern, boolean includeSiblings) {
		if (cache.containsKey(word)) {
			return cache.get(word);
		}
		Set<String> synonyms = new HashSet<>();
		for (Entry<String, Set<String>> entry : thesaurus.entrySet()) {
			if (entry.getKey().equals(word) || entry.getValue().contains(word)) {
				if (includeSiblings) {
					synonyms.addAll(entry.getValue());
				}
				synonyms.add(entry.getKey());
			}
		}
		synonyms.remove(word);
		Iterator<String> it = synonyms.iterator();
		while (it.hasNext()) {
			String synonym = it.next();
			if (!pattern.match(synonym)) {
				it.remove();
			}
		}
		cache.put(word, synonyms);

		return synonyms;
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
	private boolean reverseMatch(Clue clue, Solution solution) {
		// Populate an array with the separate words of the clue
		String[] clueWords = clue.getClueWords();
		SolutionPattern pattern = clue.getPattern();
		// More than one word in the solution?
		boolean multipleWords = pattern.hasMultipleWords();

		// Solution might need to be 're-spaced'
		String[] solutions;
		if (multipleWords) {
			// e.g. "strain a muscle"
			String recomposed = pattern.recomposeSolution(solution
					.getSolution());
			// e,g. { "strain" , "a" , "muscle" }
			String[] words = WordUtils.getWords(recomposed);
			solutions = new String[words.length + 2];
			// i.e. "strainamuscle"
			solutions[0] = solution.getSolution().toLowerCase();
			solutions[1] = recomposed;
			for (int i = 0; i < words.length; i++) {
				solutions[i + 1] = words[i];
			}
		} else {
			solutions = new String[] { solution.getSolution().toLowerCase() };
		}
		for (String s : solutions) {
			if (thesaurus.containsKey(s)) {
				Collection<String> synonyms = thesaurus.get(s);
				for (String clueWord : clueWords) {
					if (synonyms.contains(clueWord)) {
						solution.addToTrace("Confidence rating slightly increased as the clue word \""
								+ clueWord
								+ "\" is a synonym of this solution.");
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
	public boolean match(Clue clue, Solution solution) {
		// Populate an array with the separate words of the clue
		String[] clueWords = clue.getClueWords();
		SolutionPattern pattern = clue.getPattern();
		// More than one word in the solution?
		boolean multipleWords = pattern.hasMultipleWords();

		// Solution might need to be 're-spaced'
		String[] solutions = new String[multipleWords ? 2 : 1];
		solutions[0] = solution.getSolution().toLowerCase();
		if (multipleWords) {
			solutions[1] = pattern.recomposeSolution(solution.getSolution());
		}
		for (String clueWord : clueWords) {
			if (thesaurus.containsKey(clueWord)) {
				Collection<String> synonyms = thesaurus.get(clueWord);
				if (synonyms.contains(solutions[0])) {
					solution.addToTrace("Confidence rating increased as this solution is a synonym of the clue word \""
							+ clueWord + "\".");
					return true;
				} else if (multipleWords) {
					for (String word : WordUtils.getWords(solutions[1])) {
						if (synonyms.contains(word)) {
							solution.addToTrace("Confidence rating increased as this solution is a synonym of the clue word \""
									+ clueWord + "\".");
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
			if (match(c, s)) {
				double confidence = Confidence.multiply(s.getConfidence(),
						Confidence.SYNONYM_MULTIPLIER);
				s.setConfidence(confidence);
			}
		}

		// Now check if the solution contains the clue definition word as a
		// synonym, giving a lower confidence rating than the above
		for (Solution s : solutions) {
			if (reverseMatch(c, s)) {
				double confidence = Confidence.multiply(s.getConfidence(),
						Confidence.REVERSE_SYNONYM_MULTIPLIER);
				s.setConfidence(confidence);
			}
		}

	}

} // End of class Thesaurus
