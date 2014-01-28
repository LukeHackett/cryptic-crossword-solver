/**
 * This JUnit Class provides the unit and integration tests for the Clue class.
 * The tests are written and compiled in accordance to JUnit 4
 */
package uk.ac.hud.cryptic.core;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
	private static String type;
	private static Solution solution;


	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pattern = new SolutionPattern("?????");
		clue = new Clue("Air that's more usually seen in cheerleaders", 
				pattern.toString());

		solutions = new SolutionCollection();

		solution = new Solution("music",100);

		solutions.add(new Solution("smore",10));
		solutions.add(new Solution("usual",20));
		solutions.add(new Solution("taths",36));
		solutions.add(new Solution("cheer",49));
		solutions.add(solution);
		solutions.add(new Solution("sered",1));
		solutions.add(new Solution("salse",75));

		clue.setSolutions(solutions);
		clue.setActualSolution("music");
		type = "HIDDEN";
		clue.setType(type);
		String actualSolution = "MUSIC";
		clue.setActualSolution(actualSolution);


	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pattern = null;
		clue = null;
		solutions = null;
		type = null;

	}


	@Test
	public final void testClueStringString() {
		Clue test = new Clue("Air that's more usually seen in cheerleaders", "?????");
		assertEquals(test.getClue(),clue.getClue());
		assertEquals(test.getPattern().toString(),clue.getPattern().toString());
	}



	/**
	 * This method will test to see if the actual solution matches
	 * the test data solution
	 */
	@Test
	public final void testGetActualSolution() {
		String test = "MUSIC";
		assertEquals(test,clue.getActualSolution());
	}

	/**
	 * This method will test to see that the best solution i.e the solution
	 * with the highest confidence rating matches to the actual solution
	 */
	@Test
	public final void testGetBestSolution() {
		Solution test = new Solution("music", 100);
		assertEquals(clue.getBestSolution(), test);
	}

	/**
	 * This test method checks to see that the getter for the clue returns
	 * the clue as it is entered
	 */
	@Test
	public final void testGetClue() {
		assertEquals("Air that's more usually seen in cheerleaders", 
				"air that's more usually seen in cheerleaders",
				clue.getClue());
	}

	/**
	 * This method tests that all punctuation is removed from the clue.
	 */
	@Test
	//Get clue no punctuation with spaces
	public final void testGetClueNoWithPunctuation() {		
		assertEquals("air thats more usually seen in cheerleaders",
				clue.getClueNoPunctuation(false));
	}

	/**
	 * This method tests all punctuation and spaces are removed from the clue
	 */
	@Test
	//Get clue no punctuation without spaces
	public final void testGetClueNoPunctuationNoSpaces() {		
		assertEquals("airthatsmoreusuallyseenincheerleaders",
				clue.getClueNoPunctuation(true));
	}


	/**
	 * This method tests that the words in the clue are split so that they
	 * can be evaluated individually
	 */
	@Test
	public final void testGetClueWords() {
		String aclue = clue.getClueNoPunctuation(false).trim();
		String [] splitclues = aclue.split(WordUtils.REGEX_WHITESPACE);

		assertArrayEquals(splitclues, clue.getClueWords());
	}

	/**
	 * This method tests the pattern of the clue has not changed from when it
	 * has been entered
	 */
	@Test
	public final void testGetPattern() {
		assertEquals(pattern.toString(),
				clue.getPattern().toString());
	}

	/**
	 * This method tests the getter for the solutions of a clue
	 */
	@Test
	public final void testGetSolutions() {
		String t = "HIDDEN";
		Clue test = new Clue("Air that's more usually seen in cheerleaders", 
				"?????","MUSIC", t);

		SolutionCollection testSolutions = new SolutionCollection();
		testSolutions.add(new Solution("smore"));
		testSolutions.add(new Solution("usual"));
		testSolutions.add(new Solution("taths"));
		testSolutions.add(new Solution("cheer"));
		testSolutions.add(new Solution("music"));
		testSolutions.add(new Solution("sered"));
		testSolutions.add(new Solution("salse"));
		test.setSolutions(testSolutions);

		assertEquals(test.getSolutions(),clue.getSolutions());	
	}

	/**
	 * This method tests the getter for the Type of clue it is
	 */
	@Test
	public final void testGetType() {
		String type;
		type = "HIDDEN";
		assertEquals(type,clue.getType().toString());

	}

}
