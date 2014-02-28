package uk.ac.hud.cryptic.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class provides a testing interface to the Settings class. The tests
 * are primarily concerned with ensuring that the database connection settings
 * are corrected stored, as well as ensuring that input streams can be obtained
 * for the various files.
 * 
 * @author Luke Hackett
 * @version 0.1
 */
public class SettingsTest {
	// Settings instance under test within this unit test
	private static Settings settings;

	/**
	 * This method will obtain the current instance of the settings class in
	 * order for the tests to be run.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		settings = Settings.getInstance();
	}

	/**
	 * This method will destroy the current instance of the settings class.
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		settings = null;
	}

	/**
	 * This test will ensure that a valid thesaurus InputStream is returned, and
	 * that data can be read.
	 */
	@Test
	public void testDictionaryPath() {
		testStream(settings.getDictionaryStream());
	}

	/**
	 * This test will ensure that a valid dictionary InputStream is returned,
	 * and that data can be read.
	 */
	@Test
	public void testGetCustomDictionaryPath() {
		testStream(settings.getCustomDictionaryStream());
	}

	/**
	 * This test will ensure that a valid dictionary exclusions InputStream is
	 * returned, and that data can be read.
	 */
	@Test
	public void testGetDictionaryExclusionsPath() {
		testStream(settings.getDictionaryExclusionsStream());
	}

	/**
	 * This test will ensure that a valid thesaurus InputStream is returned, and
	 * that data can be read.
	 */
	@Test
	public void testGetThesaurusPath() {
		testStream(settings.getThesaurusStream());
	}

	/**
	 * This test will ensure that a valid pronouncing dictionary InputStream is
	 * returned, and that data can be read.
	 */
	@Test
	public void testGetPronouncingDictionaryPath() {
		testStream(settings.getHomophoneDictionaryStream());
	}

	/**
	 * This method will test to ensure that the correct database URL has been
	 * set within the application settings.
	 */
	@Test
	public void testGetDBURL() {
		String url = "jdbc:mysql://crypticsolver.com:3306";
		assertEquals(url, settings.getDBURL());
	}

	/**
	 * This method will test to ensure that the correct database username has
	 * been set within the application settings.
	 */
	@Test
	public void testGetDBUsername() {
		assertEquals("cryptic", settings.getDBUsername());
	}

	/**
	 * This method will test to ensure that the correct database password has
	 * been set within the application settings.
	 */
	@Test
	public void testGetDBPassword() {
		assertEquals("du4hacrEKa", settings.getDBPassword());
	}

	/**
	 * This method provides a generic interface to test to ensure that a given
	 * InputStrem is able to "contact" the associated file.
	 * 
	 * @param stream
	 *            The InputStream to be tested
	 */
	private void testStream(InputStream stream) {
		try {
			// Check to see if data is available to be read
			int available = stream.available();
			assertTrue(available > 0);

			// Check to see if the next byte of data can be read
			int data = stream.read();
			assertNotSame(-1, data);

		} catch (IOException e) {
			// Something went wrong
			fail("A serious error ocurred: " + e.getMessage());
		}
	}

}
