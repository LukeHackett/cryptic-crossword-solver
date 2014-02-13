package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.config.Settings.ResourceType;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.util.Confidence;
import uk.ac.hud.cryptic.util.WordUtils;

public class Categoriser {

	// Dictionary Instance
	private static Categoriser instance;
	// Settings Instance
	private static Settings settings = Settings.getInstance();
	// Data structure containing indicator words
	private Map<String, Collection<String>> indicators;

	/**
	 * Default Constructor
	 */
	private Categoriser() {
		populateIndicatorsFromFile();
	}

	/**
	 * Load the indicators into a HashSet to allow for much faster access
	 */
	private void populateIndicatorsFromFile() {
		URL url = settings.getIndicatorsURL(ResourceType.ASSET, "indicators");

		// Instantiate the dictionary object
		indicators = new HashMap<>();

		if (url == null) {
			System.err.println("Cannot find indicators (assets) directory");
		} else {
			try {
				File dir = new File(url.toURI());
				for (File file : dir.listFiles()) {
					// Don't read the source file
					if ("source.txt".equals(file.getName())) {
						continue;
					}
					// Get the key (look-up) word
					String solverType = settings.getFileName(file.getName(),
							"txt");
					// This will hold the indicators
					Collection<String> words = new ArrayList<>();

					try (BufferedReader br = new BufferedReader(new FileReader(
							file))) {
						String line = null;
						// For each indicator word (one to a line)
						while ((line = br.readLine()) != null) {
							// Add words to a list
							words.add(WordUtils.normaliseInput(line, false));
						}
					} catch (IOException e) {
						System.err
								.println("Exception in Categoriser initialisation.");
					}
					// And add them to the dictionary
					indicators.put(solverType, words);
				}
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method will return the current (and only) instance of the
	 * Categoriser object.
	 * 
	 * @return the categoriser
	 */
	public static Categoriser getInstance() {
		if (instance == null) {
			instance = new Categoriser();
		}
		return instance;
	}

	/**
	 * Adjust the confidence values for solution, based on whether there is an
	 * indicator word for the clue type
	 * 
	 * @param c
	 *            - the clue that is being solved
	 * @param solutions
	 *            - the solutions which have been generated for this clue
	 */
	public void confidenceAdjust(Clue c, SolutionCollection solutions) {
		Collection<String> matchingTypes = getMatchingClueTypes(c);
		for (String clueType : matchingTypes) {
			for (Solution s : solutions) {
				if (clueType.equals(s.getSolverType())) {
					double confidence = Confidence.multiply(s.getConfidence(),
							Confidence.CATEGORY_MULTIPLIER);
					s.setConfidence(confidence);
					s.addToTrace("Confidence rating increased as the clue contains indicator word(s) suggesting the solution is of type \" "
							+ clueType + "\".");
				}
			}
		}
	}

	/**
	 * Attempts to match a clue to specific clue types, based on known indicator
	 * words. TODO Another approach to this could be to use NLP
	 * 
	 * @param c
	 *            - the clue to categorise
	 * @return an array of potentially matching clue types
	 */
	public Collection<String> getMatchingClueTypes(Clue c) {
		// Matching types
		Collection<String> matches = new ArrayList<>();
		String clue = WordUtils.normaliseInput(c.getClue(), false);
		// For each clue type with known indicators
		for (String type : indicators.keySet()) {
			// And for each of these indicator word(s)
			for (String indicator : indicators.get(type)) {
				if (clue.contains(indicator)) {
					matches.add(type);
					break;
				}
			}
		}
		return matches;
	}

	public String removeIndicatorWords(String c, String type) {
		String clue = WordUtils.normaliseInput(c, false);
		if (indicators.containsKey(type)) {
			String indicator = "";
			for (String i : indicators.get(type)) {
				// if (clue.contains(i) && i.length() > indicator.length()) {
				// indicator = i;
				// }
				if (clue.contains(i)) {
					clue = clue.replace(i, "");
				}
			}
			clue = clue.replace(indicator, "");
		}
		return clue;
	}

	/**
	 * For testing purposes
	 */
	public static void main(String[] args) {
		Categoriser.getInstance();
	}

} // End of Categoriser
