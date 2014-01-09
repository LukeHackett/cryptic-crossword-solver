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
		return "assets/dictionary/acd/UKACD.txt";
		// return "assets/dictionary/standard/words";
	}

	/**
	 * This method will return the path to the thesaurus file.
	 * 
	 * @return the file path to the thesaurus
	 */
	public String getThesaurusPath() {
		return "assets/thesaurus/gutenberg/mthesaur.txt";
	}

	/**
	 * Get the URL of the cryptic project's MySQL database
	 * 
	 * @return the url of the MySQL database
	 */
	public String getDBURL() {
		return "jdbc:mysql://helios.hud.ac.uk:3306/cryptic";
	}

	/**
	 * Get the username of the cryptic project's MySQL database
	 * 
	 * @return the username of the MySQL database
	 */
	public String getDBUsername() {
		return "cryptic";
	}

	/**
	 * Get the password of the cryptic project's MySQL database. Yes this
	 * shouldn't be stored in a source file, and yes it shouldn't be stored as
	 * plain text. Please don't hurt me. Many thanks. TODO I'm an idiot.
	 * 
	 * @return the password of the MySQL database
	 */
	public String getDBPassword() {
		return "du4hacrEKa";
	}

}
