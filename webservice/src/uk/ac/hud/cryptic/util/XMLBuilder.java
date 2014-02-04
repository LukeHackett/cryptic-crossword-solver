package uk.ac.hud.cryptic.util;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * This class provides an interface in order to create a well formed XML String
 * that can be passed to other libraries or clients. The class handles any
 * number of solutions or errors and formats the output in the "application
 * defined" way.
 * 
 * @author Luke Hackett
 * @version 0.2
 */
public class XMLBuilder {
	// XML document
	private Document xml;

	/**
	 * Default constructor
	 * 
	 * @param clue
	 *            the clue that was solved
	 * @param pattern
	 *            the pattern that was used to solve the clue
	 */
	public XMLBuilder(String clue, String pattern) {
		// Create a new XML document and set the ROOT element
		Element root = new Element("solver");
		// Set the clue that was solved
		root.addContent(new Element("clue").setText(clue));
		// Set the pattern that was used
		root.addContent(new Element("pattern").setText(pattern));
		// Formulate a new document
		xml = new Document(root);
	}

	/**
	 * Secondary constructor
	 * 
	 * @param clue
	 *            the clue that was solved
	 * @param pattern
	 *            the pattern that was used to solve the clue
	 * @param timeTaken
	 *            the total time in milliseconds to solve the clue
	 */
	public XMLBuilder(String clue, String pattern, double timeTaken) {
		// Create a new XML document and set the ROOT element
		Element root = new Element("solver");
		// Set the clue that was solved
		root.addContent(new Element("clue").setText(clue));
		// Set the pattern that was used
		root.addContent(new Element("pattern").setText(pattern));
		// Set the time take to solve
		String time = new DecimalFormat("#.##").format(timeTaken / 1000);
		// String time = df.format(timeTaken/1000);
		root.addContent(new Element("duration").setText(time));
		// Formulate a new document
		xml = new Document(root);
	}

	/**
	 * This method will add the a new key element with the give value. The
	 * element will be found directly under the root element
	 * 
	 * @param key
	 *            the name of the element to be added
	 * @param value
	 *            the value of the key to be associated with the element
	 */
	public void addKeyValue(String key, String value) {
		xml.getRootElement().addContent(new Element(key).setText(value));
	}

	/**
	 * This method will add a new error message to the list of errors.
	 * 
	 * @param error
	 *            the error that has occurred
	 */
	public void addError(String error) {
		Element message = new Element("message").setText(error);
		xml.getRootElement().getChild("errors").addContent(message);
	}

	/**
	 * This method will add an array of messages to the list of errors within
	 * the XML String.
	 * 
	 * @param errors
	 *            A list of errors that occurred
	 */
	public void addErrors(String[] errors) {
		for (String error : errors) {
			addError(error);
		}
	}

	/**
	 * This method will add a new solution element that comprises of the solver
	 * that was used to solve the clue, the value of the solution (i.e. the
	 * computed answer) and the confidence rating of the solution.
	 * 
	 * @param solver
	 *            the solver that was used to solve the clue
	 * @param solution
	 *            the computed solution to the clue
	 * @param confidence
	 *            the computed confidence rating of the solution
	 * @param trace
	 *            A list of steps that were performed in obtaining the solution
	 */
	public void addSolution(String solver, String solution, String confidence,
			List<String> trace) {
		// Build a new Solution Element
		Element solElement = new Element("solution");
		// Add the solver that was used to obtain the solution
		solElement.addContent(new Element("solver").setText(solver));
		// Add the solution value
		solElement.addContent(new Element("value").setText(solution));
		// Add the confidence rating
		solElement.addContent(new Element("confidence").setText(confidence));
		// Add each of the traces
		for (String t : trace) {
			solElement.addContent(new Element("trace").setText(t));
		}
		// Add the Solution Element to the root element
		xml.getRootElement().addContent(solElement);
	}

	/**
	 * This method will convert the XML object within this class to a well
	 * formatted XML String.
	 * 
	 * @return A well formatted XML String
	 */
	@Override
	public String toString() {
		// Stringified XML output
		String output = "";

		try {
			// new XMLOutputter().output(doc, System.out);
			XMLOutputter xmlOutput = new XMLOutputter();
			// Use a compact format to save transmission bytes
			xmlOutput.setFormat(Format.getCompactFormat());
			// Write the output into a String
			StringWriter writer = new StringWriter();
			xmlOutput.output(xml, writer);
			output = writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}
}
