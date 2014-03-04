package uk.ac.hud.cryptic.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test class provides a testing interface to the XMLBuilder class.
 * 
 * @author  Luke Hackett
 * @version 0.2
 */
public class XMLBuilderTest {
	// The clue to be used within the tests
	private String clue;
	// The pattern to be used within the tests
	private String pattern;

	/**
	 * Initialises the base variables
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		clue = "My Clue";
		pattern = "My pattern";
	}

	/**
	 * Destroys the base variables
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		clue = "";
		pattern = "";
	}

	/**
	 * This method will test to ensure that an XMLBuilder object can be created
	 * in error mode. 
	 */
	@Test
	public void testXMLBuilder() {
		// Create a new XML object
		XMLBuilder builder = new XMLBuilder();

		// Ensure a blank list is setup
		assert (builder.toString().contains("<solver><errors /></solver>"));
	}

	/**
	 * This method will test to ensure that an XMLBuilder object can be created
	 * in error mode. 
	 */
	@Test
	public void testXMLBuilderStringString() {
		// Create a new XML object
		XMLBuilder builder = new XMLBuilder(clue, pattern);

		// Convert to a String
		String xml = builder.toString();

		// Ensure main elements are present
		containsRoot(xml);
		containsClue(xml, clue);
		containsPattern(xml, pattern);
	}

	/**
	 * This method will test to ensure that an XMLBuilder object can be created
	 * in results mode, with additional optional parameters. 
	 */
	@Test
	public void testXMLBuilderStringStringDouble() {
		// Create a new XML object
		XMLBuilder builder = new XMLBuilder(clue, pattern, 5.0);

		// Convert to a String
		String xml = builder.toString();

		// Ensure main elements are present
		containsRoot(xml);
		containsClue(xml, clue);
		containsPattern(xml, pattern);
		containsDuration(xml, "5.00");
	}

	/**
	 * This method will test to ensure that an XMLBuilder object can add an 
	 * additional custom key/value pair.
	 */
	@Test
	public void testAddKeyValue() {
		// Create a new XML object
		XMLBuilder builder = new XMLBuilder();
		// Create a custom key/value pair
		builder.addKeyValue("MY_KEY", "MY_VALUE");
		// Ensure it is in the XML string
		assert (builder.toString().contains("<MY_KEY>MY_VALUE</MY_KEY>"));
	}

	/**
	 * This method will test to ensure that an XMLBuilder object can add an 
	 * error message to the XML output
	 */
	@Test
	public void testAddError() {
		// Create a new XML object
		XMLBuilder builder = new XMLBuilder();

		// Add the error message
		String message = "This is an error message";
		builder.addError(message);

		// Ensure it is output correctly
		String out = "<errors><message>" + message + "</message></errors>";
		assert (builder.toString().contains(out));
	}

	/**
	 * This method will test to ensure that an XMLBuilder object can add a 
	 * number of error messages to the XML output
	 */
	@Test
	public void testAddErrors() {
		// Create a new XML object
		XMLBuilder builder = new XMLBuilder();

		// Add the error messages
		String[] messages = new String[] { "Error Message #1",
				"Error Message #2", "Error Message #3", "Error Message #4" };
		builder.addErrors(messages);

		// Obtain the XML output
		String xml = builder.toString();

		// Ensure all messages are output correctly
		for (String message : messages) {
			String xmlErr = "<errors><message>" + message
					+ "</message></errors>";
			assert (xml.contains(xmlErr));
		}
	}

	/**
	 * This method will test to ensure that an XMLBuilder object can add a 
	 * given solution to an XML object.
	 */
	@Test
	public void testAddSolution() {
		// Setup some default values
		String solver = "SOLVER";
		String solution = "SOLUTION";
		String confidence = "CONFIDENCE";
		List<String> trace = new ArrayList<String>() {
			{
				add("Step A");
				add("Step B");
				add("Step C");
			}
		};

		// Create a new XML object
		XMLBuilder builder = new XMLBuilder(clue, pattern);

		// Add the default values to the XML output
		builder.addSolution(solver, solution, confidence, trace);

		// Get the XML as a String
		String xml = builder.toString();

		// Ensure the solver used, solution and confidence are output
		assert (xml.contains("<solver>" + solver + "</solver>"));
		assert (xml.contains("<value>" + solution + "</value>"));
		assert (xml.contains("<confidence>" + confidence + "</confidence>"));

		// Ensure all trace steps are output
		for (String t : trace) {
			assert (xml.contains("<trace>" + t + "</trace>"));
		}
	}

	/**
	 * Ensures that the given XML document contains a valid ROOT.
	 * 
	 * @param xml
	 *            the XML string representation
	 */
	private void containsRoot(String xml) {
		// Ensure the tag is present
		assert (xml.contains("<solver>"));
		assert (xml.contains("</solver>"));
	}

	/**
	 * Ensures that the given XML document contains a valid clue tag, and value.
	 * 
	 * @param xml
	 *            the XML string representation
	 * @param clue
	 *            the clue that is to be stored within the clue tag
	 */
	private void containsClue(String xml, String clue) {
		// Ensure the tag is present
		assert (xml.contains("<clue>"));
		assert (xml.contains("</clue>"));

		// Ensure the tag has the correct associated value
		String contain = "<clue>" + clue + "</clue>";
		assert (xml.contains(contain));
	}

	/**
	 * Ensures that the given XML document contains a valid pattern tag, and
	 * value.
	 * 
	 * @param xml
	 *            the XML string representation
	 * @param pattern
	 *            the pattern that is to be stored within the pattern tag
	 */
	private void containsPattern(String xml, String pattern) {
		// Ensure the tag is present
		assert (xml.contains("<pattern>"));
		assert (xml.contains("</pattern>"));

		// Ensure the tag has the correct associated value
		String contain = "<pattern>" + pattern + "</pattern>";
		assert (xml.contains(contain));
	}

	/**
	 * Ensures that the given XML document contains a valid duration tag, and
	 * value.
	 * 
	 * @param xml
	 *            the XML string representation
	 * @param duration
	 *            the duration that is to be stored within the duration tag
	 */
	private void containsDuration(String xml, String duration) {
		// Ensure the tag is present
		assert (xml.contains("<duration>"));
		assert (xml.contains("</duration>"));

		// Ensure the tag has the correct associated value
		String contain = "<duration>" + duration + "</duration>";
		assert (xml.contains(contain));
	}
}
