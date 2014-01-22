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

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;
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

	//
	public enum HttpVerb {
		GET, POST, PUT, DELETE
	}

	public enum HttpResponse {
		HTML, XML, JSON
	}

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
	 * bad request. This method will attempt to post three blank variables, and
	 * will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testNoParameters() {
		// Input Values
		String clue = "";
		String solution = "";
		String pattern = "";
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		String message = "Please enter a clue to solve.";

		// Run the Test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the clue, and will
	 * test for HTML, XML and JSON responses.
	 */
	@Test
	public void testClueParameterOnly() {
		// Input Values
		String clue = "Found ermine, deer hides damaged";
		String solution = "";
		String pattern = "";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please enter a valid solution format.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the clue and the
	 * solution format, and will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testClueSolutionParameterOnly() {
		// Input Values
		String clue = "Found ermine, deer hides damaged";
		String solution = "10";
		String pattern = "";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please enter a valid pattern format.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the clue and the
	 * pattern format, and will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testCluePatternParameterOnly() {
		// Input Values
		String clue = "Found ermine, deer hides damaged";
		String solution = "";
		String pattern = "??????????";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please enter a valid solution format.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the solution format
	 * and the pattern format, and will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testSolutionPatternParameterOnly() {
		// Input Values
		String clue = "";
		String solution = "10";
		String pattern = "??????????";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please enter a clue to solve.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the solution format,
	 * and will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testSolutionParameterOnly() {
		// Input Values
		String clue = "";
		String solution = "10";
		String pattern = "";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please enter a clue to solve.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to only post the pattern format,
	 * and will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testPatternParameterOnly() {
		// Input Values
		String clue = "";
		String solution = "";
		String pattern = "??????????";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please enter a clue to solve.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to post all required values, but
	 * will ensure that the pattern format does not match the solution format,
	 * and will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testInvalidPattern() {
		// Input Values
		String clue = "Found ermine, deer hides damaged";
		String solution = "10";
		String pattern = "????????";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please ensure the solution format matches the pattern format.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This method will test to ensure that the server does not try to process a
	 * bad request. This method will attempt to post all required values, but
	 * will ensure that the solution format does not match the pattern format,
	 * and will test for HTML, XML and JSON responses.
	 */
	@Test
	public void testInvalidSolution() {
		// Input Values
		String clue = "Found ermine, deer hides damaged";
		String solution = "5";
		String pattern = "??????????";
		// Expected HTML status code
		int statusCode = HttpServletResponse.SC_BAD_REQUEST;
		// Expected output message
		String message = "Please ensure the solution format matches the pattern format.";

		// Run the test
		doTest(clue, solution, pattern, statusCode, message);
	}

	/**
	 * This test helper method will run a test with the given test data clue,
	 * solution and pattern.The method will make a request that expects HTML,
	 * XML and JSON return types, and will validate the response against the
	 * given HTML status code and the message that should be found within the
	 * response.
	 * 
	 * @param clue
	 *            the clue to be validated
	 * @param solution
	 *            the solution format to be validated
	 * @param pattern
	 *            the solution pattern format to be validated
	 * @param statusCode
	 *            the expected HTML status code
	 * @param message
	 *            the expected error message
	 */
	private void doTest(String clue, String solution, String pattern,
			int statusCode, String message) {
		// POST & GET Response
		CloseableHttpResponse response;

		// Test HTML POST request
		response = getServerResponse(clue, solution, pattern, HttpVerb.POST,
				HttpResponse.HTML);
		testHtmlResponse(response, HttpVerb.POST, 200, message);
		response = null;

		// Test XML POST request
		response = getServerResponse(clue, solution, pattern, HttpVerb.POST,
				HttpResponse.XML);
		testXmlResponse(response, HttpVerb.POST, statusCode, message);
		response = null;

		// Test JSON POST request
		response = getServerResponse(clue, solution, pattern, HttpVerb.POST,
				HttpResponse.JSON);
		testJsonResponse(response, HttpVerb.POST, statusCode, message);
		response = null;

		// Test HTML GET request
		response = getServerResponse(clue, solution, pattern, HttpVerb.GET,
				HttpResponse.HTML);
		testHtmlResponse(response, HttpVerb.GET, HttpServletResponse.SC_OK,
				message);
		response = null;

		// Test XML GET request
		response = getServerResponse(clue, solution, pattern, HttpVerb.GET,
				HttpResponse.XML);
		testXmlResponse(response, HttpVerb.GET, statusCode, message);
		response = null;

		// Test JSON GET request
		response = getServerResponse(clue, solution, pattern, HttpVerb.GET,
				HttpResponse.JSON);
		testJsonResponse(response, HttpVerb.GET, statusCode, message);

		// Close the response
		try {
			if (response != null) {
				response.close();
			}
		} catch (IOException e) {
			// Something has REALLY gone wrong.
			fail("Test aborted due to unrecoverable error: " + e.getMessage());
			response = null;
		}
	}

	/**
	 * This method will validate a HTML response against the status code and the
	 * given message.
	 * 
	 * @param response
	 *            the HTTP response object to be validated
	 * @param action
	 *            the type of request that was made
	 * @param status
	 *            the HTML status code expected to be found as part of the
	 *            response
	 * @param message
	 *            the expected message to be found within the response
	 */
	private void testHtmlResponse(CloseableHttpResponse response,
			HttpVerb action, int status, String message) {
		// Obtain the server's HTML response
		String html = getServerOutput(response);

		// Ensure the response is HTML
		String contentType = response.getEntity().getContentType().getValue();
		assertTrue(contentType.contains("text/html"));

		// Ensure the html has the required html tags
		assertTrue(html.contains("<!DOCTYPE html><html lang=\"en\">"));
		assertTrue(html.contains("</html>"));

		// Ensure the html has the required head tags
		assertTrue(html.contains("<head>"));
		assertTrue(html.contains("</head>"));

		// Ensure the html has the required head tags
		assertTrue(html.contains("<body>"));
		assertTrue(html.contains("</body>"));

		// Ensure the html has cryptic title
		assertTrue(html.contains("<title>Cryptic Crossword Solver</title>"));

		// Ensure the expected status code is returned
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals(status, statusCode);

		// Ensure the HTML message displays expected message
		assertTrue(html.contains(message));
	}

	/**
	 * This method will validate an XML response against the status code and the
	 * given message.
	 * 
	 * @param response
	 *            the HTTP response object to be validated
	 * @param action
	 *            the type of request that was made
	 * @param status
	 *            the HTML status code expected to be found as part of the
	 *            response
	 * @param message
	 *            the expected message to be found within the response
	 */
	private void testXmlResponse(CloseableHttpResponse response,
			HttpVerb action, int status, String message) {
		// Obtain the server's XML response
		String xml = getServerOutput(response);

		// Ensure the response is XML
		String contentType = response.getEntity().getContentType().getValue();
		assertEquals("application/xml", contentType);

		// Ensure the XML response has a root tag
		assertTrue(xml.contains("<solver>"));
		assertTrue(xml.contains("</solver>"));

		// Ensure the expected status code is returned
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals(status, statusCode);

		// Ensure the XML message displays expected message
		assertTrue(xml.contains(message));
	}

	/**
	 * This method will validate a JSON response against the status code and the
	 * given message.
	 * 
	 * @param response
	 *            the HTTP response object to be validated
	 * @param action
	 *            the type of request that was made
	 * @param status
	 *            the HTML status code expected to be found as part of the
	 *            response
	 * @param message
	 *            the expected message to be found within the response
	 */
	private void testJsonResponse(CloseableHttpResponse response,
			HttpVerb action, int status, String message) {
		// Obtain the server's JSON response
		String json = getServerOutput(response);

		// Ensure the response is JSON
		String contentType = response.getEntity().getContentType().getValue();
		assertEquals("application/json", contentType);

		// Ensure JSON is well formed
		try {
			new JSONObject(json);
		} catch (JSONException e) {
			fail("JSON String parse error: " + e.getMessage());
		}

		// Ensure the expected status code is returned
		int statusCode = response.getStatusLine().getStatusCode();
		assertEquals(status, statusCode);

		// Ensure the JSON message displays expected message
		assertTrue(json.contains(message));
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
	private CloseableHttpResponse getServerResponse(String clue,
			String solution, String pattern, HttpVerb action,
			HttpResponse returnType) {

		// Formulate a new URI based upon required parameters
		URI uri = getSolverURI(clue, solution, pattern);

		// Obtain the response from the given URL
		return getHttpResponse(uri, action, returnType);
	}

	/**
	 * This method will execute the given URI as the given action by the default
	 * test client.
	 * 
	 * @param uri
	 *            the URI to be executed
	 * @param action
	 *            the HTTP action to be performed
	 * @param returnType
	 *            the expected return type
	 * @return The server response object
	 */
	private CloseableHttpResponse getHttpResponse(URI uri, HttpVerb action,
			HttpResponse returnType) {
		// HTTP Response object
		CloseableHttpResponse response = null;

		try {
			// The HTTP request to be made
			HttpRequestBase request = null;

			// Cast the given action to the HTTP request
			switch (action) {
				case GET:
					request = new HttpGet(uri);
					break;

				case POST:
					request = new HttpPost(uri);
					break;

				default:
					fail("An error has occured: " + action.toString()
							+ " is not supported");
			}

			// Add additional headers if required (XML/JSON only)
			if (!returnType.equals(HttpResponse.HTML)) {
				// Add the Accept Header
				String contentType = String.format("application/%s", returnType
						.name().toLowerCase());
				request.addHeader(HttpHeaders.ACCEPT, contentType);
				// Set the AJAX header
				request.addHeader("x-requested-with", "xmlhttprequest");
			}

			// Execute the HTTP POST request
			response = client.execute(request);
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
