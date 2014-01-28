/**
 * 
 */
package uk.ac.hud.cryptic.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Stuart Leader
 */
public class WordUtilsTest {

	/**
	 * Test method for
	 * {@link uk.ac.hud.cryptic.util.WordUtils#hasCharacters(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testHasCharacters() {
		// Just enough characters
		assertTrue(WordUtils.hasCharacters("hello", "hello"));
		// Enough but the wrong ones
		assertFalse(WordUtils.hasCharacters("hello", "hrllo"));
		// Spaces shouldn't matter
		assertTrue(WordUtils.hasCharacters("hello", "h e l l o"));
		// Ignore excess characters
		assertTrue(WordUtils.hasCharacters("hello",
				"helloandsomemorecharacters"));
		// Word can't be made
		assertFalse(WordUtils
				.hasCharacters("hello", "not the right characters"));
		// Plenty of characters but just not the right one
		assertFalse(WordUtils.hasCharacters("g",
				"the quick brown fox jumps over the lazy do"));
		// The right character but not enough of them
		assertFalse(WordUtils.hasCharacters("oooo",
				"not enough o's in this sentence"));
		// Enough of the correct character
		assertTrue(WordUtils.hasCharacters("oooo",
				"there are enough ooo's in this sentence"));
		// No available characters
		assertFalse(WordUtils.hasCharacters("aww", ""));
		// Should pass, I guess
		assertTrue(WordUtils.hasCharacters("", "well this is strange"));
	}

	/**
	 * Test method for
	 * {@link uk.ac.hud.cryptic.util.WordUtils#normaliseInput(java.lang.String, boolean)}
	 * .
	 */
	@Test
	public void testNormaliseInput() {
		final String spaces = "this is a test sentence";
		final String noSpaces = "thisisatestsentence";
		// Typical sentence
		assertEquals(spaces,
				WordUtils.normaliseInput("This is a test sentence.", false));
		// Remove spaces
		assertEquals(noSpaces,
				WordUtils.normaliseInput("This is a test sentence.", true));
		// Remove all punctuation and spaces
		assertEquals(noSpaces, WordUtils.normaliseInput(
				"\"[This is (a) test-sentence…!?]\"", true));
		// Remove all punctuation - watch out for the hyphen
		assertEquals(spaces, WordUtils.normaliseInput(
				"\"[This is (a) test-sentence…!?]\"", false));
		// Now we're just going crazy
		assertEquals(
				spaces,
				WordUtils
						.normaliseInput(
								"\"[This!\"£$%&^$ is986 (5486409-*/)a ~}{@#]['¬`¬test-sentence…!?]|\\/\"",
								false));
		assertEquals(
				noSpaces,
				WordUtils
						.normaliseInput(
								"\"[This!\"£$%&^$ is986 (5486409-*/)a ~}{@#]['¬`¬test-sentence…!?]|\\/\"",
								true));
		// Nothing
		assertEquals("", WordUtils.normaliseInput("", false));
		assertEquals("", WordUtils.normaliseInput("", true));

		// Cater for accents and umlauts etc...
		assertEquals(spaces,
				WordUtils.normaliseInput("ŤhĩŜ ïş ā ťĔŝt śëņtêŃçê", false));

		// Captials
		assertEquals(spaces,
				WordUtils.normaliseInput("THIS IS A TEST SENTENCE!!!", false));
	}

	/**
	 * Test method for
	 * {@link uk.ac.hud.cryptic.util.WordUtils#removeSpacesAndHyphens(java.lang.String)}
	 * .
	 */
	@Test
	public void testRemoveSpacesAndHyphens() {
		final String expected = "thisisatestsentence";
		// Should come back the same
		assertEquals("", WordUtils.removeSpacesAndHyphens(""));
		// Typical inputs
		assertEquals(expected,
				WordUtils.removeSpacesAndHyphens("this is a test sentence"));
		assertEquals(expected,
				WordUtils.removeSpacesAndHyphens("this is a test-sentence"));
		// Not so typical
		assertEquals(
				expected,
				WordUtils
						.removeSpacesAndHyphens(" --th -i-s is - a  -t -est sent--ence--  - "));
		// Capitilisation is handled by another method
		assertTrue("Capital"
				.equals(WordUtils.removeSpacesAndHyphens("Capital")));
		// This shouldn't happen but okay
		assertEquals("", WordUtils.removeSpacesAndHyphens(" --- -- -  - -- - "));

	}

	/**
	 * Test method for
	 * {@link uk.ac.hud.cryptic.util.WordUtils#charactersPresentInWord(java.lang.String, java.lang.String[])}
	 * .
	 */
	@Test
	public void testCharactersPresentInWord() {
		// It takes no characters to make no characters
		assertTrue(WordUtils.charactersPresentInWord("", new String[] { "" }));
		// Mandatory words are present
		assertTrue(WordUtils.charactersPresentInWord("hello", new String[] {
				"h", "e", "l", "o" }));
		// Mandatory words are not present
		assertFalse(WordUtils.charactersPresentInWord("hello", new String[] {
				"h", "e", "p" }));
		// Check there are enough characters
		assertTrue(WordUtils.charactersPresentInWord("hello", new String[] {
				"h", "e", "l", "l", "o" }));
		assertFalse(WordUtils.charactersPresentInWord("hello", new String[] {
				"h", "e", "l", "l", "l", "o" }));
		// Double check!
		assertTrue(WordUtils.charactersPresentInWord("o", new String[] {
				"o" }));
		assertTrue(WordUtils.charactersPresentInWord("oo", new String[] {
		"o" }));
		assertFalse(WordUtils.charactersPresentInWord("o", new String[] {
		"o" , "o" }));
	}

}
