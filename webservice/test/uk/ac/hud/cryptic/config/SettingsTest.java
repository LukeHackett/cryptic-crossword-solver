package uk.ac.hud.cryptic.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.hud.cryptic.config.Settings.ResourceType;

/**
 * This test class provides a testing interface to the Settings class. The tests
 * are primarily concerned with ensuring that the database connection settings
 * are corrected stored, as well as ensuring that input streams can be obtained
 * for the various files.
 * 
 * @author Luke Hackett, Mohammad Rahman
 * @version 0.2
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
	public void testGetDictionarStream() {
		testStream(settings.getDictionaryStream());
	}

	/**
	 * This test will ensure that a valid dictionary InputStream is returned,
	 * and that data can be read.
	 */
	@Test
	public void testGetCustomDictionaryStream() {
		testStream(settings.getCustomDictionaryStream());
	}

	/**
	 * This test will ensure that a valid dictionary exclusions InputStream is
	 * returned, and that data can be read.
	 */
	@Test
	public void testGetDictionaryExclusionsStream() {
		testStream(settings.getDictionaryExclusionsStream());
	}
	
	/**
	 * This test will ensure that a valid properties InputStream is
	 * returned, and that data can be read.
	 */
	@Test
	public void testGetPropertyStream() {
		testStream(settings.getPropertyStream());
	}
	
	/**
	 * This test will ensure that a valid natural language processing POS
	 * tagging model InputStream is returned, and that data can be read.
	 */
	@Test
	public void testGetPOSModelStream() {
		testStream(settings.getPOSModelStream());
	}
	
	/**
	 * This test will ensure that a valid Chunker model 
	 * InputStream is returned, and that data can be read.
	 */
	@Test
	public void testGetChunkerModelStream() {
		testStream(settings.getChunkerModelStream());
	}
	
	/**
	 * This test will ensure that a valid Categoriser model 
	 * InputStream is returned, and that data can be read.
	 */
	@Test
	public void testGetCategoriserModelStream() {
		testStream(settings.getCategoriserModelStream());
	}

	/**
	 * This test will ensure that a valid Sentence Detector model 
	 * InputStream is returned, and that data can be read.
	 */
	@Test
	public void testGetSentenceDetectorModelStream() {
		testStream(settings.getSentenceDetectorModelStream());
	}
	
	/**
	 * This test will ensure that a valid Parser model 
	 * InputStream is returned, and that data can be read.
	 */
	@Test
	public void testGetParserModelStream() {
		testStream(settings.getParserModelStream());
	}
	
	/**
	 * This test will ensure that a valid Tokeniser model 
	 * InputStream is returned, and that data can be read.
	 */
	@Test
	public void testGetTokeniserModelStream() {
		testStream(settings.getTokeniserModelStream());
	}
	
	/**
	 * This test will ensure that a valid thesaurus InputStream is returned, and
	 * that data can be read.
	 */
	@Test
	public void testGetThesaurusStream() {
		testStream(settings.getThesaurusStream());
	}

	/**
	 * This test will ensure that a valid pronouncing dictionary InputStream is
	 * returned, and that data can be read.
	 */
	@Test
	public void testGetHomophoneDictionaryStream() {
		testStream(settings.getHomophoneDictionaryStream());
	}

	/**
	 * This method will test to ensure that the correct database URL has been
	 * set within the application settings.
	 */
	@Test
	public void testGetDBURL() {
		String helios = "jdbc:mysql://helios.hud.ac.uk:3306/cryptic";
		if (settings.getDBURL() == helios){
			
		}else{
			String aws = "jdbc:mysql://crypticsolver.com:3306";
			assertEquals(aws, settings.getDBURL());
		}
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
	 * This method will test the returned value of a filename does 
	 * not contain any extensions.
	 */
	@Test
	public void testGetFilename() {
		String test = settings.getFileName("anagrams.txt", "txt");
		assertEquals(test, "anagrams");
	}

	/**
	 * This method will test a directory is retrieved from the 
	 * the given classpath, represented as a URL.
	 */
	@Test
	public void testGetIndicatorsURL() {
		URL url = settings.getIndicatorsURL(ResourceType.ASSET, "indicators");
		assertNotNull(url);
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
