package uk.ac.hud.cryptic.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Unit tests for Dictionary class
 * 
 * @author Leanne Butcher
 * @version 0.1
 */

public class DictionaryTest {
	
	// Tests population of dictionary
	public Dictionary dictionary = Dictionary.getInstance();
	
	@Test
	public void testIsDictionaryWord()
	{
		assertTrue(dictionary.isWord("Hello"));
	}
	
	@Test
	public void testWrongDictionaryWord()
	{
		assertEquals(false, dictionary.isWord("Jkgfdihah"));
	}
	
	@Test
	public void testBlankDictionaryWord()
	{
		assertEquals(false, dictionary.isWord(" "));
	}
	
	@Test
	public void testMatchingWords()
	{
		String pattern = "c?t";
		HashSet<String> testColl = new HashSet<String>();
		testColl.add("cat");
		testColl.add("cot");
		testColl.add("cut");
		assertEquals(testColl, dictionary.getMatchingWords(pattern));
	}
	
	@Test
	public void testMatchingWordsNonString()
	{
		String pattern = " ";
		HashSet<String> testColl = new HashSet<String>();
		assertEquals(testColl, dictionary.getMatchingWords(pattern));
	}
	
	@Test
	public void testMatchingWordsNonWord()
	{
		String pattern = "?v?x?z";
		HashSet<String> testColl = new HashSet<String>();
		assertEquals(testColl, dictionary.getMatchingWords(pattern));
	}
	
	@Test
	public void testPrefixMatch()
	{
		String prefixData = "mis";
		assertTrue(dictionary.prefixMatch(prefixData));
	}
	
	@Test
	public void testPrefixMatchWithNonPrefix()
	{
		String prefixData = "xyz";
		assertEquals(false, dictionary.prefixMatch(prefixData));
	}
	
	@Test
	public void testPrefixMatchWithNonString()
	{
		String prefixData = "\\s";
		assertEquals(false, dictionary.prefixMatch(prefixData));
	}
	
	@Test
	public void testMatchesWithPrefix()
	{
		String prefixData = "mis";
		int lengthData = 4;
		HashSet<String> testColl = new HashSet<String>();
		testColl.add("miso");
		testColl.add("mise");
		testColl.add("miss");
		testColl.add("mist");
		assertEquals(testColl, dictionary.getMatchesWithPrefix(prefixData, lengthData));
	}
	
	@Test
	public void testMatchesWithNonString()
	{
		String prefixData = " ";
		HashSet<String> testColl = new HashSet<String>();
		assertEquals(testColl, dictionary.getMatchesWithPrefix(prefixData, 0));
	}
	
	@Test
	public void testMatchesWithNonPrefix()
	{
		String prefixData = "xzy";
		HashSet<String> testColl = new HashSet<String>();
		for(int i = 0; i < 10; i++)
			assertEquals(testColl, dictionary.getMatchesWithPrefix(prefixData, i));
	}
	
	@Test
	public void testDictionaryFilter()
	{
		SolutionCollection testColl = new SolutionCollection();
		testColl.add(new Solution("pisces"));
		testColl.add(new Solution("spices"));
		testColl.add(new Solution("sspcie"));
		SolutionPattern pattern = new SolutionPattern("??????");
		assertEquals(3, testColl.size());
		dictionary.dictionaryFilter(testColl, pattern);
		assertEquals(2, testColl.size());
	}
}
