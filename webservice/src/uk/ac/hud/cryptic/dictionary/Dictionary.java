package uk.ac.hud.cryptic.dictionary;
import java.io.*;

import uk.ac.hud.cryptic.config.Settings;
/**
 * This class provides a wrapper around the dictionary words file found within
 * Linux systems.
 * 
 * @author  Luke Hackett
 * @version 0.1
 */
public class Dictionary {
	// Dictionary Instance
    private static final Dictionary INSTANCE = new Dictionary();
    // Settings Instance
    private static Settings settings = Settings.getInstance();
    
    
    /**
     * Default Constructor
     */
    private Dictionary() {
    	
    }
    
    
    /**
     * This method will return the current (and only) instance of the 
     * Dictionary object.
     * 
     * @return  Dictionary
     */
    public static Dictionary getInstance() {
        return INSTANCE;
    }

    
    /**
     * This method will return whether or not the given word can be found in 
     * the local dictionary listing.
     * 
     * @param   word      the word to be search for
     * @return  boolean
     */
    public boolean isWord(String word) {
    	// BufferReader to read the file
    	BufferedReader br;
    	// Whether or not the word has been found
    	boolean found = false;
    	// Path to local dictionary path
    	String dictionaryPath = settings.getLocalDictionaryPath();
    	
		try {
			// Open the dictionary
			br = new BufferedReader(new FileReader(dictionaryPath));
			String line = null;
			
			// Loop over every line
			while ((line = br.readLine()) != null)  
			{
				// Do the comparison
				if(line.equals(word)){
					found = true;
					break;
				}				
			}
			
			// Close the stream
			br.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return found;
    }

}