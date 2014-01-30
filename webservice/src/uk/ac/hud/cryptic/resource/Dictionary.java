package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Solution;
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
		InputStream[] is = { settings.getDictionaryPath() };

		// Instantiate the dictionary object
		dictionary = new HashSet<>();

		// Read specified dictionary to internal data structure
		for (InputStream element : is) {
			readFile(element, true);
		}

		// Remove specified exclusions
		InputStream exclusions = settings.getDictionaryExclusionsPath();
		readFile(exclusions, false);

		// Now add custom dictionary (takes precedence over exclusions)
		InputStream customWords = settings.getCustomDictionaryPath();
		readFile(customWords, true);
	}

	/**
	 * Read an <code>InputStream</code> and either add or remove the contents
	 * from the dictionary depending on which flag is passed.
	 * 
	 * @param element
	 *            - the Stream to read in
	 * @param add
	 *            - <code>true</code> to add the found words to the dictionary,
	 *            <code>false</code> to remove them
	 */
	private void readFile(InputStream element, boolean add) {

		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				element))) {
			// Open the dictionary
			String line = null;

			// Loop over every line
			while ((line = br.readLine()) != null) {
				if (add) {
					dictionary.add(line.toLowerCase().trim());
				} else {
					dictionary.remove(line.toLowerCase().trim());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void dictionaryFilter(Set<Solution> solutions,
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

	/**
	 * Remove any prefixes from the given collection that are not present in the
	 * dictionary. This is an effective way to remove words that have being
	 * constructed by the algorithm which are essentially just an assortment of
	 * letters which hold no identified meaning.
	 * 
	 * @param prefixes
	 *            - the collection of words to verify against the dictionary
	 */
	public void dictionaryPrefixFilter(Set<Solution> prefixes) {
		// List of prefixes which don't match the beginnings of known word, and
		// so will be removed
		Collection<Solution> toRemove = new ArrayList<>();
		// For each given prefix String
		for (Solution p : prefixes) {
			// Standardise it
			String prefix = WordUtils.removeSpacesAndHyphens(p.getSolution());
			// If the dictionary can't find any words beginning with this letter
			// combination, remove it
			if (!prefixMatch(prefix)) {
				toRemove.add(p);
			}
		}
		prefixes.removeAll(toRemove);
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
	 * Get all word matches for a given word prefix.
	 * 
	 * @param prefix
	 *            - the prefix of the matches to return
	 * @param length
	 *            - the length of the target word
	 * @return a list of words from the dictionary which match with the given
	 *         prefix
	 */
	public Collection<String> getMatchesWithPrefix(String prefix, int length) {
		// Standardise the given prefix
		prefix = prefix.toLowerCase().trim();

		Collection<String> matches = new HashSet<>();
		// Have the manually check all dictionary words for all matches
		for (String w : dictionary) {
			// Get rid of punctuation and spaces
			// Return it if it matches the pattern
			if (w.length() == length && w.startsWith(prefix)) {
				matches.add(w);
			}
		}
		return matches;
	}

	/**
	 * Return a collection of dictionary words which match against the specified
	 * pattern
	 * 
	 * @param pattern
	 *            - the solution pattern, in the form of a string ("?h??"),
	 *            representing a single word of what may be a larger solution
	 *            pattern
	 * @return a list of words which match against the specified pattern
	 */
	public Collection<String> getMatchingWords(String pattern) {
		Set<String> words = new HashSet<>();
		// Go through all words of the dictionary
		for (String w : dictionary) {
			// If the dictionary word matches the pattern, return it
			if (SolutionPattern.match(pattern, w)) {
				words.add(w);
			}
		}
		return words;
	}

	/**
	 * This method will return whether or not the given word can be found in the
	 * local dictionary listing.
	 * 
	 * @param word
	 *            the word to be search for
	 * @return <code>true</code> if the word is valid, <code>false</code>
	 *         otherwise
	 */
	public boolean isWord(String word) {
		return dictionary.contains(word.toLowerCase().trim());
	}

	/**
	 * Just the same as "isWord", but this is to be used when the text may
	 * contain more than one word
	 * 
	 * @param words
	 *            - the word to check in the dictionary
	 * @return <code>true</code> if all words are valid, <code>false</code>
	 *         otherwise
	 */
	public boolean areWords(String input) {
		if (input != null && !input.isEmpty()) {
			String[] words = input.split(WordUtils.SPACE_AND_HYPHEN);
			// Check each word
			for (String word : words) {
				if (!isWord(word)) {
					return false;
				}
			}
		}
		return true;
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

} // End of class Dictionary
