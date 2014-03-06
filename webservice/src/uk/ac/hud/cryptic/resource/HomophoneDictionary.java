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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.util.Cache;
import uk.ac.hud.cryptic.util.Util;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * An interface to the homophone dictionary file(s)
 * 
 * @author Stuart Leader
 * @version 0.2
 */
public class HomophoneDictionary {
	// Thesaurus Instance
	private static HomophoneDictionary instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();
	// The separator between the word and its pronunciation
	private static final String SEPARATOR = "\\s{2}";
	// The comment indicator
	private static final String COMMENT = ";;;";
	// Actual homophone dictionary data structure
	private Map<String, List<String>> dictionary;
	// Reverse map for fast lookup of pronunciations
	private Map<List<String>, List<String>> reverseDictionary;
	// Cache of homonyms
	private Cache<String, Set<String>> cache;

	/**
	 * Default Constructor
	 */
	private HomophoneDictionary() {
		populateDictionaryFromFile();
		cache = new Cache<>();
	}

	/**
	 * Load the dictionary into a HashMap to allow for much faster access
	 */
	private void populateDictionaryFromFile() {
		InputStream is = settings.getHomophoneDictionaryStream();

		// Instantiate the homophone dictionary object
		dictionary = new HashMap<>();

		// Try-with-resources. Readers are automatically closed after use
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line = br.readLine();

			// Read past the comments
			while (line != null && line.startsWith(COMMENT)) {
				line = br.readLine();
			}

			// Line should be null after skipping the comments
			if (line == null) {
				throw new IOException("No definitions found");
			}

			// For each entry of the thesaurus
			do {
				// Split the definition and the pronunciation
				String[] components = line.split(SEPARATOR);
				// Should now have 2 elements in the array
				if (components.length == 2) {
					// Normalise the lookup word
					String word = components[0].toLowerCase().trim();
					// Create a list of the pronunciation elements
					List<String> pronunciation = Arrays.asList(WordUtils
							.getWords(components[1]));
					// Add to the homophone dictionary
					dictionary.put(word, pronunciation);
				}
			} while ((line = br.readLine()) != null);

			// Effectively swap round the keys / values
			populateReverseDictionary();

		} catch (IOException e) {
			System.err
					.println("Exception in Homophone Dictionary initialisation.");
		}
	}

	/**
	 * This allows pronunciations to be looked up faster. For example, looking
	 * up the pronunciation "HH AH0 L OW1" will return "hello". This saves
	 * having to iterate the entire homophone map to search for words with the
	 * same pronunciation.
	 */
	private void populateReverseDictionary() {
		reverseDictionary = new HashMap<>();
		// For each word-pronunciation pair in the existing map
		for (Entry<String, List<String>> e : dictionary.entrySet()) {
			// The value (pronunciation) now becomes the key
			List<String> key = e.getValue();
			// and the key (the word itself) now becomes the value
			String value = e.getKey().replaceAll("\\(\\d+\\)", "");
			// Note that a pronunciation can have multiple words attached to it
			Util.addToMap(reverseDictionary, key, value, ArrayList.class);
		}
	}

	/**
	 * This method will return the current (and only) instance of the
	 * HomophoneDictionary object.
	 * 
	 * @return the homophone dictionary
	 */
	public static HomophoneDictionary getInstance() {
		if (instance == null) {
			instance = new HomophoneDictionary();
		}
		return instance;
	}

	/**
	 * Retrieve the pronunciation of a given word
	 * 
	 * @param word
	 *            - the word to get pronunciation for
	 * @return the pronunciation of the given word
	 */
	public Collection<List<String>> getPronunciations(String word) {
		// This will hold the pronunciation, if present
		Collection<List<String>> pronunciations = new ArrayList<>();

		// Might be more than one pronunciation
		List<String> pronunciation;
		String key = word;
		int counter = 0;

		// Check it is contained in the dictionary
		while ((pronunciation = dictionary.get(key)) != null) {
			pronunciations.add(pronunciation);
			// Search for more pronunciations
			key = word + "(" + ++counter + ")";
		}

		return pronunciations;
	}

	/**
	 * Get words which share the same pronunciation as the supplied word. This
	 * only works for words which share the exact same pronunciation.
	 * 
	 * @param word
	 *            - the word to find the homonyms of
	 * @return a set of words which share the same pronunciation (homonyms) as
	 *         the supplied word
	 */
	public synchronized Set<String> getHomonyms(String word) {
		// First check the cache
		if (cache.containsKey(word)) {
			return cache.get(word);
		}
		// This will hold the words that are pronounced the same
		Set<String> homonyms = new HashSet<>();
		// Get the pronunciations of the supplied word
		Collection<List<String>> pronunciations = getPronunciations(word);
		// For each of these pronunciations
		for (List<String> pronunciation : pronunciations) {
			if (reverseDictionary.containsKey(pronunciation)) {
				homonyms.addAll(reverseDictionary.get(pronunciation));
			}
			// Remove the original word, if present
			homonyms.remove(word);
			// Add results to the cache
			cache.put(word, homonyms);
		}
		return homonyms;
	}

} // End of class HomophoneDictionary
