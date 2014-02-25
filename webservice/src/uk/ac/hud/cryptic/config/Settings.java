package uk.ac.hud.cryptic.config;

import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContext;

/**
 * This class provides a number of application wide settings and constants.
 * 
 * @author Luke Hackett, Stuart Leader, Mohammad Rahman
 * @version 0.2
 */
public class Settings {
	private static Settings instance;
	// Context if application is executed as a servlet
	private ServletContext context;
	// Pre-path for Server environment
	private static final String SERVER_PRE_PATH = "/WEB-INF/";
	// Pre-path for local environment
	private static final String LOCAL_PRE_PATH = "/";

	/**
	 * Allows one to specify whether a requested resource is an asset or a
	 * class. Other types could be added to this if necessary. This is used in
	 * the creation of the path to the specified resource.
	 * 
	 * @author Stuart Leader
	 * @version 0.2
	 */
	public enum ResourceType {
		ASSET("assets/"), CLASS("classes/uk/ac/hud/cryptic/");

		private final String path;

		ResourceType(String path) {
			this.path = path;
		}

		String getPath() {
			return path;
		}
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
	private InputStream getStream(ResourceType type, String resource) {
		// Am I being called from a Servlet?
		boolean server = context != null;
		// Path to the dictionary resource
		String path = (server ? SERVER_PRE_PATH + type.getPath()
				: LOCAL_PRE_PATH) + resource;
		// InputStream to resource
		InputStream is = server ? context.getResourceAsStream(path)
				: Settings.class.getResourceAsStream(path);

		return is;
	}

	/**
	 * This method will return the path to the custom dictionary words file.
	 * 
	 * @return the file path to the custom dictionary
	 */
	public InputStream getCustomDictionaryStream() {
		// Location of the resource
		return getStream(ResourceType.ASSET,
				"dictionary/custom/custom-dict.txt");
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
	 * This method will return the path to the dictionary exclusions words file.
	 * This contains words which are present in the large dictionaries which we
	 * don't want. Having a separate exclusions file allow us to replace the
	 * dictionaries with newer versions without losing our modifications.
	 * 
	 * @return the file path to the dictionary exclusions
	 */
	public InputStream getDictionaryExclusionsStream() {
		// Location of the resource
		return getStream(ResourceType.ASSET, "dictionary/custom/exclusions.txt");
	}

	/**
	 * This method will return the path to the dictionary words file.
	 * 
	 * @return the file path to the dictionary
	 */
	public InputStream getDictionaryStream() {
		// Location of the resource
		return getStream(ResourceType.ASSET, "dictionary/acd/UKACD.txt");
	}

	/**
	 * This method will return the path to the solvers properties file.
	 * 
	 * @return the file path to the properties file
	 */
	public InputStream getPropertyStream() {
		// Location of the property
		return getStream(ResourceType.ASSET, "properties/solvers.properties");
	}

	/**
	 * This method will return the path to the thesaurus file.
	 * 
	 * @return the file path to the thesaurus
	 */
	public InputStream getThesaurusStream() {
		// Location of the resource
		return getStream(ResourceType.ASSET, "thesaurus/gutenberg/mthesaur.txt");
	}

	/**
	 * This method will return the path to the homophone dictionary file.
	 * 
	 * @return the file path to the homophone dictionary
	 */
	public InputStream getHomophoneDictionaryStream() {
		// Location of the resource
		return getStream(ResourceType.ASSET, "homophones/cmudict.0.7a");
	}

	/**
	 * This method will return the path to the natural language processing
	 * parser model.
	 * 
	 * @return the file path to the NLP parser model
	 */
	public InputStream getParserModelStream() {
		// Location of the resource
		return getStream(ResourceType.ASSET, "nlp/en-parser-chunking.bin");
	}

	/**
	 * This method will return the path to the natural language processing
	 * categoriser model, which has been trained using the Guardian's data.
	 * 
	 * @return the file path to the NLP categoriser model
	 */
	public InputStream getCategoriserModelStream() {
		// Location of the resource
		return getStream(ResourceType.ASSET, "nlp/cryptic-categoriser.bin");
	}

	/**
	 * Set the <code>ServletContext</code> object, which should be done when the
	 * application is run in a server environment to indicate this is the case
	 * 
	 * @param sc
	 *            - the corresponding servlet context object to the deployed
	 *            service
	 */
	public void setServletContext(ServletContext sc) {
		context = sc;
	}

	/**
	 * Get a directory from the classpath, represented as a URL
	 * 
	 * @param resource
	 *            - the directory to obtain
	 * @return the requested directory as a URL, <code>null</code> otherwise
	 */
	public URL getIndicatorsURL(ResourceType type, String resource) {
		// Path to the dictionary resource
		String path = LOCAL_PRE_PATH + resource;
		// URL of resource
		return Settings.class.getResource(path);
	}

	/**
	 * Get the filename of a text file. E.g. for "anagrams.txt", return
	 * "anagrams"
	 * 
	 * @param filename
	 *            - the filename to get the name of
	 * @return the name of the specified text file
	 */
	public String getFileName(String filename, String extension) {
		filename = filename.toLowerCase().trim();
		if (filename.endsWith("." + extension)) {
			filename = filename.substring(0,
					filename.length() - (1 + extension.length()));
		}
		return filename;
	}

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

} // End of class Settings
