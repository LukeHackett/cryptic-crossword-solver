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
 * additional functionality for easily sending repsonses back to the client. For
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

}
