package uk.ac.hud.cryptic.config;

/**
 * This class provides a number of application wide settings and constants.
 * 
 * @author Luke Hackett, Stuart Leader
 * @version 0.1
 */
public class Settings {
	private static Settings instance;

	/**
	 * This method will return the current (and only) instance of the Settings
	 * object.
	 * 
	 * @return Settings
	 */
	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	/**
	 * This method will return the path to the dictionary words file.
	 * 
	 * @return the file path to the dictionary
	 */
	public String getDictionaryPath() {
		return "res/dictionary/standard/words";
	}

	/**
	 * This method will return the path to the thesaurus file.
	 * 
	 * @return the file path to the thesaurus
	 */
	public String getThesaurusPath() {
		return "res/thesaurus/gutenberg/mthesaur.txt";
	}

}
