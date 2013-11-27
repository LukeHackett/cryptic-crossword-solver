package com.cryptic.config;
/**
 * This class provides a number of application wide settings and constants.
 * 
 * @author  Luke Hackett
 * @version 0.1
 */
public class Settings {
	// Dictionary Instance
    private static final Settings INSTANCE = new Settings();
    
    
    /**
     * Default Constructor
     */
    private Settings() {
    	
    }
    
    
    /**
     * This method will return the current (and only) instance of the Settings
     * object.
     * 
     * @return  Settings
     */
    public static Settings getInstance() {
        return INSTANCE;
    }
    

    /**
     * This method will return the absolute path to the dictionary words file
     * found in UNIX and Linux operating systems.
     * 
     * @return  String
     */
    public String getLocalDictionaryPath() {
    	return "/usr/share/dict/words";
    }
    
}