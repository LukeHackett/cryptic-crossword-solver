package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.Confidence;

/**
 * Double definition solver algorithm
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.2
 */
public class DoubleDefinition extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "double definition";
	private SolutionCollection solutions;

	/**
	 * Default constructor for solver class
	 */
	public DoubleDefinition() {
		super();
	}

	/**
	 * Constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public DoubleDefinition(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();

		// Get all clue words
		String[] words = c.getClueWords();

		// First-level synonyms
		Map<String, Collection<String>> firstSynonyms = new HashMap<>();

		// Second-level synonyms
		Map<String, Collection<String>> secondSynonyms = new HashMap<>();

		// Populate the synonym maps
		for (String word : words) {
			firstSynonyms.put(word,
					THESAURUS.getMatchingSynonyms(word, pattern));
			secondSynonyms.put(word,
					THESAURUS.getSecondSynonyms(word, pattern, false));
		}

		// Look for matches... this is where it gets messy

		// First-first matching
		solutions.addAll(findSynonymMatches(firstSynonyms, firstSynonyms));

		// First-second (or vice-versa) matching
		solutions.addAll(findSynonymMatches(firstSynonyms, secondSynonyms));

		// Note no second-second matching. That's too vague.

		return solutions;
	}

	/**
	 * Using maps of synonyms compiled previously, determine if two words of the
	 * clue share a common synonym. If so, add it as a solution and return it.
	 * 
	 * @param map1
	 *            - The first map of synonyms to analyse
	 * @param map2
	 *            - The second map of synonyms to analyse
	 * @return a <code>SolutionCollection</code> of any found solutions
	 */
	private SolutionCollection findSynonymMatches(
			Map<String, Collection<String>> map1,
			Map<String, Collection<String>> map2) {

		// This will be returned, containing any found solutions
		SolutionCollection sc = new SolutionCollection();

		// Don't look at this mess. You will die a little inside.
		// For each word (key) in the first map passed in
		for (Entry<String, Collection<String>> e1 : map1.entrySet()) {
			// For each word (key) in the second map passed in
			for (Entry<String, Collection<String>> e2 : map2.entrySet()) {
				// Make sure the words are difference
				if (!e1.getKey().equals(e2.getKey())) {
					// For each synonym of these distinct clue words
					for (String synonym1 : e1.getValue()) {
						for (String synonym2 : e2.getValue()) {
							// If these different words of the clue share a
							// common synonym, woohoo.
							if (synonym1.equals(synonym2)) {
								Solution s = new Solution(synonym1, NAME);
								s.setConfidence(Confidence.DOUBLE_DEFINITION_INITIAL);

								// Compile a detailed trace message
								String trace = "The words of the clue, ";
								trace += "\"" + e1.getKey() + "\" and ";
								trace += "\"" + e2.getKey() + "\"";
								trace += " share the synonym \"" + synonym1
										+ "\".";
								s.addToTrace(trace);

								// If both maps are equal, i.e. first and
								// first-level synonyms,
								// increase confidence
								// Yes, ==, as we want to compare object
								// references
								if (map1 == map2) {
									double confidence = Confidence.multiply(
											s.getConfidence(),
											Confidence.CATEGORY_MULTIPLIER);
									s.setConfidence(confidence);
									s.addToTrace("Confidence rating increased as this is a direct synonym of two of the clue's words.");
								}
								sc.add(s);
							}
						}
					}
				}
			}
		}

		return sc;
	}

	@Override
	public String toString() {
		return NAME;
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		testSolver(DoubleDefinition.class);
	}

} // End of class DoubleDefinition
