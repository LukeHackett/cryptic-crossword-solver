package cryptic.util;

import java.util.Collection;

public class Dictionary {
	// Dictionary Instance
	private static Dictionary instance;
	// Actual dictionary data structure
	private Collection<String> dictionary;

	/**
	 * Default Constructor
	 */
	private Dictionary() {
		getLocalDictionary();
	}

	private void getLocalDictionary() {

	}

	public static Dictionary getInstance() {
		return null;
	}

	public boolean isWord(String word) {
		return false;
	}
	
	
	public boolean searchLocalDictionary(String word) {
		return false;
	}
	
	public boolean searchRemoteDictionary(String word) {
		return false;
	}
	

}