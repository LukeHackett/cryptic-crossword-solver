package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

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

		// List for all synonyms
		Map<String, List<String>> allSynonyms = new HashMap<>();

		for (String clueWord : words) {
			// Get first and second-level synonyms for each word
			Set<String> synonyms = THESAURUS.getSecondSynonyms(clueWord,
					pattern, true);
			// Add these synonyms to a list, making note of which clue word they
			// correspond to
			for (String synonym : synonyms) {
				// Add to the list if already present
				if (allSynonyms.containsKey(synonym)) {
					allSynonyms.get(synonym).add(clueWord);
				} else {
					// Otherwise create a list to use as the map's value
					List<String> mapWords = new ArrayList<>();
					mapWords.add(clueWord);
					allSynonyms.put(synonym, mapWords);
				}
			}
		}

		// Check for synonyms added to list twice
		checkForDoubles(allSynonyms, pattern);

		return solutions;
	}

	public void checkForDoubles(Map<String, List<String>> synonyms,
			SolutionPattern pattern) {

		// Iterate through all the synonyms found for the clue words
		for (Entry<String, List<String>> entry : synonyms.entrySet()) {
			// Proceed only if the synonym matches more than one clue word
			int matchCount = entry.getValue().size();
			if (matchCount > 1) {
				// Add to solutions
				Solution s = new Solution(entry.getKey(), NAME);

				// Compile a detailed trace message
				String trace = matchCount + " words of the clue, ";
				for (int i = 0; i < matchCount; i++) {
					trace += "\"" + entry.getValue().get(i) + "\"";
					if (i != matchCount - 1) {
						trace += " and ";
					}
				}
				trace += " share the synonym \"" + entry.getKey() + "\".";

				s.addToTrace(trace);
				// Add this to the master list of solutions
				solutions.add(s);
			}
		}
	}

	@Override
	public String toString() {
		return NAME;
	}

} // End of class DoubleDefinition
