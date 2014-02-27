package uk.ac.hud.cryptic.nlp;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import uk.ac.hud.cryptic.config.Settings;

public class CoreTools {

	public static final String TOKENS = "tokens";
	public static final String POS_TAGS = "pos";
	public static final String CHUNKED = "chunk";

	private static CoreTools instance;
	private POSTaggerME posTagger;
	private SentenceDetectorME sentenceDetector;
	private TokenizerME tokeniser;

	private final Settings settings = Settings.getInstance();

	private CoreTools() {
		try {
			// Initialise tokeniser
			InputStream sStream = settings.getSentenceDetectorModelStream();
			SentenceModel sModel = new SentenceModel(sStream);
			sStream.close();
			sentenceDetector = new SentenceDetectorME(sModel);

			// Initialise tokeniser
			InputStream tStream = settings.getTokeniserModelStream();
			TokenizerModel tModel = new TokenizerModel(tStream);
			tStream.close();
			tokeniser = new TokenizerME(tModel);

			// Initialise POS tagger
			InputStream pStream = settings.getPOSModelStream();
			POSModel pModel = new POSModel(pStream);
			pStream.close();
			posTagger = new POSTaggerME(pModel);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieve the one and only instance of the CoreTools (Singleton design
	 * pattern)
	 * 
	 * @return the NLP core tools
	 */
	public static CoreTools getInstance() {
		if (instance == null) {
			instance = new CoreTools();
		}
		return instance;
	}

	public synchronized String[] detectSentences(String input) {
		// Split the input into sentences. There should typically only be one.
		return sentenceDetector.sentDetect(input);
	}

	public synchronized String[] tokenise(String input) {
		return tokeniser.tokenize(input);
	}

	public synchronized String[] tag(String[] input) {
		return posTagger.tag(input);
	}

} // End of class CoreTools
