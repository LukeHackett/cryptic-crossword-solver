package uk.ac.hud.cryptic.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Manager;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.util.XMLErrorBuilder;
import uk.ac.hud.cryptic.util.XMLResultBuilder;

/**
 * This class provides a servlet to provide an interface to the application.
 * Clues are solved by posting the clue and the solution pattern to this
 * resource.
 * 
 * @author Luke Hackett, Stuart Leader
 * @version 0.2
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
	 * This method will compare a string length against a given pattern.
	 * 
	 * @param length
	 *            the expected length of the pattern format
	 * @param pattern
	 *            the pattern format
	 * @return true if length of the pattern is equal to the pattern format
	 *         length
	 */
	private boolean hasSameLength(String length, String pattern) {
		// Compare current word length against actual length
		try {
			// Obtain integer value of solution
			int solLen = Integer.parseInt(length);

			// Compare solution format number against pattern format
			// e.g. 3 => ???
			if (solLen != pattern.length()) {
				return false;
			}
		} catch (NumberFormatException e) {
			// An error has occurred therefore error
			return false;
		}
		return true;
	}

	/**
	 * This method will validate the given clue
	 * 
	 * @param clue
	 *            the clue to be validated
	 * @return true if clue is valid
	 */
	private boolean isClueValid(String clue) {
		// Ensure the clue is present
		return !(clue == null || clue.isEmpty());
	}

	/**
	 * This method will validate the given solution pattern, based upon a
	 * well-formed regular expression.
	 * 
	 * @param pattern
	 *            the solution pattern to be validated
	 * @return true if the solution pattern format is valid
	 */
	private boolean isPatternValid(String pattern) {
		// Pattern string regular expression
		final String regex = "[0-9A-Za-z?]+((,|-)[0-9A-Za-z?]+)*";
		boolean match = Pattern.matches(regex, pattern);

		// Pattern String must be present and of a valid format
		return isPresent(pattern) && match;
	}

	/**
	 * This method will validate the given solution, based upon a well-formed
	 * regular expression.
	 * 
	 * @param solution
	 *            the solution to be validated
	 * @return true if the solution is valid
	 */
	private boolean isSolutionValid(String solution) {
		// Solution string regular expression
		final String regex = "[0-9]+((,|-)[0-9]+)*";
		boolean match = Pattern.matches(regex, solution);

		// Solution String must be present and of a valid format
		return isPresent(solution) && match;
	}

	/**
	 * This method will validate the given solution format against the given
	 * pattern string.
	 * 
	 * @param solutionString
	 * @param patternString
	 * @return true if the solution format matches the solution pattern format
	 */
	private boolean isValidMatch(String solutionString, String patternString) {
		// String constants
		final String wordSeperator = ",";
		final String subwordSeperator = "-";

		// Split the format and pattern based upon the word separator
		String[] solutions = solutionString.split(wordSeperator);
		String[] patterns = patternString.split(wordSeperator);

		// Ensure the solution and pattern strings align with each other
		if (solutions.length != patterns.length) {
			return false;
		}

		// Analyse each of the expected words
		for (int i = 0; i < solutions.length; i++) {
			// Get the solution format and pattern format at the same index
			String solution = solutions[i];
			String pattern = patterns[i];

			// Check to see if a sub-spit is required
			if (solution.contains(subwordSeperator)) {
				// Split word into the sub-words (i.e. hyphenated answers)
				String[] innerSolutions = solution.split(subwordSeperator);
				String[] innerPatterns = pattern.split(subwordSeperator);

				// Compare all sub-word lengths against actual lengths
				for (int j = 0; j < innerSolutions.length; j++) {
					// Break the look if validation has failed
					if (!hasSameLength(innerSolutions[j], innerPatterns[j])) {
						return false;
					}
				}
			} else {
				// false if validation has failed
				if (!hasSameLength(solution, pattern)) {
					return false;
				}
			}
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
		// Notify Manager this is a servlet
		manager.setServletContext(getServletConfig().getServletContext());
		
		SolutionCollection solutions = manager.distributeAndSolveClue(clue);
		// Solutions aren't sorted until this is requested
		Set<Solution> sortedSolutions = solutions.sortSolutions();

		// Needed to correct the output of the solution(s)
		final SolutionPattern pattern = clue.getPattern();

		// Create a new XML Builder object
		XMLResultBuilder xmlBuilder = new XMLResultBuilder(clueString,
				patternString);

		// Add each of the solutions to the XML document
		for (Solution s : sortedSolutions) {
			String solver = s.getSolverType();
			String solution = pattern.recomposeSolution(s.getSolution());
			String confidence = Solution.CONF_FORMATTER.format(s
					.getConfidence());
			List<String> trace = s.getSolutionTrace();

			xmlBuilder.addSolution(solver, solution, confidence, trace);
		}
		
		return xmlBuilder.toString();
	}

	/**
	 * This method will validate this given clue, solution and pattern against
	 * various aspects to ensure that the inputs are valid. The method will
	 * return a list of error messages, should one or more of the inputs fail
	 * the validation.
	 * 
	 * @param clue
	 *            the clue to be validated
	 * @param solution
	 *            the solution format to be validated
	 * @param pattern
	 *            the solution pattern format to be validated
	 * @return A String array of errors if errors are detected
	 */
	private String[] validateInputs(String clue, String solution, String pattern) {
		// List of error messages to be displayed to the client
		Collection<String> messages = new ArrayList<>();

		// Ensure the clue is valid.
		if (!isClueValid(clue)) {
			messages.add("Please enter a clue to solve.");
		}

		// Ensure the solution format is valid
		if (!isSolutionValid(solution)) {
			messages.add("Please enter a valid solution format.");
		}

		// Ensure the pattern format is valid
		if (!isPatternValid(pattern)) {
			messages.add("Please enter a valid pattern format.");
		}

		// Ensure that the solution format matches the pattern format
		if (!isValidMatch(solution, pattern)) {
			messages.add("Please ensure the solution format matches the pattern format.");
		}

		return messages.toArray(new String[messages.size()]);
	}

	/**
	 * This method will allow for three parameters to be sent via a GET request
	 * -- clue, length and pattern. Clue refers to the original clue, and the
	 * length reference to the length of the solution, whilst pattern refers to
	 * the format of the answer. This method will automatically return the user
	 * back to the main index page.
	 * 
	 * @param request
	 *            HTTP request information
	 * @param response
	 *            HTTP response information
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Obtain the input requests
		String clue = request.getParameter("clue");
		String length = request.getParameter("length");
		String pattern = request.getParameter("pattern");

		// Check for a new request
		if (clue == null && length == null && pattern == null) {
			request.getRequestDispatcher("solver.jsp")
					.forward(request, response);
			return;
		}

		// Do the GET request as a POST request
		doPost(request, response);
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
		settings.setServletContext(getServletConfig().getServletContext());

		// Obtain the input requests
		String clue = request.getParameter("clue");
		String solution = request.getParameter("length");
		String pattern = request.getParameter("pattern");

		// Validate Inputs
		String[] errors = validateInputs(clue, solution, pattern);

		// Check to see if a page needs to be rendered server-side
		if (isAjaxRequest(request)) {
			// Determine if the request is expecting a JSON return
			boolean json = isJSONRequest(request);

			// Send errors and cancel the current request if required
			if (errors.length > 0) {
				// Build a new XML document
				XMLErrorBuilder builder = new XMLErrorBuilder();
				builder.addErrors(errors);

				sendError(response, builder.toString(), json,
						HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			// Solve the clue
			String data = solveClue(clue, pattern);

			// Send the response
			sendResponse(response, data, json);

		} else {
			// Server-side page rendering is required
			request.setAttribute("clue", clue);
			request.setAttribute("length", solution);
			request.setAttribute("pattern", pattern);

			// Check to see if there are any errors
			if (errors.length > 0) {
				// Validation has failed -> inform end user
				request.setAttribute("errors", errors);
			} else {
				// Validation has passed -> present results
				String data = solveClue(clue, pattern);
				request.setAttribute("results", data);
			}

			// Forward request and response onto the view
			request.getRequestDispatcher("solver.jsp")
					.forward(request, response);
		}
	}
}
