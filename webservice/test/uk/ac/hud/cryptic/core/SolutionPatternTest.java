package uk.ac.hud.cryptic.core;

import static org.junit.Assert.*;

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
	public void testMatch() {
		// Single
		assertEquals(true, SolutionPattern.match("?????", "Dream"));
		assertEquals(false, SolutionPattern.match("???????", "Dream"));

		// Hyphen
		assertEquals(true, SolutionPattern.match("???-???", "Top-hat"));
		assertEquals(false, SolutionPattern.match("????-???", "Dream"));

		// Space
		assertEquals(false, SolutionPattern.match("???,??", "Dream"));
		assertEquals(true, SolutionPattern.match("???,???", "the cat"));
		
		// Both
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
}
