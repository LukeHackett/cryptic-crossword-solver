/**
 * This JUnit Class provides the unit and integration tests for the Clue class.
 * The tests are written and compiled in accordance to JUnit 4
 */
package uk.ac.hud.cryptic.core;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.hud.cryptic.solver.Solver.Type;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * @author Mohammad Rahman
 * @version 0.1
 * 
 */
public class ClueTest {
	
	private static SolutionPattern pattern;
	private static Clue clue;
	private static SolutionCollection solutions;
	private static String actualSolution;
	private static Type type;


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pattern = new SolutionPattern("?????");
		clue = new Clue("Air that's more usually seen in cheerleaders", 
				pattern.toString()); //MUSIC
		solutions = new SolutionCollection();
		actualSolution = "MUSIC";
		type = Type.HIDDEN;

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pattern = null;
		clue = null;
		solutions = null;
		actualSolution = null;
		type = null;
		
	}

	
	@Test
	public final void testClueStringString() {
		Clue test = new Clue("Air that's more usually seen in cheerleaders", "?????");
		assertTrue(test.equals(clue));
		
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#Clue(java.lang.String, java.lang.String, java.lang.String, uk.ac.hud.cryptic.solver.Solver.Type)}.
	 */
	@Test
	public final void testClueStringStringStringType() {
		
		Type t = Type.HIDDEN;
		Clue test = new Clue("Air that's more usually seen in cheerleaders", "?????","MUSIC", t);
		assertTrue(test.equals(clue));
		
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getActualSolution()}.
	 */
	@Test
	public final void testGetActualSolution() {
		String test = "music";
		assertEquals(test,clue.getActualSolution());
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getBestSolution()}.
	 */
	@Test
	public final void testGetBestSolution() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getClue()}.
	 */
	@Test
	public final void testGetClue() {
		assertEquals("Air that's more usually seen in cheerleaders", 
				"air that's more usually seen in cheerleaders",
				clue.getClue());
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getClueNoPunctuation(boolean)}.
	 * No Punctuation
	 */
	@Test
	//Get clue no punctuation with spaces
	public final void testGetClueNoWithPunctuation() {		
		assertEquals("air thats more usually seen in cheerleaders",
				clue.getClueNoPunctuation(false));
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getClueNoPunctuation(boolean)}.
	 * No Spaces
	 */
	@Test
	//Get clue no punctuation without spaces
	public final void testGetClueNoPunctuationNoSpaces() {		
		assertEquals("airthatsmoreusuallyseenincheerleaders",
				clue.getClueNoPunctuation(true));
	}
	

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getClueWords()}.
	 */
	@Test
	public final void testGetClueWords() {
		String aclue = clue.getClueNoPunctuation(false).trim();
		String [] splitclues = aclue.split(WordUtils.REGEX_WHITESPACE);

		assertArrayEquals(splitclues, clue.getClueWords());
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getPattern()}.
	 */
	@Test
	public final void testGetPattern() {
		assertEquals(pattern.toString(),
				clue.getPattern().toString());
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getSolutions()}.
	 */
	@Test
	public final void testGetSolutions() {
		SolutionCollection test = new SolutionCollection();
		test.add(new Solution("MUSIC"));
		assertEquals(test,solutions);
	}

	/**
	 * Test method for {@link uk.ac.hud.cryptic.core.Clue#getType()}.
	 */
	@Test
	public final void testGetType() {
		Type t = Type.HIDDEN;
		assertEquals(t,type);
	}

}
