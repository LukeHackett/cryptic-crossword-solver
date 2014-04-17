package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Categoriser;
import uk.ac.hud.cryptic.util.Util;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Container solver algorithm ------------Example------------- Clue - Stash or
 * put in stage (7) Answer - Storage --------------------------------
 * 
 * @authors Mohammad Rahman, Leanne Butcher, Stuart Leader
 * @version 0.1
 */
public class Container extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "container";

	// Indicator headings
	private static final String LINR = "0linr0";
	private static final String RINL = "0rinl0";

	/**
	 * Default constructor for solver class
	 */
	public Container() {
		super();
	}

	/**
	 * Enum for words on either side of an indicator
	 */
	private enum Position {
		RINL("right in left"), LINR("left in right"), NONE("");

		// Position of word
		private final String text;

		// Set position of word
		Position(String text) {
			this.text = text;
		}

		// Get position of word
		@SuppressWarnings("unused")
		private String getText() {
			return text;
		}
	}

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Container(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		SolutionCollection solutions = new SolutionCollection();

		// Get clue pattern
		final SolutionPattern pattern = c.getPattern();

		// Get clue with no punctuation
		String clue = c.getClueNoPunctuation(true);

		// Read in indicators for container clue types
		Collection<String> indicators = Categoriser.getInstance()
				.getIndicators(NAME);

		// Set the initial position of a word
		Position position = Position.NONE;

		// Look through indicators to determine which word
		// (left or right of indicator) goes into the other
		// words (left or right of indicator)
		loop: for (String indicator : indicators) {
			switch (indicator) {
				case RINL:
					// Word right of indicator goes into left of indicator
					position = Position.RINL;
					break;
				case LINR:
					// Word left of indicator goes into right of indicator
					position = Position.LINR;
					break;
				default:
					// Check if the clue contains the indicator being checked
					if (clue.contains(indicator)) {
						// Find solutions
						solutions.addAll(findSynonyms(c, pattern, position,
								indicator));
						break loop;
					}
					break;
			}
		}

		pattern.filterSolutions(solutions);

		return solutions;
	}

	/**
	 * A method which finds all synonyms of the words in the given clue
	 * 
	 * @param c
	 *            the clue
	 * @param pattern
	 *            the pattern of the clue
	 * @param position
	 *            whether the left word is in right or vice versa
	 * @param indicator
	 *            the indicator word from the clue
	 * @return the solutions
	 */
	private SolutionCollection findSynonyms(Clue c, SolutionPattern pattern,
			Position position, String indicator) {

		// To store the solutions
		SolutionCollection solutions = new SolutionCollection();

		// Separate the words for fast comparison
		String[] cluewords = c.getClueWords();

		// To store the synonyms of each word
		Map<String, Set<String>> synonyms = new HashMap<>();

		// Retrieve the synonyms of each word
		for (String word : cluewords) {
			synonyms.put(word, THESAURUS.getSynonyms(word));
			synonyms.get(word).add(word);
		}

		filterSynonyms(synonyms, pattern);

		solutions = matchUpSynonyms(synonyms, position, pattern);

		return solutions;
	}

	/**
	 * A method to match synonyms of each word on either side of the indicator
	 * 
	 * @param synonyms
	 *            the synonyms of each word
	 * @param position
	 *            which word contains the other
	 * @param pattern
	 *            the solution pattern
	 * @return the matching synonyms
	 */
	private SolutionCollection matchUpSynonyms(
			Map<String, Set<String>> synonyms, Position position,
			SolutionPattern pattern) {

		// Store the solutions
		SolutionCollection solutions = new SolutionCollection();

		// Nested loop to iterate over all synonyms
		for (Entry<String, Set<String>> outerEntry : synonyms.entrySet()) {
			for (Entry<String, Set<String>> innerEntry : synonyms.entrySet()) {
				if (!(outerEntry == innerEntry)) {
					for (String outerString : outerEntry.getValue()) {
						for (String innerString : innerEntry.getValue()) {
							containWord(solutions, outerEntry.getKey(),
									outerString, innerEntry.getKey(),
									innerString, position, pattern);
						}
					}
				}
			}
		}

		return solutions;
	}

	/**
	 * A method to check the processed word is in the dictionary
	 * 
	 * @param solutions
	 *            the solutions
	 * @param firstClue
	 * @param firstWord
	 *            the word before the indicator
	 * @param secondClue
	 * @param secondWord
	 *            the word after the indicator
	 * @param position
	 *            which word goes into which
	 * @param pattern
	 *            the solution pattern
	 */
	private void containWord(SolutionCollection solutions, String firstClue,
			String firstWord, String secondClue, String secondWord,
			Position position, SolutionPattern pattern) {

		if (firstWord.length() + secondWord.length() == pattern
				.getTotalLength()) {
			// If first word is going into second word
			if (position == Position.LINR) {
				for (int i = 1; i < secondWord.length(); i++) {
					StringBuilder sb = new StringBuilder(secondWord);
					sb.insert(i, firstWord);
					if (DICTIONARY.isWord(sb.toString())) {
						Solution s = new Solution(sb.toString(), NAME);
						s.addToTrace(generateTrace(secondClue, secondWord));
						s.addToTrace(generateTrace(firstClue, firstWord));
						s.addToTrace(String
								.format("Insert the word \"%s\" into the middle of \"%s\".",
										firstWord, secondWord));
						solutions.add(s);
					}
				}
			}
			// If second word is going into first word
			else if (position == Position.RINL) {
				for (int i = 1; i < firstWord.length(); i++) {
					StringBuilder sb = new StringBuilder(firstWord);
					sb.insert(i, secondWord);
					if (DICTIONARY.isWord(sb.toString())) {
						Solution s = new Solution(sb.toString(), NAME);
						s.addToTrace(generateTrace(firstClue, firstWord));
						s.addToTrace(generateTrace(secondClue, secondWord));
						s.addToTrace(String
								.format("Insert the word \"%s\" into the middle of \"%s\".",
										secondWord, firstWord));
						solutions.add(s);
					}
				}
			}
		}
	}

	/**
	 * A method to generate the trace of how the solution was found
	 * 
	 * @param clueWord
	 * @param synonym
	 * @return the trace message
	 */
	private String generateTrace(String clueWord, String synonym) {
		boolean same = clueWord.equals(synonym);
		String message = "Take the ";
		message += same ? "clue word " : "synonym ";
		message += "\"" + synonym + "\"";
		if (!same) {
			message += " of clue word \"" + clueWord + "\".";
		} else {
			message += ".";
		}
		return message;
	}

	/**
	 * A method to filter out synonyms that have a character length greater than
	 * length of the solution
	 * 
	 * @param synonyms
	 *            the synonyms of each word in the clue
	 * @param pattern
	 *            the solution pattern
	 */
	private void filterSynonyms(Map<String, Set<String>> synonyms,
			SolutionPattern pattern) {

		// To avoid ConcurrentModificationExceptions :(
		Map<String, Set<String>> toRemove = new HashMap<>();

		// Identify what to remove
		for (Entry<String, Set<String>> entry : synonyms.entrySet()) {
			for (String synonym : entry.getValue()) {
				String[] syn = synonym.split(WordUtils.SPACE_AND_HYPHEN);
				if (synonym.length() > pattern.getTotalLength()
						|| syn.length > 1) {
					Util.addToMap(toRemove, entry.getKey(), synonym,
							HashSet.class);
				}
			}
		}
		// Now remove those which are not valid
		for (Entry<String, Set<String>> entry : toRemove.entrySet()) {
			for (String synonym : entry.getValue()) {
				synonyms.get(entry.getKey()).remove(synonym);
			}
		}
	}

	/**
	 * Get the database name for this type of clue
	 * 
	 * @return the database name for this type of clue
	 */
	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(Container.class);
	}

} // End of class Container
