package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.util.SolutionPattern;

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
		String dictionaryPath = settings.getDictionaryPath();

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
	 * @return the dictionary
	 */
	public static Dictionary getInstance() {
		if (instance == null) {
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

	/**
	 * Remove any words from the given collection that are not present in the
	 * dictionary. This is an effective way to remove words that have being
	 * constructed by the algorithm which are essentially just an assortment of
	 * letters which hold no identified meaning.
	 * 
	 * @param solutions
	 *            - the collection of words to verify against the dictionary
	 * @param pattern
	 *            - the SolutionStructure object modelling the characteristics
	 *            of the solution from the user's provided input
	 */
	public void dictionaryFilter(Collection<String> solutions,
			SolutionPattern pattern) {
		Collection<String> toRemove = new ArrayList<>();
		outer: for (String solution : solutions) {
	
			// Break each potential solution into it's separate word components
			String[] words;
			if (pattern.hasMultipleWords()) {
				words = pattern.separateSolution(solution);
			} else {
				words = new String[] { solution };
			}
			// Check each component of the solution is a confirmed word
			// TODO Check against an abbreviations list and other resources
			for (String word : words) {
				if (!getInstance().isWord(word)) {
					// Remove solutions which contain at least one word which
					// isn't in the dictionary
					toRemove.add(solution);
					continue outer;
				}
			}
		}
		// Remove those solutions which contain unconfirmed words
		solutions.removeAll(toRemove);
	}

}
