package uk.ac.hud.cryptic.dictionary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.config.Settings;

/**
 * This class provides a wrapper around the dictionary words file found within
 * Linux systems.
 * 
 * @author Luke Hackett
 * @version 0.1
 */
public class Dictionary {
	// Dictionary Instance
	private static Dictionary instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();
	// Actual dictionary data structure
	private Collection<String> dictionary;

	/**
	 * Default Constructor
	 */
	private Dictionary() {
		populateDictionaryFromFile();
	}

	/**
	 * Load the dictionary into a HashSet to allow for much faster access
	 */
	private void populateDictionaryFromFile() {
		// BufferReader to read the file
		BufferedReader br;
		// Path to local dictionary path
		String dictionaryPath = settings.getLocalDictionaryPath();

		try {
			// Open the dictionary
			br = new BufferedReader(new FileReader(dictionaryPath));
			String line = null;

			// Instantiate the dictionary object
			dictionary = new HashSet<>();

			// Loop over every line
			while ((line = br.readLine()) != null) {
				dictionary.add(line.toLowerCase());
			}

			// Close the stream
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * This method will return the current (and only) instance of the Dictionary
	 * object.
	 * 
	 * @return Dictionary
	 */
	public static Dictionary getInstance() {
		if(instance == null) {
	         instance = new Dictionary();
	      }
		return instance;
	}

	/**
	 * This method will return whether or not the given word can be found in the
	 * local dictionary listing.
	 * 
	 * @param word
	 *            the word to be search for
	 * @return boolean
	 */
	public boolean isWord(String word) {
		return dictionary.contains(word.toLowerCase());
	}

}