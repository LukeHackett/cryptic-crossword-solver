package uk.ac.hud.cryptic.util;

import uk.ac.hud.cryptic.core.SolutionPattern;

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
	public static final String REGEX_SEPARATORS = "(" + SolutionPattern.SPACE
			+ "|" + SolutionPattern.HYPHEN + ")";

	// Will match whitespace characters
	public static final String REGEX_WHITESPACE = "\\s+";

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

} // End of class WordUtils
