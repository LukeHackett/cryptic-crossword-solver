/**
 * This JUnit Class provides the unit and integration tests for the Clue class.
 * The tests are written and compiled in accordance to JUnit 4
 */
package uk.ac.hud.cryptic.core;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.hud.cryptic.util.WordUtils;

/**
 * @author Mohammad Rahman
 * @version 0.1
 * 
 */
public class ClueTest {

	//Test Data
	private SolutionPattern pattern = new SolutionPattern("?????");
	private Clue test = new Clue("Air that's more usually seen in cheerleaders", 
			pattern.toString()); //MUSIC

	@Test
	//Get clue in lower case
	public void testGetClue() {
		assertEquals("Air that's more usually seen in cheerleaders", 
				"air that's more usually seen in cheerleaders",
				test.getClue());
	}

	@Test
	//Get clue no punctuation with spaces
	public void testGetClueNoWithPunctuation() {		
		assertEquals("air thats more usually seen in cheerleaders",
				test.getClueNoPunctuation(false));
	}

	@Test
	//Get clue no punctuation without spaces
	public void testGetClueNoPunctuationNoSpaces() {		
		assertEquals("airthatsmoreusuallyseenincheerleaders",
				test.getClueNoPunctuation(true));
	}

	@Test
	//Separate the words in a clue
	public void testGetClueWords() {	

		String clue = test.getClueNoPunctuation(false).trim();
		String [] splitclues = clue.split(WordUtils.REGEX_WHITESPACE);

		assertArrayEquals(splitclues, test.getClueWords());
	}

	@Test
	//Get the clues pattern as String
	public void testGetPattern() {		
		assertEquals(pattern.toString(),
				test.getPattern().toString());
	}

}
