package uk.ac.hud.cryptic.config;

import java.io.InputStream;

import javax.servlet.ServletContext;

/**
 * This class provides a number of application wide settings and constants.
 * 
 * @author Luke Hackett, Stuart Leader
 * @version 0.1
 */
public class Settings {
	private static Settings instance;
	// Context if application is executed as a servlet
	private ServletContext context;
	// Pre-path for Server environment
	private static final String SERVER_PRE_PATH = "/WEB-INF/assets/";
	// Pre-path for local environment
	private static final String LOCAL_PRE_PATH = "/";

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
	public InputStream getDictionaryPath() {
		// Location of the resource
		return getPath("dictionary/acd/UKACD.txt");
	}

	/**
	 * This method will return the path to the thesaurus file.
	 * 
	 * @return the file path to the thesaurus
	 */
	public InputStream getThesaurusPath() {
		// Location of the resource
		return getPath("thesaurus/gutenberg/mthesaur.txt");
	}

	/**
	 * This method will return the path to the pronouncing dictionary file.
	 * 
	 * @return the file path to the pronouncing dictionary
	 */
	public InputStream getPronouncingDictionaryPath() {
		// Location of the resource
		return getPath("homophones/cmudict.0.7a");

	}

	/**
	 * Get the InputStream for the given path to a resource. The exact location
	 * depends on whether the application is being run from a local or server
	 * environment.
	 * 
	 * @param resource
	 *            - the path of the resource to use
	 * @return the <code>InputStream</code> of the requested resource
	 */
	private InputStream getPath(String resource) {
		// Am I being called from a Servlet?
		boolean server = context != null;
		// Path to the dictionary resource
		String path = (server ? SERVER_PRE_PATH : LOCAL_PRE_PATH) + resource;
		// InputStream to resource
		InputStream is = server ? context.getResourceAsStream(path)
				: Settings.class.getResourceAsStream(path);

		return is;
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

	public void setServletContext(ServletContext sc) {
		context = sc;
	}

	public ServletContext getServletContext() {
		return context;
	}

}
