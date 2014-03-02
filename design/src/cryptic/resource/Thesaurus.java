package cryptic.resource;

import java.util.Collection;

public class Thesaurus {
	// Dictionary Instance
	private static Thesaurus instance;

	
	/**
	 * Default Constructor
	 */
	private Thesaurus() {
		
	}


	public static Thesaurus getInstance() {
		return null;
	}


	public boolean getAlteratives(String word) {
		return false;
	}
	
	
	public Collection<String> searchRemoteThesaurus(String word) {
		return null;
	}
	
}