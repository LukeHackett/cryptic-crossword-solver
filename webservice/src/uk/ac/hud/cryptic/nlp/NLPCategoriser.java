package uk.ac.hud.cryptic.nlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import uk.ac.hud.cryptic.config.Settings;

public class NLPCategoriser {

	private static NLPCategoriser instance;
	private final Settings settings = Settings.getInstance();
	private DocumentCategorizerME categoriser;

	/**
	 * Initialise the parser by loading in the pre-trained categoriser model
	 */
	private NLPCategoriser() {
		try {
			InputStream is = settings.getCategoriserModelStream();
			DoccatModel m = new DoccatModel(is);
			categoriser = new DocumentCategorizerME(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtain a map of the probabilities that the given clue belongs to a
	 * particular clue type.
	 * 
	 * @param clue
	 *            - The clue to categorise using NLP
	 * @return A map of the probabilities in the format <Anagram, 0.986>
	 */
	public Map<String, Double> categorise(String clue) {

		double[] outcomes = categoriser.categorize(clue);

		// Print out the best category
		// String bestCategory = categoriser.getBestCategory(outcomes);
		// System.out.println(clue + ", Type: " + bestCategory);

		Map<String, Double> probabilities = new HashMap<>();
		int i;
		for (i = 0; i < outcomes.length; i++) {
			String category = categoriser.getCategory(i);
			probabilities.put(category, outcomes[i]);
			// System.out.println("Putting \"" + category
			// + "\" with probability \"" + outcomes[i] + "\".");
		}
		return probabilities;
	}

	/**
	 * Get the best clue type for a given clue.
	 * 
	 * @param clue
	 *            - The clue to get the best category for
	 * @return the best fitting category
	 */
	public String getBestCategory(String clue) {
		double[] outcomes = categoriser.categorize(clue);
		return categoriser.getBestCategory(outcomes);
	}

	/**
	 * Retrieve the one and only instance of the NLPCategoriser (Singleton
	 * design pattern)
	 * 
	 * @return the NLP categoriser
	 */
	public static NLPCategoriser getInstance() {
		if (instance == null) {
			instance = new NLPCategoriser();
		}
		return instance;
	}

	/**
	 * A main method for testing purposes
	 */
	public static void main(String[] args) {
		NLPCategoriser cat = NLPCategoriser.getInstance();
		cat.categorise("A dung pit removed during modernisation");
		cat.categorise("Performing masculine arts, do oneself an injury!");
		cat.categorise("Something or other going from back to front");
		cat.categorise("Something about every other character pattern");
	}

} // End of class NLPCategoriser
