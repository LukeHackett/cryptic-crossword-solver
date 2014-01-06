package uk.ac.hud.cryptic.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Thesaurus {
	// Pointer to the dictionary text file
	private static final File THESAURUS_FILE = new File(
			"res/thesaurus/gutenberg/mthesaur.txt");
	// HashSet provides the best .contains() performance
	private static final Collection<Collection<String>> THESAURUS = new HashSet<>();

	/**
	 * Check if a specified word is present in the dictionary being used.
	 * 
	 * @param word
	 *            - the word to check against the dictionary
	 * @return true if the dictionary contains the specified word, false
	 *         otherwise
	 */
	public static boolean match(String clueWord, String solution) {
		for (Collection<String> entry : THESAURUS) {
			if (entry.contains(clueWord)
					&& entry.contains(solution.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Read in the dictionary text file to a Java data structure to massively
	 * increase work lookup performance.
	 */
	static {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					THESAURUS_FILE));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] words = line.split(",");

				Collection<String> entry = new ArrayList<>();
				for (String word : words) {
					entry.add(word.toLowerCase());
				}
				THESAURUS.add(entry);
			}
			br.close();
		} catch (Exception e) {
			System.err
					.println("Exception in Thesaurus static initialisation block.");
		}
	}

	public static Collection<String> getSynonyms(String word) {
		Collection<String> synonyms = new HashSet<>();
		for (Collection<String> entry : THESAURUS) {
			if (entry.contains(word)) {
				synonyms.addAll(entry);
			}
		}
		synonyms.remove(word);
		return synonyms;
	}

} // End of class Thesaurus
