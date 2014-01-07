package uk.ac.hud.cryptic.util;

import java.util.ArrayList;
import java.util.Collection;

import uk.ac.hud.cryptic.resource.Dictionary;

/**
 * A collection of helper methods relating to the manipulation of words and
 * sentences.
 */
public class WordUtils {

	// Will match anything that isn't [A-Z] or [a-z] including spaces
	public static final String REGEX_NON_LETTERS_SPACES = "(\\W|_|[0-9])+";

	// Will match anything that isn't [A-Z] or [a-z] excluding spaces
	public static final String REGEX_NON_LETTERS = "[^A-Za-z\\s]+";

	// Will match characters used to separate solution patterns
	public static final String REGEX_SEPARATORS = "(,|-)";

	// Will match anything other than [0-9]
	public static final String REGEX_NON_NUMERIC = "\\D+";
	
	private static final Dictionary DICT = Dictionary.getInstance();

	/**
	 * Remove any non-alphabetical characters including spaces
	 * 
	 * @param input
	 *            - The input text
	 * @param removeSpaces
	 *            - true if spaces should be removed, false otherwise
	 * @return the input string minus any non-alphabetical characters
	 */
	public static String removeNonAlphabet(String input, boolean removeSpaces) {
		// Select the appropriate REGEX pattern depending on parameters
		String regex = removeSpaces ? REGEX_NON_LETTERS_SPACES
				: REGEX_NON_LETTERS;
		String output = input.replaceAll(regex, "");

		// Trim any excess
		output = output.trim();

		// To lower case
		output = output.toLowerCase();

		return output;
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
	public static void dictionaryFilter(Collection<String> solutions,
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
				if (!DICT.isWord(word)) {
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
