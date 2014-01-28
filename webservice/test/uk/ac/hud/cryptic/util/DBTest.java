/**
 * 
 */
package uk.ac.hud.cryptic.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.solver.Acrostic;
import uk.ac.hud.cryptic.solver.Anagram;
import uk.ac.hud.cryptic.solver.Hidden;
import uk.ac.hud.cryptic.solver.Pattern;

/**
 * @author Stuart Leader
 */
public class DBTest {

	private Collection<Clue> clues;

	@Before
	public void runBeforeEveryTest() {
		clues = new ArrayList<>();
	}

	@After
	public void runAfterEveryTest() {
		clues = null;
	}

	// No types specified. Should return 0 clues.
	@Test
	public void testGetTestClues1() {
		clues = DB.getTestClues(20, true);
		assertEquals(0, clues.size());
	}

	// Should return 20 clues of type Acrostic
	@Test
	public void testGetTestClues2() {
		clues = DB.getTestClues(20, true, new Acrostic().toString());
		assertEquals(20, clues.size());
	}

	// Should return 40 clues of type Acrostic and Anagram
	@Test
	public void testGetTestClues3() {
		clues = DB.getTestClues(20, true, new Acrostic().toString(),
				new Anagram().toString());
		assertEquals(40, clues.size());
	}

	// Should return 20 KNOWN clues of type Hidden
	@Test
	public void testGetTestClues4() {
		clues = DB.getTestClues(20, false, new Hidden().toString());
		for (Clue c : clues) {
			assertEquals(c.getPattern().getTotalLength(), c.getPattern()
					.getKnownCharacters().length);
		}
	}

	// Should return 20 UNKNOWN clues of type Hidden
	@Test
	public void testGetTestClues5() {
		clues = DB.getTestClues(20, true, new Hidden().toString());
		for (Clue c : clues) {
			assertEquals(0, c.getPattern().getKnownCharacters().length);
		}
	}

	// Should return no more than 50 clues
	@Test
	public void testGetTestClues6() {
		final int maxClues = 50;
		clues = DB.getTestClues(new Pattern().toString(), true, maxClues);
		assertTrue(clues.size() <= maxClues);
	}

	// Should return 0 clues as type doesn't match
	@Test
	public void testGetTestClues7() {
		clues = DB.getTestClues("not a valid type", true, 50);
		assertEquals(0, clues.size());
	}

	// Should return 0 clues as type doesn't match
	@Test
	public void testGetTestClues8() {
		clues = DB
				.getTestClues(50, true, "not a valid type", "neither is this");
		assertEquals(0, clues.size());
	}

}
