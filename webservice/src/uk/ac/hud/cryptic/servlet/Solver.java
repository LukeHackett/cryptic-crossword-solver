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
		String solutionString = request.getParameter("length");
		String patternString = request.getParameter("pattern");

		// Ensure that the inputs have been sent
		if (!valid_inputs(clueString, solutionString, patternString)) {
			// TODO log exceptions
			throw new HTTPException(HttpServletResponse.SC_BAD_REQUEST);
		}

		// Solve the clue
		String data = solveClue(clueString, patternString);

		// Send the response
		boolean json = isJSONRequest(request);
		sendResponse(response, data, json);
	}

	/**
	 * This method ensures that the given inputs are present and valid.
	 * 
	 * @param clueString
	 *            The clue to be solved
	 * @param solutionString
	 *            The required length of the solution
	 * @param patternString
	 *            The solution pattern
	 * @return whether or not the inputs are valid
	 */
	private boolean valid_inputs(String clueString, String solutionString,
			String patternString) {

		// Clue validator
		if (clueString == null || clueString.isEmpty()) {
			return false;
		}

		// Solution length validator
		if (solutionString == null || solutionString.isEmpty()) {
			// TODO: Improve Validation
			return false;
		}

		// Solution pattern validator
		if (patternString == null || patternString.isEmpty()) {
			// TODO: Improve Validation
			return false;
		}

		return true;
	}

	/**
	 * This method provides a simple entry point to solving a given clue
	 * utilising the supplied pattern. A well formated XML String is returned
	 * with any given results.
	 * 
	 * @param clueString
	 *            The clue to be solved
	 * @param patternString
	 *            The solution pattern
	 * @return XML String of results
	 */
	private String solveClue(String clueString, String patternString) {
		// Encapsulate the clueString in an object and solve it
		Clue clue = new Clue(clueString, patternString);
		Manager manager = new Manager();
		SolutionCollection solutions = manager.distributeAndSolveClue(clue);

		// Needed to correct the output of the solution(s)
		final SolutionPattern pattern = clue.getPattern();

		// Format the data
		// TODO: Use of library
		String data = "";
		data += "<clue>" + clueString + "</clue>";
		data += "<pattern>" + patternString + "</pattern>";
		for (Solution s : solutions) {
			String solution = pattern.recomposeSolution(s.getSolution());
			data += "<solution>";
			data += "<value>" + solution + "</value>";
			data += "<confidence>" + s.getConfidence() + "</confidence>";
			data += "</solution>";
		}

		return data;
	}
}
