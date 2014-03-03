/**
 * This JUnit Class provides the unit and integration tests for the Manager class.
 * The tests are written and compiled in accordance to JUnit 4
 *
 * @author Mohammad Rahman
 * @version 0.1
 * 
 */
package uk.ac.hud.cryptic.core;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ManagerTest {

	private static SolutionPattern pattern;
	private static Clue clue;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		pattern = new SolutionPattern("?????");
		clue = new Clue("Air that's more usually seen in cheerleaders", 
				pattern.toString());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		pattern = null;
		clue = null;

	}

	/**
	 * This test tests to see the method returns a collection
	 * of solutions from the input of a clue
	 */
	@Test
	public final void testDistributeAndSolveClue() {		
		Manager man = new Manager();
		SolutionCollection sol = man.distributeAndSolveClue(clue);
		assertTrue(!sol.isEmpty());
	}

}
