package uk.ac.hud.cryptic.resource;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.junit.Test;

public class AbbreviationsTest {

	// Tests population of abbreviations
	public Abbreviations abbreviations = Abbreviations.getInstance();
	
	@Test
	public void testGetAbbreviationsForWord() 
	{
		String wordToAbbr = "quiet";
		HashSet<String> abbr = new HashSet<String>();
		abbr.add("p");
		abbr.add("pp");
		abbr.add("sh");
		abbr.add("mum");
		assertEquals(abbr, abbreviations.getAbbreviationsForWord(wordToAbbr));
	}
	
	@Test
	public void testGetAbbreviationsForClue()
	{
		String clueToAbbr = "Help the medic";
		LinkedHashMap<String, Set<String>> abbr = new LinkedHashMap<>();
		Set<String> abbre = new HashSet<String>();	
		abbre.add("mo");
		abbre.add("md");
		abbre.add("mb");
		abbre.add("bm");
		abbre.add("doc");
		abbre.add("gp");
		abbre.add("dr");
		abbr.put("medic", abbre);
		assertEquals(abbr, abbreviations.getAbbreviationsForClue(clueToAbbr));
	}
	
	
}
