package uk.ac.hud.cryptic.solver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Deletion solver algorithm
 * 
 * @author Leanne Butcher
 * @version 0.3
 */
public class Deletion extends Solver {
	// A readable (and DB-valid) name for the solver
	private static final String NAME = "deletion";

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
		List<String> indicators = readIndicators("assets/indicators/deletion.txt");
		String clue = c.getClue();

		boolean headIndicator = false;
		boolean tailIndicator = false;
		boolean edgeIndicator = false;
		for (String indicator : indicators) {
			switch (indicator) {
				case "*HEAD*":
					headIndicator = true;
					tailIndicator = false;
					edgeIndicator = false;
					break;
				case "*TAIL*":
					headIndicator = false;
					tailIndicator = true;
					edgeIndicator = false;
					break;
				case "*EDGES*":
					headIndicator = false;
					tailIndicator = false;
					edgeIndicator = true;
					break;
				default:
					if (clue.contains(indicator)) {
						solutions.addAll(findSynonymsToDeleteFrom(clue,
								pattern, headIndicator, tailIndicator,
								edgeIndicator));
					}
					break;
			}
		}

		pattern.filterSolutions(solutions);

		return solutions;
	}

	private SolutionCollection findSynonymsToDeleteFrom(String clue,
			SolutionPattern pattern, boolean head, boolean tail, boolean edge) {
		String[] clueWords = clue.split(WordUtils.SPACE_AND_HYPHEN);
		Set<String> synonyms = new HashSet<>();
		for (String word : clueWords) {
			synonyms.addAll(THESAURUS.getSynonymsInSameEntry(word));
		}

		filterSynonyms(synonyms, pattern);

		SolutionCollection solutions = new SolutionCollection();
		for (String synonym : synonyms) {
			if (synonym.length() > 2) {
				if (head == true || edge == true) {
					synonym = synonym.replaceFirst(synonym.substring(0, 1), "");
				}

				if (tail == true || edge == true) {
					synonym = synonym.substring(0, synonym.length() - 1);
				}

				if (DICTIONARY.isWord(synonym)) {
					solutions.add(new Solution(synonym));
				}
			}
		}
		return solutions;
	}

	private void filterSynonyms(Set<String> synonyms, SolutionPattern pattern) {
		for (Iterator<String> it = synonyms.iterator(); it.hasNext();) {
			String synonym = it.next();
			String[] syn = synonym.split(WordUtils.SPACE_AND_HYPHEN);
			if ((synonym.length() >= (pattern.getTotalLength() + 2))
					&& (synonym.length() <= pattern.getTotalLength())
					|| (syn.length != 1)) {
				it.remove();
			}
		}
	}

	private List<String> readIndicators(String indicatorFile) {
		List<String> indicators = new ArrayList<>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(indicatorFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				indicators.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return indicators;
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
		 testSolver(Deletion.class);
		// Clue c = new Clue("dog beheaded bird", "?????");
		// Clue c = new Clue("head off champion worker", "???????");
		// Clue c = new Clue("suggest not starting in a flabby way", "?????");
		// Clue c = new Clue("circuits almost falling", "????");
		// Clue c = new Clue("alter without finishing the last word", "????");
		//Clue c = new Clue("little shark edges away from diver's equipment","???");
		//Deletion s = new Deletion();
		//s.solve(c);
	}

}
