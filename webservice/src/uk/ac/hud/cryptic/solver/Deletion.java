package uk.ac.hud.cryptic.solver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Categoriser;
import uk.ac.hud.cryptic.util.Util;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Deletion solver algorithm
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.3
 */
public class Deletion extends Solver {
	// A readable (and DB-valid) name for the solver
	private static final String NAME = "deletion";

	// Indicator headings
	private static final String HEAD = "0head0";
	private static final String TAIL = "0tail0";
	private static final String EDGE = "0edges0";

	private enum Position {
		HEAD("the first letter"), TAIL("the last letter"), EDGE(
				"the first and last letters"), NONE("");

		private final String text;

		Position(String text) {
			this.text = text;
		}

		private String getText() {
			return text;
		}
	}

	/**
	 * Default constructor for solver class
	 */
	public Deletion() {
		super();
	}

	/**
	 * Constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Deletion(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		SolutionCollection solutions = new SolutionCollection();
		final SolutionPattern pattern = c.getPattern();
		Collection<String> indicators = Categoriser.getInstance()
				.getIndicators(NAME);
		String clue = c.getClue();

		Position position = Position.NONE;

		loop: for (String indicator : indicators) {
			switch (indicator) {
				case HEAD:
					position = Position.HEAD;
					break;
				case TAIL:
					position = Position.TAIL;
					break;
				case EDGE:
					position = Position.EDGE;
					break;
				default:
					if (clue.contains(indicator)) {
						solutions.addAll(findSynonymsToDeleteFrom(c, pattern,
								position, indicator));
						break loop;
					}
					break;
			}
		}
		pattern.filterSolutions(solutions);

		return solutions;
	}

	private SolutionCollection findSynonymsToDeleteFrom(Clue clue,
			SolutionPattern pattern, Position position, String indicator) {
		Map<String, Set<String>> synonyms = new HashMap<>();
		for (String word : clue.getClueWords()) {
			synonyms.put(word, THESAURUS.getSynonymsInSameEntry(word));
		}

		filterSynonyms(synonyms, pattern);

		SolutionCollection solutions = new SolutionCollection();
		for (Entry<String, Set<String>> entry : synonyms.entrySet()) {
			for (final String synonym : entry.getValue()) {
				String solution = synonym;
				if (solution.length() > 2) {
					if (position == Position.HEAD || position == Position.EDGE) {
						solution = solution.substring(1);
					}

					if (position == Position.TAIL || position == Position.EDGE) {
						solution = solution.substring(0, solution.length() - 1);
					}

					if (DICTIONARY.isWord(solution) && pattern.match(solution)) {
						Solution s = new Solution(solution, NAME);
						s.addToTrace("Synonym is \"" + synonym
								+ "\", from clue word \"" + entry.getKey()
								+ "\"");
						s.addToTrace("Take the clue word \"" + entry.getKey()
								+ "\" and get its synonym \"" + synonym + "\".");
						s.addToTrace("The clue contains the indicator \""
								+ indicator + "\", which means to remove "
								+ position.getText() + " of \"" + synonym
								+ "\".");

						solutions.add(s);
					}
				}
			}
		}
		return solutions;
	}

	private void filterSynonyms(Map<String, Set<String>> synonyms,
			SolutionPattern pattern) {
		// This way rather than iterators to avoid
		// ConcurrentModificationExceptions :(
		Map<String, Set<String>> toRemove = new HashMap<>();
		for (Entry<String, Set<String>> entry : synonyms.entrySet()) {
			for (String synonym : entry.getValue()) {
				String[] syn = synonym.split(WordUtils.SPACE_AND_HYPHEN);
				if (synonym.length() >= pattern.getTotalLength() + 2
						&& synonym.length() <= pattern.getTotalLength()
						|| syn.length != 1) {
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

	private static void tagDB() {
		Deletion d = new Deletion();

		InputStream is = Settings.class.getResourceAsStream("/cryptic.csv");

		try (ICsvListReader reader = new CsvListReader(
				new InputStreamReader(is), CsvPreference.STANDARD_PREFERENCE)) {

			List<String> line;
			while ((line = reader.read()) != null) {
				String clue = WordUtils.normaliseInput(line.get(0), false);
				String solution = WordUtils.normaliseInput(line.get(1), true);

				Clue c = new Clue(clue, SolutionPattern.toPattern(solution,
						false), solution, NAME);
				SolutionCollection sc = d.solve(c);
				if (sc.contains(solution)) {
					System.out
							.println("UPDATE `cryptic_clues` SET `type`='deletion' WHERE `clue` = \""
									+ line.get(0).trim()
									+ "\" AND `solution` = \""
									+ line.get(1).trim()
									+ "\" AND `type` IS NULL;");
					System.out.println("-- "
							+ sc.getSolution(solution).getSolutionTrace() + " ("
							+ sc.size() + " solution"
							+ (sc.size() > 1 ? "s)" : ")"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Entry point to the code for testing purposes
	 */
	public static void main(String[] args) {
		// tagDB();
		testSolver(Deletion.class);
		// Clue c = new Clue(
		// "'the church' is incomplete as the address for the priest",
		// "????", "ABBE", NAME);
		// Clue c = new Clue("dog beheaded bird", "?????");
		// Clue c = new Clue("head off champion worker", "???????");
		// Clue c = new Clue("suggest not starting in a flabby way", "?????");
		// Clue c = new Clue("circuits almost falling", "????");
		// Clue c = new Clue("alter without finishing the last word", "????");
		// Clue("little shark edges away from diver's equipment","???");
		// Deletion s = new Deletion();
		// s.solve(c);
	}

} // End of class Deletion
