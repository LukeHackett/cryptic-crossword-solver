package uk.ac.hud.cryptic.nlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.util.Util;

public class NLPChunker {

	private static NLPChunker instance;
	private static CoreTools nlpCore = CoreTools.getInstance();
	private final Settings settings = Settings.getInstance();
	private ChunkerME chunker;

	/**
	 * Initialise the parser by loading in the parser model
	 */
	private NLPChunker() {
		try {
			// Initialise chunker
			InputStream cStream = settings.getChunkerModelStream();
			ChunkerModel cModel = new ChunkerModel(cStream);
			cStream.close();
			chunker = new ChunkerME(cModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve the one and only instance of the NLPParser (Singleton design
	 * pattern)
	 * 
	 * @return the NLP parser
	 */
	public static NLPChunker getInstance() {
		if (instance == null) {
			instance = new NLPChunker();
		}
		return instance;
	}

	/**
	 * Retrieve a map representing the chunking of a specified clue.
	 * 
	 * @param clue
	 *            - the clue to parse
	 * @return a map containing all the information gathered during chunking
	 */
	public Map<String, List<String>> chunkClue(String clue) {
		// Will contain lots of hopefully useful information about the chunking
		Map<String, List<String>> info = new HashMap<>(3);

		// Split the input into sentences. There should typically only be one.
		String[] sentences = nlpCore.detectSentences(clue);

		// For each identified sentence
		for (String sentence : sentences) {
			// Tokenise it - i.e. separate the words and punctuation into
			// separate elements
			String[] tokens = nlpCore.tokenise(sentence);
			addToMap(info, CoreTools.TOKENS, tokens);

			// Get the parts-of-speech tags for each token (needed for chunking)
			String[] posTags = nlpCore.tag(tokens);
			addToMap(info, CoreTools.POS_TAGS, posTags);

			// And chunk using the pre-calculated information
			String[] chunk = chunker.chunk(tokens, posTags);
			addToMap(info, CoreTools.CHUNKED, chunk);
		}

		System.out.println(info.get(CoreTools.TOKENS));
		System.out.println(info.get(CoreTools.POS_TAGS));
		System.out.println(info.get(CoreTools.CHUNKED));

		return info;
	}

	/**
	 * Add some information gathered during the chunking process to a dedicated
	 * Map
	 * 
	 * @param map
	 *            - the map to add the information to
	 * @param key
	 *            - the map's key (use the NLPChunker constants)
	 * @param value
	 *            - the value to be added to the map, or appended to the
	 *            existing value if one already exists.
	 */
	private void addToMap(Map<String, List<String>> map, String key,
			String[] value) {
		// Add Array to a List (Arrays.addAll is read-only and throws exception)
		List<String> toAdd = new ArrayList<>();
		for (String item : value) {
			toAdd.add(item);
		}

		// Add to map, or add to existing value if present
		Util.addAllToMap(map, key, toAdd, ArrayList.class);
	}

	/**
	 * As usual, for testing purposes.
	 */
	public static void main(String[] args) {
		NLPChunker c = NLPChunker.getInstance();
		c.chunkClue("Performing masculine arts, do oneself an injury!");
	}

} // End of class NLPChunker
