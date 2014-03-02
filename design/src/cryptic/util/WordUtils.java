package cryptic.util;
import java.util.*;

import cryptic.resource.Dictionary;
import cryptic.solver.SolutionCollection;
public class WordUtils {

	// Will match anything that isn't [A-Z] or [a-z] including spaces
	public static final String REGEX_NON_LETTERS_SPACES = "(\\W|_|[0-9])+";

	// Will match anything that isn't [A-Z] or [a-z] excluding spaces
	public static final String REGEX_NON_LETTERS = "[^A-Za-z\\s]+";

	// Will match all characters likely used to separate solution lengths
	public static final String REGEX_SEPARATORS = "(,|\\.|\\s|-|:|;|\\+)+";

	// Will match anything other than [0-9]
	public static final String REGEX_NON_NUMERIC = "\\D+";
	
	private static final Dictionary DICT = Dictionary.getInstance();



	public static String removeNonAlphabet(String input, boolean removeSpaces) {
		return "";
	}

	public static void dictionaryFilter(SolutionCollection solutions) {

	}
}
