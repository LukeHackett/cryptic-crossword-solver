package uk.ac.hud.cryptic.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object modelling the solution to the corresponding clue. This is done
 * using details provided by the user, such as the known word length(s).
 */
// TODO Allow the modelling of known letters, and create a method (in WordUtils)
// to then filter out the potential solutions based on these
public class SolutionStructure {

	// As inputted by the user. e.g. "5,4-2"
	private final String rawLength;
	// Solution is comprised of this many words
	private int wordCount;
	// Individual word lengths. e.g. [ 5 , 4 , 2 ]
	private int[] indLengths;
	// The characters present between words. e.g. [ "," , "-" ]
	private String[] separators;
	// Total number of characters in the solution. e.g. For 5,4-2 = 11
	private int totalLength;
	// Does the solution comprise of a single or multiple words?
	private boolean multipleWords;

	/**
	 * Constructor takes in the user input of what is known of the solution
	 * (e.g. its character lengths) and performs useful calculation based on
	 * this data such as the total character length of the solution and the
	 * number of words it comprises of.
	 * 
	 * @param rawLength
	 *            - the user input of the known parameters of the clue's
	 *            solution
	 */
	public SolutionStructure(String rawLength) {
		this.rawLength = rawLength;
		calculate(rawLength);

	}

	private void calculate(String rawLength) {
		// Obtain the individual lengths
		String[] indWordLengths = rawLength.split(WordUtils.REGEX_SEPARATORS);
		multipleWords = indWordLengths.length > 1;
		if (multipleWords) {
			processSeparators();
		}

		wordCount = indWordLengths.length;
		indLengths = new int[wordCount];

		// Get a cumulative total
		int i = 0;
		for (i = 0; i < wordCount; i++) {
			String indWordLength = indWordLengths[i];

			try {
				int lengthValue = Integer.parseInt(indWordLength);
				totalLength += lengthValue;
				indLengths[i] = lengthValue;
			} catch (NumberFormatException e) {
				System.err.println("Cannot parse solution length ("
						+ indWordLength + ")");
			}
		}
	}

	private void processSeparators() {
		Pattern p = Pattern.compile(WordUtils.REGEX_NON_NUMERIC);
		Matcher m = p.matcher(rawLength);
		Collection<String> matches = new ArrayList<String>();
		while (m.find()) {
			matches.add(m.group());
		}
		separators = matches.toArray(new String[matches.size()]);
	}

	public int getTotalLength() {
		return totalLength;
	}

	public String getRawLength() {
		return rawLength;
	}

	public String recomposeSolution(String solution) {
		if (!multipleWords) {
			return solution.toUpperCase();
		}
		// e.g. "THEBIGDIPPER" to "THE BIG-DIPPER"
		StringBuilder sb = new StringBuilder(solution);
		// Separator offset
		int offset = 0;
		int i;
		for (i = 0; i < separators.length; i++) {

			String separator = separators[i];
			if (separator.length() > 1) {
				separator = separator.trim();
			}
			if (separator.equals(",")) {
				separator = " ";
			}

			sb.insert(indLengths[i] + offset, separator);
			offset += indLengths[i] + separator.length();
		}
		return sb.toString().toUpperCase();
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

} // End of class SolutionStructure
