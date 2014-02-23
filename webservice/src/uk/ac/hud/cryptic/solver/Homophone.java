package uk.ac.hud.cryptic.solver;

import java.util.Arrays;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Categoriser;
import uk.ac.hud.cryptic.resource.HomophoneDictionary;
import uk.ac.hud.cryptic.resource.Thesaurus;
import uk.ac.hud.cryptic.util.Confidence;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Homophone solver algorithm
 * 
 * @author Stuart Leader
 * @version 0.2
 */
public class Homophone extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "homophone";
	// A link to the homophone dictionary
	private static final HomophoneDictionary HOMOPHONE_DICT = HomophoneDictionary
			.getInstance();

	/**
	 * Default constructor for solver class
	 */
	public Homophone() {
		super();
	}

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Homophone(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {

		SolutionCollection solutions = new SolutionCollection();

		final SolutionPattern pattern = c.getPattern();

		// Remove indicator word(s) from the clue to decrease the solve time
		String clue = c.getClueNoPunctuation(false);
		clue = Categoriser.getInstance().removeIndicatorWords(clue, NAME);
		String[] words = clue.split(WordUtils.REGEX_WHITESPACE);

		// Find direct homonyms
		solutions.addAll(findDirectHomonyms(words));

		// Find homonyms of synonyms
		solutions.addAll(findSynonymHomonyms(words));

		// Remove risk of matching original words
		solutions.removeAllStrings(Arrays.asList(c.getClueWords()));

		// Remove solutions which don't match the provided pattern
		pattern.filterSolutions(solutions);

		// Filter out invalid words
		DICTIONARY.dictionaryFilter(solutions, pattern);

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, solutions);

		return solutions;
	}

	private SolutionCollection findDirectHomonyms(String[] words) {
		SolutionCollection solutions = new SolutionCollection();

		for (String word : words) {
			Set<String> homonyms = HOMOPHONE_DICT.getHomonyms(word);
			for (String homonym : homonyms) {
				Solution s = new Solution(homonym, NAME);
				s.addToTrace("Pronunciation of " + word + " matches with "
						+ homonym);
				// Adjust the solution's confidence
				double confidence = Confidence.multiply(s.getConfidence(),
						Confidence.HOMOPHONE_MULTIPLIER);
				s.setConfidence(confidence);
				solutions.add(s);
			}
		}
		return solutions;
	}

	private SolutionCollection findSynonymHomonyms(String[] words) {
		SolutionCollection solutions = new SolutionCollection();

		for (String word : words) {
			// Find the synonyms of the word
			Set<String> synonyms = THESAURUS.getSynonyms(word);
			for (String synonym : synonyms) {
				Set<String> homonyms = HOMOPHONE_DICT.getHomonyms(synonym);
				for (String homonym : homonyms) {
					Solution s = new Solution(homonym, NAME);
					s.addToTrace("Pronunciation of \"" + synonym
							+ "\" (synonym of " + word + ") matches with \""
							+ homonym + "\"");
					// Adjust the solution's confidence
					double confidence = Confidence.multiply(s.getConfidence(),
							Confidence.HOMOPHONE_MULTIPLIER);
					s.setConfidence(confidence);
					solutions.add(s);
				}
			}
		}
		return solutions;
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
		testSolver(Homophone.class);
		// Clue c = new Clue("way said to be problematic for lame dog", "?????",
		// "style", NAME);
		// Homophone h = new Homophone();
		// h.solve(c);
	}

} // End of class Homophone

