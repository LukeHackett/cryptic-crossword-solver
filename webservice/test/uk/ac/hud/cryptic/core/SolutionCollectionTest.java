/**
 * This JUnit Class provides the unit and integration tests for the Manager
 * class. The tests are written and compiled in accordance to JUnit 4
 * 
 * @author Mohammad Rahman
 * @version 0.2
 */

package uk.ac.hud.cryptic.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SolutionCollectionTest {

	private static SolutionPattern pattern;
	private static Clue clue;
	private SolutionCollection sols;

	@Before
	public void setUp() throws Exception {
		pattern = new SolutionPattern("?????");
		clue = new Clue("Air that's more usually seen in cheerleaders",
				pattern.toString());

		sols = new SolutionCollection();
		sols.add(new Solution("smore", 10, "undefined"));
		sols.add(new Solution("usual", 13, "undefined"));
		sols.add(new Solution("taths", 20, "undefined"));
		sols.add(new Solution("cheer", 7, "undefined"));
		sols.add(new Solution("music", 90, "undefined"));
		sols.add(new Solution("cered", 5, "undefined"));
		sols.add(new Solution("salse", 44, "undefined"));
		clue.setSolutions(sols);
	}

	@After
	public void tearDown() throws Exception {
		pattern = null;
		clue = null;
		sols = null;
	}

	/**
	 * This tests checks the Solution collection contain the solution string
	 */
	@Test
	public final void testContainsString() {

		String solution = "music";
		assertTrue(clue.getSolutions().contains(solution));

	}

	/**
	 * This tests checks the Solution with the highest confidence from the
	 * Solution Collection
	 */
	@Test
	public final void testGetBestSolution() {
		Solution bestSolution = new Solution("music", 90, "undefined");
		assertEquals(clue.getSolutions().getBestSolution(), bestSolution);
	}

	/**
	 * This tests checks only solutions greater then the specified confidence
	 * score are returned only
	 */
	@Test
	public final void testGetSolutionsGreaterThan() {

		// Test Data
		SolutionCollection sc = new SolutionCollection();
		sc.add(new Solution("music", 90, "undefined"));
		sc.add(new Solution("salse", 44, "undefined"));

		int confidence = 35;
		assertEquals(sc, clue.getSolutions()
				.getSolutionsGreaterThan(confidence));

	}

	/**
	 * This tests checks only solutions less than the specified confidence score
	 * are returned only
	 */
	@Test
	public final void testGetSolutionsLessThan() {
		// Test Data
		SolutionCollection sc = new SolutionCollection();
		sc.add(new Solution("smore", 10, "undefined"));
		sc.add(new Solution("usual", 13, "undefined"));
		sc.add(new Solution("taths", 20, "undefined"));
		sc.add(new Solution("cheer", 7, "undefined"));
		sc.add(new Solution("cered", 5, "undefined"));

		int confidence = 35;
		assertEquals(sc, clue.getSolutions().getSolutionsLessThan(confidence));
	}

	/**
	 * This tests checks that a collection of solutions are removed from the
	 * solution collection.
	 */
	@Test
	public final void testRemoveAllStrings() {

		// Test data
		SolutionCollection test = new SolutionCollection();
		test.add(new Solution("music", 90, "undefined"));
		test.add(new Solution("salse", 44, "undefined"));
		Collection<String> sc = new ArrayList<String>();
		sc.add("smore");
		sc.add("usual");
		sc.add("taths");
		sc.add("cheer");
		sc.add("cered");

		// Test Method
		clue.getSolutions().removeAllStrings(sc);

		assertEquals(test, clue.getSolutions());

	}

	/**
	 * Test the returned solutions are sorted by their confidence ratings
	 */
	@Test
	public final void testSortSolutions() {

		Set<Solution> set = sols.sortSolutions(); //Converting to TreeSet
		Boolean sorted = null;
		Solution current;
		Solution previous = new Solution("","");
		previous.setConfidence(100);

		Iterator<Solution> itr = set.iterator();
		while(itr.hasNext()) {
			current = itr.next();
			if(current.getConfidence() <= previous.getConfidence()){
				sorted = true;
				previous = current;
			}else{
				sorted = false;
				break;
			}
		}
		Boolean pass = true;
		assertEquals(pass,sorted);
	}

	/**
	 * Test the Solution object matches the passed String solution
	 */
	@Test
	public final void testGetSolutions() {
		Solution test = new Solution("music");
		test.setConfidence(90);
		assertEquals(sols.getSolution("music"),test );
	}

	/**
	 * Test a new solution is added to the collection.
	 */
	@Test
	public final void testAddNewSolution() {
		boolean result;
			Solution solution = new Solution("usimu");
			solution.setConfidence(1);
			sols.add(solution);
			result = true;
			assertTrue(result); //New Solution added
	}
	
	/**
	 * Test a duplicate solutions are not added to the collection
	 */
	@Test
	public final void testAddDuplicate() {
		boolean result;
		if(sols.contains("music")){
			result = false;
			assertFalse(result); //Cannot add duplicates
		}
	}
	
	/**
	 * Test when a duplicate solution is entered with a higher confidence it replaces
	 * the previous solution.
	 */
	@Test
	public final void testAddIncreaseConfidence() {

		Solution solution = new Solution("music");
		solution.setConfidence(99);
		sols.add(solution);
		Solution res = sols.getSolution("music");
		assertEquals(solution, res);
	}
	
}
