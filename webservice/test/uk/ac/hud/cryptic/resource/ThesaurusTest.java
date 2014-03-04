package uk.ac.hud.cryptic.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionPattern;

/**
 * Unit tests for Thesaurus class
 * 
 * @author Leanne Butcher, Stuart Leader
 * @version 0.1
 */

public class ThesaurusTest {

	// Tests population of Thesaurus
	public Thesaurus thesaurus = Thesaurus.getInstance();

	@Test
	public void testSynonymMatch() {
		Clue testClue = new Clue("Speedy", "???????");
		Solution testSolution = new Solution("quick");
		assertTrue(thesaurus.match(testClue, testSolution));
	}

	@Test
	public void testGetSynonyms() {
		Collection<String> testColl = new HashSet<>();
		testColl.add("act between");
		testColl.add("arbitrate");
		testColl.add("bargain");
		testColl.add("go between");
		testColl.add("intercede");
		testColl.add("intermediate");
		testColl.add("interpose");
		testColl.add("intervene");
		testColl.add("judge");
		testColl.add("make terms");
		testColl.add("mediate");
		testColl.add("meet halfway");
		testColl.add("moderate");
		testColl.add("negotiate");
		testColl.add("referee");
		testColl.add("represent");
		testColl.add("sit down with");
		testColl.add("step in");
		testColl.add("umpire");

		String test = "treat with";

		Collection<String> synonyms = thesaurus.getSynonyms(test);
		assertTrue(synonyms.size() == testColl.size());
		assertTrue(synonyms.containsAll(testColl));
	}

	@Test
	public void testGetSpecificSynonyms() {
		Collection<String> testColl = new HashSet<>();
		String solution = "absence of mind";
		testColl.add(solution);
		String test = "dream";
		SolutionPattern pattern = new SolutionPattern("??s????-o?-m???");
		assertEquals(testColl, thesaurus.getMatchingSynonyms(test, pattern));
	}

	@Test
	public void testGetSpecificSynonymsNoKnownChars() {
		Collection<String> testColl = new HashSet<>();
		testColl.add("absence of mind");
		String test = "dream";
		SolutionPattern pattern = new SolutionPattern("???????-??-????");
		assertEquals(testColl, thesaurus.getMatchingSynonyms(test, pattern));
	}
	
	@Test
	public void testGetSecondSynonymsWithPattern() {
		
	}
	
	@Test
	public void testGetSecondSynonymsMinMaxLength() {
		
	}
	
	@Test 
	public void testGetWordContainingSynonym() {
		
	}
	
	@Test
	public void testConfidenceAdjust() {
		
	}
}
