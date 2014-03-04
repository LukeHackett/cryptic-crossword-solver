package uk.ac.hud.cryptic.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * A collection of helper methods relating to the manipulation of words and
 * sentences.
 */
public class WordUtils {

	// Will match anything that is [A-Z] or [a-z] (a single letter) excluding
	// spaces
	public static final String REGEX_LETTER = "[A-Za-z]";

	// Will match anything that isn't [A-Z] or [a-z] including spaces
	public static final String REGEX_NON_LETTERS_SPACES = "(\\W|_)+";

	// Will match anything that isn't [A-Z] or [a-z] excluding spaces
	public static final String REGEX_NON_LETTERS = "[^A-Za-z0-9\\s]+";

	// Will match characters used to separate solution patterns
	public static final String REGEX_SEPARATORS = "(" + SolutionPattern.SPACE
			+ "|" + SolutionPattern.HYPHEN + ")";

	// Will match whitespace characters
	public static final String REGEX_WHITESPACE = "\\s+";

	// Spaces and hyphens
	public static final String SPACE_AND_HYPHEN = "(\\s+|-)";

	/**
	 * Check whether the characters known by the user are present within the
	 * potential solution
	 * 
	 * @param word
	 *            the solution to check
	 * @param mandatoryChars
	 *            the characters which should be present
	 * @return
	 */
	public static boolean charactersPresentInWord(String word,
			String[] mandatoryChars) {
		StringBuilder builder = new StringBuilder();
		// Convert array of String to String
		for (String s : mandatoryChars) {
			builder.append(s);
		}
		return hasCharacters(builder.toString(), word);
	}

	/**
	 * Check if a single word or SPACED phrase matches against a given pattern.
	 * For example, true will be returned with input parameters "over the top"
	 * and "????,???,???". The actual characters are not dealt with here to see
	 * if they match up, just the word lengths. For the example given, lengths
	 * [4,3,3] will match with word length [4,3,3] of the pattern.
	 * 
	 * @param phrase
	 *            - the phrase to check the word lengths of
	 * @param pattern
	 *            - the solution pattern to match against
	 * @return <code>true</code> if the word lengths match up,
	 *         <code>false</code> otherwise
	 */
	public static boolean wordLengthMatch(String phrase, SolutionPattern pattern) {
		boolean match = true;

		String[] indWords = phrase.split(WordUtils.SPACE_AND_HYPHEN);
		int[] indLengths = new int[indWords.length];
		for (int i = 0; i < indWords.length; i++) {
			indLengths[i] = indWords[i].length();
		}

		// Test the "pattern" of the synonym and the solution match
		if (!Arrays.equals(indLengths, pattern.getIndividualWordLengths())) {
			match = false;
		}
		return match;
	}

	/**
	 * Determine whether a specified word can be create by using the characters
	 * present in a pool of available characters.
	 * 
	 * @param targetWord
	 *            - the word to attempt to create using the supplied characters
	 * @param characters
	 *            - a pool of characters that can be used to build the target
	 *            word
	 * @return <code>true</code> if the target word can be built the set (or a
	 *         subset of) the characters present in this String,
	 *         <code>false</code> otherwise
	 */
	public static boolean hasCharacters(String targetWord, String characters) {
		// This list will be reduced as characters are consumed
		Collection<Character> remaining = new ArrayList<>();
		for (char c : characters.toCharArray()) {
			remaining.add(c);
		}

		if (!targetWord.isEmpty()) {

			// For each character of the target word
			for (char c : targetWord.toCharArray()) {
				// If the char isn't available in the pool of remaining
				// characters,
				// abort mission
				if (!remaining.remove(c)) {
					return false;
				}
			}
		}
		// If you've reached here, the target word can indeed be built
		return true;
	}

	/**
	 * This method will reverse the ordering of a given String word input.
	 * 
	 * @param word
	 *            the word to be reversed
	 * @return String
	 */
	public static String reverseWord(String word) {
		// no need to reverse over single letters
		if (word.length() < 1) {
			return word;
		}

		// The reversed word
		String reversedWord = "";

		// Loop over the word backwards and rebuild
		for (int i = word.length() - 1; i >= 0; i--) {
			reversedWord += word.charAt(i);
		}

		return reversedWord;
	}

	/**
	 * Remove any non-alphabetical characters including spaces. This should be
	 * used from input coming from the user, such as the Clue and Solution text.
	 * 
	 * @param input
	 *            - The input text
	 * @param removeSpaces
	 *            - true if spaces should be removed, false otherwise
	 * @return the input string minus any non-alphabetical characters
	 */
	public static String normaliseInput(String input, boolean removeSpaces) {
		// Select the appropriate REGEX pattern depending on parameters
		String regex = removeSpaces ? REGEX_NON_LETTERS_SPACES
				: REGEX_NON_LETTERS;

		// First replace apostrophes with nothing - not a space please
		String output = input.replaceAll("'", "");

		// Get rid of accented characters
		output = Normalizer.normalize(output, Form.NFD).replaceAll(
				"\\p{InCombiningDiacriticalMarks}+", "");

		// Replace punctuation with a space
		output = output.replaceAll(regex, " ");

		if (removeSpaces) {
			output = removeSpacesAndHyphens(output);
		} else {
			// Replace multiple spaces with just one
			output = output.replaceAll("\\s+", " ");
		}

		// Trim any excess
		output = output.trim();

		// To lower case
		output = output.toLowerCase();

		return output;
	}

	/**
	 * Removes spaces and hyphens from a given String
	 * 
	 * @param input
	 *            - the input to remove spaces and hyphens from
	 * @return the text with no spaces or hyphens
	 */
	public static String removeSpacesAndHyphens(String input) {
		return input.replaceAll(SPACE_AND_HYPHEN, "");
	}

} // End of class WordUtils
