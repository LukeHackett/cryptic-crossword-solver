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
 * Container solver algorithm 
 * ------------Example-------------
 * Clue - Stash or put in stage (7) Answer - Storage 
 * --------------------------------
 * 
 * @author Mohammad Rahman
 * @version 0.1
 */
public class Container extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "container";
	private static final String LINR = "0linr0";
	private static final String RINL = "0rinl0";

	/**
	 * Default constructor for solver class
	 */
	public Container() {
		super();
	}

	/**
	 * Enum for the positions of letter to delete
	 */
	private enum Position {
		RINL("right in left"), LINR("left in right"), NONE("");

		// Position of letter
		private final String text;

		// Set position of letter to delete
		Position(String text) {
			this.text = text;
		}

		// Get position of letter to delete
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

		// Set the initial position of letter to be deleted to none
		Position position = Position.NONE;

		// Look through indicators to determine the position of the letter which
		// should be removed
		loop: for (String indicator : indicators) {
			switch (indicator) {
				case RINL:
					// First letter
					position = Position.RINL;
					break;
				case LINR:
					// Last letter
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

	private Collection<? extends Solution> findSynonyms(Clue c,
			SolutionPattern pattern, Position position, String indicator) {

		SolutionCollection solutions = new SolutionCollection();

		// Separate the words for fast comparison
		String[] cluewords = c.getClueWords();

		Map<String, Set<String>> synonyms = new HashMap<>();
		for (String word : cluewords) {
			synonyms.put(word,
					THESAURUS.getSynonyms(word));
			synonyms.get(word).add(word);
		}

		filterSynonyms(synonyms, pattern);

		solutions = matchUpSynonyms(synonyms, position, pattern);

		return solutions;
	}

	private SolutionCollection matchUpSynonyms(
			Map<String, Set<String>> synonyms, Position position,
			SolutionPattern pattern) {

		SolutionCollection solutions = new SolutionCollection();

		for (Entry<String, Set<String>> outerEntry : synonyms.entrySet()) {
			for (Entry<String, Set<String>> innerEntry : synonyms.entrySet()) {
				if (!(outerEntry == innerEntry)) {
					for (String outerString : outerEntry.getValue()) {
						for (String innerString : innerEntry.getValue()) {
							containWord(solutions, outerString, innerString,
									position, pattern);
						}
					}
				}
			}
		}

		return solutions;
	}

	private void containWord(SolutionCollection solutions, String firstWord,
			String secondWord, Position position, SolutionPattern pattern) {
		if(firstWord.equals("stage") && secondWord.equals("or") || (secondWord.equals("stage")
				&& (firstWord.equals("or")))) {

		}

		if ((firstWord.length() + secondWord.length()) == pattern.getTotalLength()) {
			// If first word is going into second word
			if (position == Position.LINR) {
				for (int i = 1; i < secondWord.length(); i++) {
					StringBuilder sb = new StringBuilder(secondWord);
					sb.insert(i, firstWord);
					if (DICTIONARY.isWord(sb.toString())) {
						solutions.add(new Solution(sb.toString(), NAME));
					}
				}
			}
			// If second word is going into first word
			else if (position == Position.RINL) {
				for (int i = 1; i < firstWord.length(); i++) {
					StringBuilder sb = new StringBuilder(firstWord);
					sb.insert(i, secondWord);
					if (DICTIONARY.isWord(sb.toString())) {
						solutions.add(new Solution(sb.toString(), NAME));
					}
				}
			}
		}
	}

	private void filterSynonyms(Map<String, Set<String>> synonyms,
			SolutionPattern pattern) {
		// This way rather than iterators to avoid
		// ConcurrentModificationExceptions :(
		Map<String, Set<String>> toRemove = new HashMap<>();

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

	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(Container.class);
		//Clue c = new Clue("Wear around the brave", "???????");
		//Container co = new Container();
		//co.solve(c);
	}

} // End of class Container
