package uk.ac.hud.cryptic.servlet;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import org.json.JSONObject;
import org.json.XML;

/**
 * The Servlet class extends the main HttpServlet class and provides some
 * additional functionality for easily sending responses back to the client. For
 * consistency, all Servlets should extend this class.
 * 
 * @author Luke Hackett
 * @version 0.1
 */
public class Servlet extends HttpServlet {
	// Default Serial ID
	private static final long serialVersionUID = -8312653026552012858L;

	/**
	 * Sends the given data to the client based upon the response information.
	 * The method is able to send both XML and JSON responses, and is denoted by
	 * the JSON boolean variable.
	 * 
	 * @param response
	 *            HTTP response information
	 * @param payload
	 *            the data to send to the client
	 * @param json
	 *            should the response be in JSON (false gives XML)
	 */
	protected void sendResponse(HttpServletResponse response, String data,
			boolean json) {
		try {
			// Convert to JSON if needed
			if (json) {
				JSONObject jobj = XML.toJSONObject(data);
				// Indent to 3 levels
				data = jobj.toString(3);
			}

			// Flush the output to the network
			OutputStream out = response.getOutputStream();
			out.write(data.getBytes());
			out.flush();
		} catch (IOException e) {
			throw new HTTPException(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Sends the given error messages to the client based upon the response
	 * information. The method is able to send both XML and JSON responses, and
	 * if a non-javascript request is made, then the first error message will be
	 * returned along with the server's default error page.
	 * 
	 * @param response
	 *            HTTP response information
	 * @param payload
	 *            the data to send to the client
	 * @param errors
	 *            the array of error messages to be displayed
	 * @param errorCode
	 *            the HTML status code to be associated with the response
	 */
	protected void sendError(HttpServletRequest request,
			HttpServletResponse response, String[] errors, int errorCode) {

		try {
			// Only send raw data if original request was made via ajax
			if (isAjaxRequest(request)) {
				// Create the XML error message list
				String xml = "<errors>";
				for (String msg : errors) {
					xml += "<message>" + msg + "</message>";
				}
				xml += "</errors>";

				// Send the error
				response.setStatus(errorCode);
				boolean json = isJSONRequest(request);
				sendResponse(response, xml, json);
			} else {
				// Send a default server error using the first error message
				response.sendError(errorCode, errors[0]);
			}
		} catch (IOException | NullPointerException
				| ArrayIndexOutOfBoundsException e) {
			throw new HTTPException(
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * This method returns whether or not the x-requested-with header found
	 * within the request object states an AJAX request.
	 * 
	 * @param request
	 *            the HTTP request object
	 * @return true if x-requested-with header contains "xmlhttprequest"
	 */
	protected boolean isAjaxRequest(HttpServletRequest request) {
		String ajax = request.getHeader("x-requested-with");
		return ajax != null && ajax.toLowerCase().contains("xmlhttprequest");
	}

	/**
	 * This method returns whether or not the accept header found within the
	 * request object is expecting a JSON return.
	 * 
	 * @param request
	 *            the HTTP request object
	 * @return true if accept header contains "json"
	 */
	protected boolean isJSONRequest(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		return accept != null && accept.contains("json");
	}

	/**
	 * This method returns whether or not the accept header found within the
	 * request object is expecting an XML return.
	 * 
	 * @param request
	 *            the HTTP request object
	 * @return true if accept header contains "xml"
	 */
	protected boolean isXMLRequest(HttpServletRequest request) {
		String accept = request.getHeader("accept");
		return accept != null && accept.contains("xml");
	}

	/**
	 * This method returns whether or not the given String value is non-empty
	 * and it's not null.
	 * 
	 * @param value
	 *            the string value
	 * @return true if is not empty or null
	 */
	protected boolean isPresent(String value) {
		return (value == null || value.isEmpty()) ? false : true;
	}
}
