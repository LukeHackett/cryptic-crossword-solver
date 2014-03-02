package cryptic.config;

import java.io.InputStream;
import java.net.URL;

public class Settings {
	private static Settings instance;
	// Pre-path for Server environment
	private static final String SERVER_PRE_PATH = "/WEB-INF/";
	// Pre-path for local environment
	private static final String LOCAL_PRE_PATH = "/";

	private Settings() {
		
	}

	private InputStream getStream(String resource) {
		return null;
	}

	public InputStream getCustomDictionaryStream() {
		return null;
	}

	public String getDBPassword() {
		return null;
	}

	public String getDBURL() {
		return null;
	}

	public String getDBUsername() {
		return null;
	}

	public InputStream getDictionaryExclusionsStream() {
		return null;
	}

	public InputStream getDictionaryStream() {
		return null;
	}

	public InputStream getPropertyStream() {
		return null;
	}

	public InputStream getThesaurusStream() {
		return null;
	}

	public InputStream getHomophoneDictionaryStream() {
		return null;
	}

	public InputStream getParserModelStream() {
		return null;
	}

	public InputStream getTokeniserModelStream() {
		return null;
	}

	public InputStream getSentenceDetectorModelStream() {
		return null;
	}

	public InputStream getPOSModelStream() {
		return null;
	}

	public InputStream getChunkerModelStream() {
		return null;
	}

	public InputStream getCategoriserModelStream() {
		return null;
	}

	public static Settings getInstance() {
		return null;
	}
}
