package uk.ac.hud.cryptic.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test class provides a testing interface to the SolverServlet - the main
 * servlet within the web service. Both Unit testing and integration testing are
 * performed within this test class. Unit testing is based upon the JUnit Test
 * Framework (Version 4) and the integration testing utilises the Apache
 * HttpComponents packages.
 * 
 * @author Luke Hackett
 * @version 0.1
 */
public class SolverTest {
	// URI scheme value
	private static String scheme;
	// URI host name
	private static String host;
	// URI port
	private static int port;
	// URL path
	private static String path;
	// Closeable client used to protected against memory leaks
	private static CloseableHttpClient client;

	/**
	 * Initialises a number of base variables
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Set the default values
		scheme = "http";
		host = "localhost";
		port = 8080;
		path = "/cryptic/solver";
		client = HttpClients.createDefault();
	}

	/**
	 * Frees the all base variables
	 * 
	 * @throws Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// Clear base values
		scheme = null;
		host = null;
		port = 0;
		path = null;
		client.close();
		client = null;
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to post three blank variables.
	 */
	@Test
	public void testNoParameters() {
		String clue = "";
		String solution = "";
		String pattern = "";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please enter a clue to solve.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the clue.
	 */
	@Test
	public void testClueParameterOnly() {
		String clue = "Found ermine, deer hides damaged";
		String solution = "";
		String pattern = "";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please enter a valid solution format.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the clue and the
	 * solution format.
	 */
	@Test
	public void testClueSolutionParameterOnly() {
		String clue = "Found ermine, deer hides damaged";
		String solution = "10";
		String pattern = "";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please enter a valid pattern format.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the clue and the
	 * pattern format.
	 */
	@Test
	public void testCluePatternParameterOnly() {
		String clue = "Found ermine, deer hides damaged";
		String solution = "";
		String pattern = "??????????";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please enter a valid solution format.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the solution format
	 * and the pattern format.
	 */
	@Test
	public void testSolutionPatternParameterOnly() {
		String clue = "";
		String solution = "10";
		String pattern = "??????????";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please enter a clue to solve.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the solution format.
	 */
	@Test
	public void testSolutionParameterOnly() {
		String clue = "";
		String solution = "10";
		String pattern = "";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please enter a clue to solve.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the pattern format.
	 */
	@Test
	public void testPatternParameterOnly() {
		String clue = "";
		String solution = "";
		String pattern = "??????????";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please enter a clue to solve.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to post all required values, but
	 * will ensure that the pattern format does not match the solution format.
	 */
	@Test
	public void testInvalidPattern() {
		String clue = "Found ermine, deer hides damaged";
		String solution = "10";
		String pattern = "????????";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please ensure the solution format matches the pattern format.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to post all required values, but
	 * will ensure that the solution format does not match the pattern format.
	 */
	@Test
	public void testInvalidSolution() {
		String clue = "Found ermine, deer hides damaged";
		String solution = "5";
		String pattern = "??????????";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String errorMessage = "Please ensure the solution format matches the pattern format.";

		// Run the bad request test
		inputBadRequest(clue, solution, pattern, statusCode, errorMessage);
	}

	/**
	 * This method will test to ensure that a bad request is correctly
	 * identified based upon this given inputs. The method will check the
	 * returned status code, as well as the given error message.
	 * 
	 * @param clue
	 *            the clue to be validated
	 * @param solution
	 *            the solution format to be validated
	 * @param pattern
	 *            the solution pattern format to be validated
	 * @param statusCode
	 *            the expected HTML status code
	 * @param error
	 *            the expected error message
	 */
	private void inputBadRequest(String clue, String solution, String pattern,
			int statusCode, String error) {
		// Formulate a new URI based upon required parameters
		URI uri = getSolverURI(clue, solution, pattern);

		// Obtain the response from the given URL
		CloseableHttpResponse response = getHttpResponse(uri);

		// Get the HTML returned from the server
		String html = getServerOutput(response);

		// Get the returned HTML status code
		int htmlCode = response.getStatusLine().getStatusCode();

		// Test the returned data
		try {
			// Ensure valid HTML was returned
			assertTrue(validHtml(html));

			// Ensure the HTML message contains the status code
			assertTrue(html.contains("HTTP Status " + statusCode));

			// Ensure the HTML message displays the correct message
			assertTrue(html.contains(error));

			// Ensure the server set the correct HTML status code
			assertEquals(statusCode, htmlCode);
		} finally {
			// Close the response
			try {
				response.close();
			} catch (IOException e) {
				// Something has gone wrong.
				fail("HTTP response was not closed: " + e.getMessage());
			}
		}
	}

	/**
	 * This method will decide if a given HTML string can be deemed as being
	 * valid or not
	 * 
	 * @param html
	 *            the HTML String to be analysed
	 * @return True if the HTML is valid
	 */
	private boolean validHtml(String html) {
		// HTML
		if (!html.contains("<html>") || !html.contains("</html>")) {
			return false;
		}

		// HEAD
		if (!html.contains("<head>") || !html.contains("</head>")) {
			return false;
		}

		// BODY
		if (!html.contains("<body>") || !html.contains("</body>")) {
			return false;
		}

		return true;
	}

	/**
	 * This method will execute the given URI as a new POST to the client
	 * 
	 * @param uri
	 *            the URI to be executed
	 * @return The server response object
	 */
	private CloseableHttpResponse getHttpResponse(URI uri) {
		// HTTP Response object
		CloseableHttpResponse response = null;

		try {
			// Create a new HTTP POST based upon the URI
			HttpPost httppost = new HttpPost(uri);

			// Execute the HTTP POST request
			response = client.execute(httppost);

		} catch (IOException e) {
			// Something has REALLY gone wrong.
			fail("Test aborted due to unrecoverable error: " + e.getMessage());
		}

		return response;
	}

	/**
	 * This method will obtain the output that is returned by the server, and
	 * can be either, HTML, XML or JSON.
	 * 
	 * @param response
	 *            the HTTP response object
	 * @return The String created by the server
	 */
	private String getServerOutput(CloseableHttpResponse response) {
		// Concatenated data
		String data = "";

		// Reader to obtain the returned data
		BufferedReader br;

		try {
			// Create a new buffered reader for the server response
			br = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			// Concatenate the String
			String line;
			while ((line = br.readLine()) != null) {
				data += line;
			}

		} catch (IllegalStateException | IOException e) {
			// Something has REALLY gone wrong.
			fail("Test aborted due to unrecoverable error: " + e.getMessage());
		}

		return data;
	}

	/**
	 * This method will create a well-formed URI object that can be used as part
	 * of various HTTP requests. If the URI object is able to be built, the test
	 * that called this method will fail.
	 * 
	 * @param clue
	 *            the clue that is to be sent to the web service
	 * @param solution
	 *            the solution format that is to be sent to the web service
	 * @param pattern
	 *            the solution pattern format that is to be sent to the web
	 *            service
	 * @return A URI object
	 */
	private URI getSolverURI(String clue, String length, String pattern) {
		// Default value
		URI uri = null;

		try {
			uri = new URIBuilder().setScheme(scheme).setHost(host)
					.setPort(port).setPath(path).setParameter("clue", clue)
					.setParameter("length", length)
					.setParameter("pattern", pattern).build();
		} catch (URISyntaxException e) {
			// Something has REALLY gone wrong.
			fail("Test aborted due to unrecoverable error: " + e.getMessage());
		}

		return uri;
	}

}
