package uk.ac.hud.cryptic.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Manager;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.util.SolutionPattern;

/**
 * This class provides a servlet to provide an interface to the application.
 * Clues are solved by posting the clue and the solution pattern to this
 * resource.
 * 
 * @author Luke Hackett, Stuart Leader
 * @version 0.1
 */
@WebServlet("/solver")
public class Solver extends Servlet {
	// Generated Serial ID
	private static final long serialVersionUID = -7066687691201583586L;

	/**
	 * Default Constructor
	 */
	public Solver() {
		super();
	}

	/**
	 * This method will allow for two parameters to be posted -- clue and
	 * length. Clue refers to the original clue, and the solutionLength refers
	 * to the length of the solution. Responses are returned as either XML
	 * (default) or JSON depending upon the client's request.
	 * 
	 * @param request
	 *            HTTP request information
	 * @param response
	 *            HTTP response information
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// ServletContext is required to locate resource (e.g. dictionary)
		// TODO This could perhaps be implemented more elegantly?
		Settings settings = Settings.getInstance();
		// When settings has this "context", it knows to load resources from
		// somewhere else
		settings.setServletContext(this.getServletConfig().getServletContext());

		// Obtain the input requests
		String clueString = request.getParameter("clue");
		String patternString = request.getParameter("pattern");

		// Ensure that the inputs have been sent
		if (clueString == null || patternString == null) {
			// TODO log exceptions
			throw new HTTPException(HttpServletResponse.SC_BAD_REQUEST);
		}

		// Encapsulate the clueString in an object and solve it!
		Clue clue = new Clue(clueString, patternString);
		Manager manager = new Manager();
		SolutionCollection solutions = manager.distributeAndSolveClue(clue);

		// Needed to correct the output of the solution(s)
		final SolutionPattern pattern = clue.getPattern();

		// Format some test data
		String data = "";
		data += "<response>";
		data += "<clue_recieved>" + clueString + "</clue_recieved>";
		data += "<pattern_recieved>" + patternString + "</pattern_recieved>";
		for (Solution s : solutions) {
			String solution = pattern.recomposeSolution(s.getSolution());
			data += "<solution>";
			data += "<text>" + solution + "</text>";
			data += "<confidence>" + s.getConfidence() + "</confidence>";
			data += "</solution>";
		}
		data += "</response>";

		// Send the response
		boolean json = isJSONRequest(request);
		sendResponse(response, data, json);
	}
}
