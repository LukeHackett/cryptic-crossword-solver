package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.ac.hud.cryptic.config.Settings;

/**
 * An interface to the abbreviations file
 * 
 * @author Stuart Leader
 * @version 0.3
 */
public class Abbreviations {
	// Thesaurus Instance
	private static Abbreviations instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();
	// Actual Abbreviations data structure
	private Map<String, Set<String>> abbreviations;

	/**
	 * Default Constructor
	 */
	private Abbreviations() {
		populateListFromFile();
	}

	/**
	 * Load the abbreviations into a HashMap to allow for much faster access
	 */
	private void populateListFromFile() {
		InputStream is = settings.getAbbreviationsStream();

		// Instantiate the abbreviations object
		abbreviations = new HashMap<>();

		// Read in the file
		String jsonRaw = "";
		// Try-with-resources. Readers are automatically closed after use
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				jsonRaw += line;
			}
		} catch (IOException e) {
			System.err
					.println("Exception in Homophone Dictionary initialisation.");
		}

		// Represent the String as a JSON object
		JSONObject jsonObj = new JSONObject(jsonRaw);

		// Add to the Map object
		for (Object keyObj : jsonObj.keySet()) {
			// Key
			String word = (String) keyObj;
			
			// Value(s)
			JSONArray array = jsonObj.getJSONArray(word);
			Set<String> wordAbbreviations = new HashSet<>();
			for (int i = 0; i < array.length(); i++) {
				wordAbbreviations.add(array.getString(i));
			}

			// Add to the abbreviation data structure
			abbreviations.put(word, wordAbbreviations);
		}
	}

	/**
	 * This method will return the current (and only) instance of the
	 * Abbreviations object.
	 * 
	 * @return the abbreviations
	 */
	public static Abbreviations getInstance() {
		if (instance == null) {
			instance = new Abbreviations();
		}
		return instance;
	}

	/**
	 * Get the abbreviations, if any, which match the given word.
	 * 
	 * @param word
	 *            - the word to find the abbreviations for
	 * @return a set of words which are abbreviations of the given word
	 */
	public synchronized Set<String> getAbbreviations(String word) {
		// Return empty set if no abbreviations, rather than null
		return abbreviations.containsKey(word) ? abbreviations.get(word)
				: new HashSet<String>();
	}

} // End of class Abbreviations
