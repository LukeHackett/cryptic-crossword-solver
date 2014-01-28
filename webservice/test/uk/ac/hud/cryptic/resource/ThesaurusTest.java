package uk.ac.hud.cryptic.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Unit tests for Dictionary class
 * 
 * @author Leanne Butcher
 * @version 0.1
 */

public class ThesaurusTest {

	// Tests population of Thesaurus
	public Thesaurus thesaurus = Thesaurus.getInstance();

	@Test
	public void testSynonymMatch() {
		Clue testClue = new Clue("Speedy", "???????");
		String testSolution = "quick";
		assertTrue(thesaurus.match(testClue, testSolution));
	}

	@Test
	public void testGetMatch() {
		Clue testClue = new Clue("Stop the flow in crazy get-up", "???");
		String testSolution = "crazy";
		assertEquals(185, thesaurus.getMatchCount(testClue, testSolution));
	}

	@Test
	public void testGetSynonyms() {
		// TODO //HashSet<String> testColl = new HashSet<String>();
		// String test = "zone";
		// assertEquals(testColl, thesaurus.getSynonyms(test));
		assertTrue(false);
	}

	@Test
	public void testGetSpecificSynonyms() {
		HashSet<String> testColl = new HashSet<String>();
		String solution = "absence of mind";
		testColl.add(solution);
		String test = "dream";
		SolutionPattern pattern = new SolutionPattern("??s????-o?-m???");
		assertEquals(testColl, thesaurus.getSpecificSynonyms(test, pattern));
	}

	@Test
	public void testGetSpecificSynonymsNoKnownChars() {
		HashSet<String> testColl = new HashSet<String>();
		testColl.add("sleight of hand");
		testColl.add("absence of mind");
		String test = "dream";
		SolutionPattern pattern = new SolutionPattern("???????-??-????");
		assertEquals(testColl, thesaurus.getSpecificSynonyms(test, pattern));
	}
}
