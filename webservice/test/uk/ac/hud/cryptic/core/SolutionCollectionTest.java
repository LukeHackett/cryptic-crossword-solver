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
import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolutionCollectionTest {

	private static SolutionPattern pattern;
	private static Clue clue;

	@Before
	public void setUp() throws Exception {
		pattern = new SolutionPattern("?????");
		clue = new Clue("Air that's more usually seen in cheerleaders", 
				pattern.toString());


		SolutionCollection sols = new SolutionCollection();
		sols.add(new Solution("smore",10));
		sols.add(new Solution("usual",13));
		sols.add(new Solution("taths",20));
		sols.add(new Solution("cheer",7));
		sols.add(new Solution("music",90));
		sols.add(new Solution("cered",5));
		sols.add(new Solution("salse",44));
		clue.setSolutions(sols);
	}

	@After
	public void tearDown() throws Exception {
		pattern = null;
		clue = null;
	}

	/**
	 * This tests checks the Solution collection contain
	 * the solution string
	 */
	@Test
	public final void testContainsString() {

		String solution = "music";
		assertTrue(clue.getSolutions().contains(solution));

	}

	/**
	 * This tests checks the Solution with the highest confidence
	 * from the Solution Collection
	 */
	@Test
	public final void testGetBestSolution() {
		Solution bestSolution = new Solution("music",90);		
		assertEquals(clue.getSolutions().getBestSolution(),bestSolution);
	}

	/**
	 * This tests checks only solutions greater then the specified
	 * confidence score are returned only
	 */
	@Test
	public final void testGetSolutionsGreaterThan() {

		//Test Data
		SolutionCollection sc = new SolutionCollection();
		sc.add(new Solution("music",90));
		sc.add(new Solution("salse",44));

		int confidence = 35;
		assertEquals(sc,clue.getSolutions().getSolutionsGreaterThan(confidence));

	}

	/**
	 * This tests checks only solutions less than the specified
	 * confidence score are returned only
	 */
	@Test
	public final void testGetSolutionsLessThan() {
		//Test Data
		SolutionCollection sc = new SolutionCollection();
		sc.add(new Solution("smore",10));
		sc.add(new Solution("usual",13));
		sc.add(new Solution("taths",20));
		sc.add(new Solution("cheer",7));		
		sc.add(new Solution("cered",5));

		int confidence = 35;	
		assertEquals(sc,clue.getSolutions().getSolutionsLessThan(confidence));
	}


	/**
	 * This tests checks that a collection of solutions are removed
	 * from the solution collection.
	 */
	@Test
	public final void testRemoveAllStrings() {

		//Test data
		SolutionCollection test = new SolutionCollection();
		test.add(new Solution("music",90));
		test.add(new Solution("salse",44));
		Collection<String> sc = new ArrayList<String>();
		sc.add("smore");
		sc.add("usual");
		sc.add("taths");
		sc.add("cheer");		
		sc.add("cered");

		//Test Method
		clue.getSolutions().removeAllStrings(sc);

		assertEquals(test, clue.getSolutions());

	}

}
