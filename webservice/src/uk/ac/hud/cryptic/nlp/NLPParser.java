package uk.ac.hud.cryptic.nlp;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import uk.ac.hud.cryptic.config.Settings;

public class NLPParser {

	private static NLPParser instance;
	// private static CoreTools nlpCore = CoreTools.getInstance();
	private final Settings settings = Settings.getInstance();
	private Parser parser;

	/**
	 * Initialise the parser by loading in the parser model
	 */
	private NLPParser() {
		try {
			InputStream pStream = settings.getParserModelStream();
			ParserModel pModel = new ParserModel(pStream);
			pStream.close();
			parser = ParserFactory.create(pModel);
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
	public static NLPParser getInstance() {
		if (instance == null) {
			instance = new NLPParser();
		}
		return instance;
	}

	/**
	 * Retrieve the Parse object for a specified clue.
	 * 
	 * @param clue
	 *            - the clue to parse
	 * @return a parse object representing this clue
	 */
	public Parse parseClue(String clue) {

		Parse parse = ParserTool.parseLine(clue, parser, 1)[0];
		// Print the parse to console
		parse.show();

		return parse;
	}

	public static void main(String[] args) {
		NLPParser p = NLPParser.getInstance();
		p.parseClue("Performing masculine arts, do oneself an injury!");
	}

} // End of class NLPParser
