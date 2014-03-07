package uk.ac.hud.cryptic.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import uk.ac.hud.cryptic.resource.Thesaurus;
import uk.ac.hud.cryptic.solver.Solver;
import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Reversal solver algorithm that utilises a small amount of NLP to cut down the
 * search space when solving clue.
 * 
 * @author Luke Hackett
 * @version 0.3
 */
public class Reversal extends Solver {
	// A readable (and DB-valid) name for the solver
	private static final String NAME = "reversal";
	// Settings instance
	private Settings settings = Settings.getInstance();

	/**
	 * Default constructor for the reversal solver class
	 */
	public Reversal() {
		super();
	}

	/**
	 * Constructor for the reversal solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Reversal(Clue clue) {
		super(clue);
	}

	/**
	 * This method solves the give clue returning a collection of possible
	 * solutions, if any.
	 * 
	 * @param c
	 *            - the clue to solve
	 * @return a <code>Collection</code> of potential solutions
	 */
	@Override
	public SolutionCollection solve(Clue c) {
		// Contains all the possible solutions to the clue
		SolutionCollection collection = new SolutionCollection();

		// Reverse the pattern
		String reversedPattern = WordUtils.reverseWord(c.getPattern()
				.getPattern());
		SolutionPattern pattern = new SolutionPattern(reversedPattern);

		// Get the possible fodders
		String[] fodders = getFodders(c);

		// Loop over all calculated fodders
		for (String fodder : fodders) {
			// Get all synonyms that match the reversed pattern
			Set<String> synonyms = THESAURUS.getSecondSynonyms(fodder, pattern,
					true);

			// Add the fodder as a synonym as it could contain the answer
			synonyms.add(fodder);

			// Reverse all synonyms to try to create another word
			for (String synonym : synonyms) {
				// Reverse the String
				String reversedWord = WordUtils.reverseWord(synonym);

				// Add as a solution if the reversed word is a real word
				if (DICTIONARY.isWord(reversedWord)) {
					collection.add(new Solution(reversedWord, NAME));
				}
			}
		}

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(c, collection);

		return collection;
	}

	/**
	 * This method will return an array of possible Fodders based upon the given
	 * clue. A reversal fodder is normally a singular or plural noun, but may be
	 * a number of words.
	 * 
	 * @param c
	 *            - the clue to solve
	 * @return An array of possible fodders
	 */
	private String[] getFodders(Clue clue) {
		// A list of possible fodders
		List<String> fodders = new ArrayList<String>();

		try {
			// Create a new POSModel
			POSModel model = new POSModel(settings.getPOSModelStream());

			// Apply to the tagger
			POSTaggerME tagger = new POSTaggerME(model);

			// Split on all whitespace
			String words[] = WordUtils.getWords(clue.getClue());

			// Get a list of the tags
			String[] tags = tagger.tag(words);

			// This is a guess...
			for (int i = 0; i < tags.length; i++) {
				// ...Fodders are marked with 'NN' or 'NNS'... usually
				if (tags[i].equals("NN") || tags[i].equals("NNS")) {
					fodders.add(words[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Return the fodders as a String array
		return fodders.toArray(new String[fodders.size()]);
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
		testSolver(Reversal.class);
	}

}
