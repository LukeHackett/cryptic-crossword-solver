package uk.ac.hud.cryptic.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

public class SolutionPatternTest {

	@Test
	public void testToPattern() {
		// Single word
		String p1 = SolutionPattern.toPattern("hello", true);
		String p2 = SolutionPattern.toPattern("hello", false);
		assertEquals("?????", p1);
		assertEquals("hello", p2);

		// Spaces
		String p3 = SolutionPattern.toPattern("over the top", true);
		String p4 = SolutionPattern.toPattern("over the top", false);
		assertEquals("????,???,???", p3);
		assertEquals("over,the,top", p4);

		// Hyphens
		String p5 = SolutionPattern.toPattern("is inside-out", true);
		String p6 = SolutionPattern.toPattern("is inside-out", false);
		assertEquals("??,??????-???", p5);
		assertEquals("is,inside-out", p6);

		// Test it doesn't fall over
		String p7 = SolutionPattern.toPattern("", true);
		assertEquals("", p7);
	}

	@Test
	public void testStaticMatch() {
		// Single
		assertEquals(true, SolutionPattern.match("?????", "dream"));
		assertEquals(false, SolutionPattern.match("???????", "dream"));

		// Hyphen
		assertEquals(true, SolutionPattern.match("???-???", "top-hat"));
		assertEquals(false, SolutionPattern.match("????-???", "dream"));

		// Spaces are not yet handled
		assertEquals(false, SolutionPattern.match("???,?????", "the dream"));
		assertEquals(false, SolutionPattern.match("???,???", "the cat"));
	}

	@Test
	public void testToString() {
		SolutionPattern solPattern = new SolutionPattern("?????");
		assertEquals("?????", solPattern.toString());

		SolutionPattern solutionPattern = new SolutionPattern("??A??");
		assertEquals("??a??", solutionPattern.toString());
	}

	@Test
	public void testGetKnownChars() {
		// Test no known characters
		SolutionPattern sp1 = new SolutionPattern("?????");
		String[] s1 = sp1.getKnownCharacters();
		assertEquals(0, s1.length);

		SolutionPattern sp2 = new SolutionPattern("?????,??-??,??");
		String[] s2 = sp2.getKnownCharacters();
		assertEquals(0, s2.length);

		// Single word, no repeats
		SolutionPattern sp3 = new SolutionPattern("halo");
		String[] s3 = sp3.getKnownCharacters();
		assertEquals("halo", stringArrayToString(s3));

		// Single word, repeats
		SolutionPattern sp4 = new SolutionPattern("hello");
		String[] s4 = sp4.getKnownCharacters();
		assertEquals("hello", stringArrayToString(s4));

		// With spacers, no repeats
		SolutionPattern sp5 = new SolutionPattern("in??,??e-m?s?");
		String[] s5 = sp5.getKnownCharacters();
		assertEquals("inems", stringArrayToString(s5));

		// With spacers, repeats
		SolutionPattern sp6 = new SolutionPattern("??e,s?ll?-sa?sa??s");
		String[] s6 = sp6.getKnownCharacters();
		assertEquals("esllsasas", stringArrayToString(s6));
	}

	private static String stringArrayToString(String[] input) {
		StringBuilder builder = new StringBuilder();
		for (String s : input) {
			builder.append(s);
		}
		return builder.toString();
	}

	@Test
	public void testGetNumberOfWords() {
		SolutionPattern solPattern = new SolutionPattern("????????");
		assertEquals(1, solPattern.getNumberOfWords());

		SolutionPattern solutionPattern = new SolutionPattern("??-????-??");
		assertEquals(3, solutionPattern.getNumberOfWords());
	}

	@Test
	public void testGetPattern() {
		SolutionPattern solPattern = new SolutionPattern("??-????-??");
		assertEquals("??-????-??", solPattern.getPattern());
	}

	@Test
	public void testRecomposeSolution() {
		// Single word
		SolutionPattern sp1 = new SolutionPattern("?????");
		String s1 = sp1.recomposeSolution("hello");
		assertEquals("hello", s1);

		// Single word, some known chars
		SolutionPattern sp2 = new SolutionPattern("?e??o");
		String s2 = sp2.recomposeSolution("hello");
		assertEquals("hello", s2);

		// Single word, all known chars
		SolutionPattern sp3 = new SolutionPattern("hello");
		String s3 = sp3.recomposeSolution("hello");
		assertEquals("hello", s3);

		// Multi-word
		SolutionPattern sp5 = new SolutionPattern("???,???-??????");
		String s5 = sp5.recomposeSolution("thebigdipper");
		assertEquals("the big-dipper", s5);

		// Multi-word, some known chars
		SolutionPattern sp6 = new SolutionPattern("?he,??g-d?p?er");
		String s6 = sp6.recomposeSolution("thebigdipper");
		assertEquals("the big-dipper", s6);

		// Multi-word, all known chars
		SolutionPattern sp7 = new SolutionPattern("the,big-dipper");
		String s7 = sp7.recomposeSolution("thebigdipper");
		assertEquals("the big-dipper", s7);
	}

	@Test
	public void testGetTotalLength() {
		SolutionPattern solPattern = new SolutionPattern("????????");
		assertEquals(8, solPattern.getTotalLength());

		SolutionPattern solutionPattern = new SolutionPattern("??-????,??");
		assertEquals(8, solutionPattern.getTotalLength());
	}

	@Test
	public void testHasMultipleWords() {
		SolutionPattern solPattern = new SolutionPattern("????????");
		assertEquals(false, solPattern.hasMultipleWords());

		SolutionPattern solutionPattern = new SolutionPattern("??-????,??");
		assertEquals(true, solutionPattern.hasMultipleWords());
	}

	@Test
	public void testFilterSolutions() {
		HashSet<Solution> solutions = new HashSet<Solution>();
		solutions.add(new Solution("pink"));
		solutions.add(new Solution("blue"));
		solutions.add(new Solution("dream"));
		solutions.add(new Solution("hello"));
		solutions.add(new Solution("dog"));
		HashSet<Solution> filtered = new HashSet<Solution>();
		filtered.add(new Solution("dream"));
		filtered.add(new Solution("hello"));
		SolutionPattern solPattern = new SolutionPattern("?????");
		solPattern.filterSolutions(solutions);
		assertEquals(filtered, solutions);
	}

	@Test
	public void testGetIndividualWordLengths() {
		SolutionPattern solPattern = new SolutionPattern("??-????,??");
		int[] wordLengths = { 2, 4, 2 };
		assertArrayEquals(wordLengths, solPattern.getIndividualWordLengths());
	}

	@Test
	public void testMatch() {
		// Single
		SolutionPattern sp1 = new SolutionPattern("?????");
		assertTrue(sp1.match("dream"));
		assertFalse(sp1.match("drea"));
		assertFalse(sp1.match("dreamy"));
		// As pattern is ?, this matches anything.
		// TODO Check ?s of pattern correspond to alphabet only of the proposed
		// solution (not critical)
		assertTrue(sp1.match("?????"));

		// Hyphen
		SolutionPattern sp2 = new SolutionPattern("???-???");
		assertTrue(sp2.match("top-hat"));
		// Should re-space and match
		assertTrue(sp2.match("tophat"));
		assertTrue(sp2.match("top hat"));
		// Should fail
		assertFalse(sp2.match("top-ha"));
		assertFalse(sp2.match("top-hatt"));

		// Spaces and hyphens
		SolutionPattern sp3 = new SolutionPattern("???,???-??????");
		assertTrue(sp3.match("thebigdipper"));
		assertTrue(sp3.match("the big dipper"));
		assertTrue(sp3.match("the big-dipper"));
		assertTrue(sp3.match("the-big-dipper"));
		assertFalse(sp3.match("he big-dipper"));
		assertFalse(sp3.match("the bigg-dipper"));

		// Known chars
		SolutionPattern sp4 = new SolutionPattern("?h?,?ig-??pp?r");
		assertTrue(sp4.match("thebigdipper"));
		assertTrue(sp4.match("the big dipper"));
		assertTrue(sp4.match("the big-dipper"));
		assertFalse(sp4.match("yjrnohfooort"));
		assertFalse(sp4.match("yjr noh-fooort"));
		assertFalse(sp4.match("yjr-noh-fooort"));
	}

	@Test
	public void testSeparateSolution() {
		SolutionPattern solPattern = new SolutionPattern("????,??????,??,?????");
		String[] splitSolution = { "this", "should", "be", "split" };
		assertArrayEquals(splitSolution,
				solPattern.separateSolution("thisshouldbesplit"));
	}

	@Test
	public void testSplitPattern() {
		SolutionPattern solPattern = new SolutionPattern("??-????-??");
		String[] splitPattern = { "??", "????", "??" };
		assertArrayEquals(splitPattern, solPattern.splitPattern());
	}
}
