package uk.ac.hud.cryptic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.ac.hud.cryptic.util.WordUtils;

/**
 * An object modelling the solution to the corresponding clue. This is done
 * using details provided by the user, such as the known word length(s).
 */
public class SolutionPattern {

	// The constants of a solution pattern
	public static final char SPACE = ',';
	public static final char HYPHEN = '-';
	public static final char UNKNOWN_CHARACTER = '?';

	// As inputted by the user. e.g. "?a??e,???d-??"
	private final String pattern;
	// Solution is comprised of this many words
	private int wordCount;
	// Individual word lengths. e.g. [ 5 , 4 , 2 ]
	private int[] indLengths;
	// The characters present between words. e.g. [ ',' , '-' ]
	private char[] separators;
	// Total number of characters in the solution. e.g. For 5,4-2 = 11
	private int totalLength;
	// Does the solution comprise of a single or multiple words?
	private boolean multipleWords;
	// The solution patterns for the individual words of the solution
	private String[] indWordPatterns;
	// True if no characters have been specified
	private boolean allUnknown;

	/**
	 * Constructor takes in the user input of what is known of the solution
	 * (e.g. its character lengths) and performs useful calculation based on
	 * this data such as the total character length of the solution and the
	 * number of words it comprises of.
	 * 
	 * @param pattern
	 *            - the user input of the known parameters of the clue's
	 *            solution
	 */
	public SolutionPattern(String pattern) {
		this.pattern = pattern.toLowerCase();
		calculate(pattern);

	}

	/**
	 * Parse the given pattern in order to obtain useful information on it, such
	 * as individual and total character counts.
	 * 
	 * @param pattern
	 *            - the solution pattern as given by the user
	 */
	private void calculate(String pattern) {
		// Obtain the individual word patterns
		indWordPatterns = pattern.split(WordUtils.REGEX_SEPARATORS);
		// If more than one word, set the multipleWords flag
		multipleWords = indWordPatterns.length > 1;
		if (multipleWords) {
			// Separators won't be present in a single-word solution
			separators = processSeparators(pattern, WordUtils.REGEX_SEPARATORS);
		}

		// Are all characters unknown?
		final String regex = "\\?+";
		allUnknown = Pattern.matches(regex, pattern);

		// For 5-4,2 this will be 3
		wordCount = indWordPatterns.length;
		// Aiming to achieve an array with contents of [ 5 , 4 , 2 ]
		indLengths = new int[wordCount];

		// Get a cumulative total
		int i = 0;
		for (i = 0; i < wordCount; i++) {
			String indWordLength = indWordPatterns[i];

			try {
				int lengthValue = indWordLength.length();
				totalLength += lengthValue;
				indLengths[i] = lengthValue;
			} catch (NumberFormatException e) {
				System.err.println("Cannot parse solution length ("
						+ indWordLength + ")");
			}
		}
	}

	/**
	 * Manipulate the given text in order to obtain an array of the separation
	 * characters used to split up the separate words. For example, this method
	 * will return array [ '-' , ',' ] for input "5-4,2"
	 * 
	 * @param pattern
	 *            - the solution pattern
	 * @param matchOn
	 *            - a regular expression of the characters to match on
	 * @return a char array of the separators present in the given pattern
	 */
	private static char[] processSeparators(String pattern, String matchOn) {
		// Initialise regex objects using the given information
		Pattern p = Pattern.compile(matchOn);
		Matcher m = p.matcher(pattern);
		// Will temporary hold the found separators
		List<Character> matches = new ArrayList<>();
		// Add matches to this list
		while (m.find()) {
			matches.add(m.group().charAt(0));
		}
		// Convert the char List to a char array
		char[] separators = new char[matches.size()];
		for (int i = 0; i < matches.size(); i++) {
			separators[i] = matches.get(i);
		}
		return separators;
	}

	/**
	 * Get the total character length of the solution. For 5-4,2 this will be 11
	 * (5+4+2)
	 * 
	 * @return the total length of the solution
	 */
	public int getTotalLength() {
		return totalLength;
	}

	/**
	 * Apply the correct formatting to a given solution to make it match the
	 * original solution pattern. In other words, the solution will be spaced
	 * according to the separators which are declared by the user. Solution
	 * "THEBIGDIPPER" will be recomposed to "THE BIG-DIPPER" with pattern
	 * "???,???-??????"
	 * 
	 * @param solution
	 *            - the solution to format according to the pattern
	 * @return the solution which has been manipulated to fit the solution
	 *         pattern
	 */
	public String recomposeSolution(String solution) {
		// A single word will need no manipulation
		if (!multipleWords) {
			return solution;
		}
		// This will help in recreating the solution
		StringBuilder sb = new StringBuilder(solution);
		// Separator offset
		int offset = 0, i;
		// For each separator between the sub-words of the solution
		for (i = 0; i < separators.length; i++) {
			char separator = separators[i];
			// Convert pattern characters to actual characters. e.g. a comma
			// becomes a space
			if (separator == SolutionPattern.SPACE) {
				separator = ' ';
			} else if (separator == SolutionPattern.HYPHEN) {
				// Just in case the hyphen separator character changes
				separator = '-';
			}

			// Insert the separators in the correct positions of the solution
			// (managed by an offset value)
			sb.insert(indLengths[i] + offset, separator);
			offset += indLengths[i] + 1;
		}
		return sb.toString();
	}

	public String[] separateSolution(String solution) {
		String[] indWords = new String[wordCount];

		int pos = 0;
		int i;
		for (i = 0; i < wordCount; i++) {
			indWords[i] = solution.substring(pos, pos + indLengths[i]);
			pos += indLengths[i];
		}
		return indWords;
	}

	/**
	 * Determine whether the solution pattern is modelling a multi-word solution
	 * 
	 * @return <code>true</code> if the solution being modelled is multi-worded,
	 *         <code>false</code> otherwise
	 */
	public boolean hasMultipleWords() {
		return multipleWords;
	}

	/**
	 * Get the solution pattern in the defined format. For example,
	 * "THE BIG-DIPPER" will be represented as "???,???-??????" where question
	 * marks may be replaced by known characters
	 * 
	 * @return the solution pattern modelling the solution to the clue
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Determine is a given solution matches against the solution pattern
	 * provided by the user.
	 * 
	 * @param solution
	 *            - the solution to match against the specified pattern
	 * @return <code>true</code> if the proposed solution matches the pattern,
	 *         <code>false</code> otherwise
	 */
	public boolean match(String solution) {
		solution = WordUtils.removeNonAlphabet(solution, true);
		// Assume a match until proven otherwise
		boolean match = true;

		// The lengths have to match
		if (!(solution.length() == totalLength)) {
			match = false;
		} else if (allUnknown) {
			match = true;
		} else {
			int counter = 0;
			// For the patterns representing each individual word
			outer: for (String pattern : indWordPatterns) {
				// For each character of this pattern ('?','-' or alphabet)
				for (char item : pattern.toCharArray()) {
					if (item == UNKNOWN_CHARACTER) {
						counter++;
					} else {
						// If a known character given by the user conflicts with
						// a character of the proposed solution, this is not a
						// match
						if (Character.toLowerCase(item) != Character
								.toLowerCase(solution.charAt(counter++))) {
							match = false;
							break outer;
						}
					}
				}
			}
		}
		return match;
	}

	/**
	 * Generate a solution pattern which maps to the known solution which is
	 * passed in.
	 * 
	 * @param solution
	 *            - the solution to generate a pattern for
	 * @return a solution pattern mapping to the given solution
	 */
	public static String toPattern(String solution) {
		// Solution separated by '-' and ' ', rather than '-' and ','
		final String separatorRegEx = "(\\s+|-+)";
		// Split the solution into its separate word components
		String[] words = solution.split(separatorRegEx);
		// Set the multiple word flag accordingly
		boolean multipleWords = words.length > 1;
		// Obtain an array of the separators used in the solution
		char[] separators = processSeparators(solution, separatorRegEx);

		// This is the solution pattern which will now be generated
		String pattern = "";
		// For each individual word of the solution
		for (int i = 0; i < words.length; i++) {
			String word = WordUtils.removeNonAlphabet(words[i], false);
			// Add a '?' for each character
			for (int j = 0; j < word.length(); j++) {
				pattern += UNKNOWN_CHARACTER;
			}
			// Insert the correct separators where necessary in the pattern
			if (multipleWords && i < words.length - 1) {
				switch (separators[i]) {
					case ' ':
						pattern += SPACE;
						break;
					case '-':
						pattern += HYPHEN;
						break;
					default:
						pattern += SPACE;
						break;
				}
			}
		}
		return pattern;
	}

	/**
	 * Remove words from a collection of <code>Solution</code>s that don't match
	 * against the <code>SolutionPattern</code>
	 * 
	 * @param solutions
	 *            - the collection of solutions (as <code>String</code>s to
	 *            filter
	 * @param pattern
	 *            - the <code>SolutionPattern</code> to match against TODO This
	 *            might have a better home somewhere else
	 */
	public void filterSolutions(SolutionCollection solutions) {
		Collection<Solution> toRemove = new ArrayList<>();
		// For each proposed solution
		for (Solution solution : solutions) {
			// If it doesn't match the pattern, throw it out
			if (!match(solution.getSolution())) {
				toRemove.add(solution);
			}
		}
		solutions.removeAll(toRemove);
	}

	/**
	 * A human readable representation of the <code>SolutionPattern</code>
	 */
	@Override
	public String toString() {
		return pattern;
	}

} // End of class SolutionPattern