package uk.ac.hud.cryptic.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

public class SolutionTest {

	private static Solution solution = new Solution("Dream");
	private static Solution solutionConfidence = new Solution("Dream", 75);
	
	@Test
	public void testAddToTraceAndGetSolutionTrace()
	{
		Collection<String> testColl = new ArrayList<>();
		assertEquals(testColl, solution.getSolutionTrace());
		String traceEntry = "Looking through Acrostic";
		solution.addToTrace(traceEntry);
		testColl.add(traceEntry);
		assertEquals(testColl, solution.getSolutionTrace());
	}
	
	@Test
	public void testCompareToSameConfidencesAndSolutions()
	{
		Solution solutionToCompare = new Solution("Dream", 75);
		assertEquals(0, solutionConfidence.compareTo(solutionToCompare));
	}
	
	@Test
	public void testCompareToSameConfidencesAndDifferentSolutions()
	{
		Solution solutionToCompare = new Solution("Fantasy", 75);
		assertEquals(-2, solutionConfidence.compareTo(solutionToCompare));
	}
	
	@Test
	public void testCompareToDifferentConfidencesAndSameSolutions()
	{
		Solution solutionToCompare = new Solution("Dream", 50);
		assertEquals(0, solutionConfidence.compareTo(solutionToCompare));
	}
	
	@Test
	public void testCompareToDifferentConfidencesAndSolutions()
	{
		Solution solutionToCompare = new Solution("Fantasy", 50);
		assertEquals(1, solutionConfidence.compareTo(solutionToCompare));
	}
	
	@Test
	public void testEqualsDifferentSolutionAndConfidence()
	{
		Solution solutionToCompare = new Solution("Fantasy", 75);
		assertEquals(false, solutionConfidence.equals(solutionToCompare));
	}
	
	@Test
	public void testEqualsSameSolutionAndConfidence()
	{
		Solution solutionToCompare = new Solution("Dream", 75);
		assertEquals(true, solutionConfidence.equals(solutionToCompare));
	}
	
	@Test
	public void testEqualsSameSolutionAndDifferentConfidence()
	{
		Solution solutionToCompare = new Solution("Dream", 50);
		assertEquals(true, solutionConfidence.equals(solutionToCompare));
	}
	
	@Test
	public void testEqualsDifferentSolutionAndSameConfidence()
	{
		Solution solutionToCompare = new Solution("Fantasy", 75);
		assertEquals(false, solutionConfidence.equals(solutionToCompare));
	}
	
	@Test
	public void testGetConfidence()
	{
		assertEquals(0, Double.compare(75.00, solutionConfidence.getConfidence()));
	}
	
	@Test
	public void testGetSolution()
	{
		assertEquals("dream", solutionConfidence.getSolution());
	}
	
	@Test
	public void testSetConfidence()
	{
		Solution solution = new Solution("Test");
		assertEquals(true, solution.setConfidence(50));
		assertEquals(0, Double.compare(50.00, solution.getConfidence()));
	}
	
	@Test
	public void testToString()
	{
		assertEquals("dream [75%]", solutionConfidence.toString());
	}
}
