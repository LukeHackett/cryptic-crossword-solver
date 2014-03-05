package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.util.WordUtils;

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
					.println("Exception in Abbreviations initialisation.");
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
			abbreviations.put(WordUtils.normaliseInput(word, false),
					wordAbbreviations);
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
	 * Get the abbreviations, if any, which match the given, single word.
	 * 
	 * @param word
	 *            - the single word to find the abbreviations for
	 * @return a set of words which are abbreviations of the given word
	 */
	public synchronized Set<String> getAbbreviationsForWord(String word) {
		// Return empty set if no abbreviations, rather than null
		return abbreviations.containsKey(word) ? abbreviations.get(word)
				: new HashSet<String>();
	}

	/**
	 * Get the abbreviations for as many words as possible in the given clue. For
	 * example, "help the medic" will contain 7 abbreviations for the word
	 * medic. "medal for the medic" will contain 4 abbreviations for medal and 7
	 * for medic. However, the clue "master of ceremonies" will return 1
	 * abbreviation which matches the entire clue (i.e. "master of ceremonies").
	 * The algorithm is greedy, and will attempt to match the biggest String
	 * possible in the given clue. This means it will match all of the String
	 * "master of ceremonies" before matching abbreviations for "master".
	 * 
	 * @param clue
	 *            - the clue to look for abbreviations
	 * @return a LinkedHashMap of all the abbreviations that have been found
	 */
	public synchronized Map<String, Set<String>> getAbbreviationsForClue(
			String clue) {
		// This will be returned and will contain any found abbreviations
		LinkedHashMap<String, Set<String>> abbrMap = new LinkedHashMap<>();

		// Abbreviations can span across multiple words
		// Convert clue to List
		List<String> clueList = new ArrayList<>(Arrays.asList(clue
				.split(WordUtils.REGEX_WHITESPACE)));

		// The index of the last clue word
		int maxIndex = clueList.size() - 1;
		// Used to avoid duplicate matching in the case of a greedy match
		int nextUnfoundIndex = 0;

		// Starting FROM the first word of the clue for the beginning of the
		// substring
		for (int i = 0; i <= maxIndex; i++) {
			// Only proceed if the word at this index hasn't had an abbreviation
			// found already
			if (i >= nextUnfoundIndex) {
				// Start with the biggest index (TO) for the end of the
				// substring
				for (int j = maxIndex; j >= i; j--) {
					// Create a string from the current indexes
					String clueWords = composeClueSubstring(clueList, i, j);
					// If this String has registered abbreviations, note them!
					if (abbreviations.containsKey(clueWords)) {
						abbrMap.put(clueWords, abbreviations.get(clueWords));
						// Only look for the abbreviations of remaining clue
						// words
						nextUnfoundIndex = j + 1;
						break;
					}
				}
			}
		}
		return abbrMap;
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
	 * Entry point for testing purposes
	 */
	public static void main(String[] args) {
		Abbreviations a = Abbreviations.getInstance();
		a.getAbbreviationsForClue("medal the medic map makers master of arts");
		a.getAbbreviationsForClue("cricketer is on 50 again");
	}

} // End of class Abbreviations
