package uk.ac.hud.cryptic.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object modelling the solution to the corresponding clue. This is done
 * using details provided by the user, such as the known word length(s).
 */
public class SolutionPattern {

	// The constants of a solution pattern
	public static final char SPACE = ',';
	public static final char HYPHEN = '-';
	public static final char UNKNOWN_CHARACTER = '_';

	// As inputted by the user. e.g. "_a__e,___d-__"
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

	private void calculate(String pattern) {
		// Obtain the individual word patterns
		indWordPatterns = pattern.split(WordUtils.REGEX_SEPARATORS);
		multipleWords = indWordPatterns.length > 1;
		if (multipleWords) {
			processSeparators();
		}

		wordCount = indWordPatterns.length;
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

	private void processSeparators() {
		Pattern p = Pattern.compile(WordUtils.REGEX_SEPARATORS);
		Matcher m = p.matcher(pattern);
		List<Character> matches = new ArrayList<>();
		while (m.find()) {
			matches.add(m.group().charAt(0));
		}
		separators = new char[matches.size()];
		for (int i = 0; i < matches.size(); i++) {
			separators[i] = matches.get(i);
		}
	}

	public int getTotalLength() {
		return totalLength;
	}

	public String getRawLength() {
		return pattern;
	}

	public String recomposeSolution(String solution) {
		if (!multipleWords) {
			return solution;
		}
		// e.g. "THEBIGDIPPER" to "THE BIG-DIPPER"
		StringBuilder sb = new StringBuilder(solution);
		// Separator offset
		int offset = 0;
		int i;
		for (i = 0; i < separators.length; i++) {

			char separator = separators[i];
			if (separator == SolutionPattern.SPACE) {
				separator = ' ';
			}

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

	public boolean hasMultipleWords() {
		return multipleWords;
	}

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

		if (!(solution.length() == totalLength)) {
			match = false;
		} else {
			int counter = 0;
			outer: for (String pattern : indWordPatterns) {
				for (char item : pattern.toCharArray()) {
					if (item == UNKNOWN_CHARACTER) {
						counter++;
					} else {
						if (item != solution.charAt(counter++)) {
							match = false;
							break outer;
						}
					}
				}
			}
		}
		return match;
	}
} // End of class SolutionPattern
