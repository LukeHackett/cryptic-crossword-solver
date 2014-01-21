package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * This class provides a wrapper around the dictionary words file found within
 * Linux systems.
 * 
 * @author Luke Hackett, Stuart Leader
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
		InputStream[] is = { settings.getDictionaryPath(),
				settings.getCustomDictionaryPath() };

		// Instantiate the dictionary object
		dictionary = new HashSet<>();

		for (int i = 0; i < is.length; i++) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					is[i]))) {
				// Open the dictionary
				String line = null;

				// Loop over every line
				while ((line = br.readLine()) != null) {
					dictionary.add(line.toLowerCase().trim());
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
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
		return dictionary.contains(word.toLowerCase().trim());
	}

	/**
	 * Get all word matches for a given solution pattern. This means words of
	 * the correct length, with the correct characters where these have been
	 * specified by the user
	 * 
	 * @param pattern
	 *            - the pattern to match against
	 * @return a list of words from the dictionary which match against the
	 *         pattern provided
	 */
	public Collection<String> getMatches(SolutionPattern pattern) {
		Collection<String> matches = new HashSet<>();
		// Go through each word in the dictionary
		for (String word : dictionary) {
			// Return it if it matches the pattern
			if (pattern.match(word)) {
				matches.add(word);
			}
		}
		return matches;
	}

	/**
	 * Determine if any words are present in the dictionary which begin with the
	 * specified prefix
	 * 
	 * @param prefix
	 *            - check if any words in the dictionary begin with this string
	 * @return <code>true</code> if there is at least one match in the
	 *         dictionary, <code>false</code> otherwise
	 */
	public boolean prefixMatch(String prefix) {
		// Standarise the given prefix
		prefix = prefix.toLowerCase().trim();
		// Have the manually check all dictionary words until a match is found
		for (String word : dictionary) {
			if (word.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get all word matches for a given word prefix.
	 * 
	 * @param prefix
	 *            - the prefix of the matches to return
	 * @return a list of words from the dictionary which match with the given
	 *         prefix
	 */
	public Collection<String> getMatchesWithPrefix(String prefix) {
		// Standarise the given prefix
		prefix = prefix.toLowerCase().trim();

		Collection<String> matches = new HashSet<>();
		// Have the manually check all dictionary words for all matches
		for (String w : dictionary) {
			// Get rid of punctuation and spaces
			String word = WordUtils.removeNonAlphabet(w, true);
			// Return it if it matches the pattern
			if (word.startsWith(prefix)) {
				matches.add(word);
			}
		}
		return matches;
	}

	/**
	 * Remove any prefixes from the given collection that are not present in the
	 * dictionary. This is an effective way to remove words that have being
	 * constructed by the algorithm which are essentially just an assortment of
	 * letters which hold no identified meaning.
	 * 
	 * @param prefixes
	 *            - the collection of words to verify against the dictionary
	 */
	public void dictionaryPrefixFilter(SolutionCollection prefixes) {
		Collection<Solution> toRemove = new ArrayList<>();
		for (Solution p : prefixes) {
			String prefix = WordUtils.removeNonAlphabet(p.getSolution(), true);
			if (!prefixMatch(prefix)) {
				toRemove.add(p);
			}
		}
		prefixes.removeAll(toRemove);
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
	 *            - the SolutionPattern object modelling the characteristics of
	 *            the solution from the user's provided input
	 */
	public void dictionaryFilter(SolutionCollection solutions,
			SolutionPattern pattern) {
		Collection<Solution> toRemove = new ArrayList<>();
		outer: for (Solution solution : solutions) {

			// Break each potential solution into it's separate word components
			String[] words;
			if (pattern.hasMultipleWords()) {
				words = pattern.separateSolution(solution.getSolution());
			} else {
				words = new String[] { solution.getSolution() };
			}
			// Check each component of the solution is a confirmed word
			// TODO Check against an abbreviations list and other resources
			for (String word : words) {
				if (!isWord(word)) {
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

} // End of class Dictionary
