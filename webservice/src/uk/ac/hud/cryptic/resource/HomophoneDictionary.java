package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.hud.cryptic.config.Settings;
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

	/**
	 * Default Constructor
	 */
	private HomophoneDictionary() {
		populateDictionaryFromFile();
	}

	/**
	 * Load the dictionary into a HashMap to allow for much faster access
	 */
	private void populateDictionaryFromFile() {
		InputStream is = settings.getHomophoneDictionaryPath();

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
					List<String> pronunciation = Arrays.asList(components[1]
							.split(WordUtils.REGEX_WHITESPACE));
					// Add to the homophone dictionary
					dictionary.put(word, pronunciation);
				}
			} while ((line = br.readLine()) != null);

		} catch (IOException e) {
			System.err
					.println("Exception in Homophone Dictionary initialisation.");
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
	public List<String> getPronunciation(String word) {
		// This will hold the pronunciation, if present
		List<String> pronunciation = new ArrayList<>();
		// Check it is contained in the dictionary
		if (dictionary.containsKey(word)) {
			pronunciation.addAll(dictionary.get(word));
		}
		return pronunciation;
	}

} // End of class HomophoneDictionary
