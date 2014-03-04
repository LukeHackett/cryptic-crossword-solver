package uk.ac.hud.cryptic.resource;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;


public class HomophoneDictionaryTest {
	
	private static ArrayList<String> words = new ArrayList<String>();
	private static ArrayList<String> results = new ArrayList<String>();
	private static HomophoneDictionary dictionary;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	dictionary = HomophoneDictionary.getInstance();
		
	 words.add("groan");
	 words.add("hair");
	 words.add("hall");
	 words.add("hear");
	 words.add("plain");
	 words.add("rain");
	 words.add("write");
	 words.add("wait");
	 words.add("which");
	 words.add("knew");
	 
	 results.add("grown");
	 results.add("hare");
	 results.add("haul");
	 results.add("here");
	 results.add("plane");
	 results.add("reign");
	 results.add("right");
	 results.add("weight");
	 results.add("witch");
	 results.add("new");
	 
	}
	
	
	/**
	 * This test ensures that a pattern for pronunciation for each word
	 * is found to be later used to find the Homonyms.
	 */
	@Test
	public void testGetPronunciations() {
		for(String word: words){
			assertNotNull(dictionary.getPronunciations(word));
		}
	}
	
	
	/**
	 * This test checks that for all the provided test words
	 * it finds and populates some homonyms. The test then ensures that the
	 * correct homonym is in the results.
	 */
	@Test
	public void testGetHomonyms() {
		
		for(int i =0; i < words.size(); i++ ){
			Set<String> matches = dictionary.getHomonyms(words.get(i));
			assertTrue( matches.contains(results.get(i)));		
		}
		
	}
	
}
