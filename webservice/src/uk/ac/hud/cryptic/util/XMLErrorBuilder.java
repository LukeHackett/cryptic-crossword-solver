package uk.ac.hud.cryptic.util;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * This class provides an interface in order to create a well formed XML error
 * strings that can be passed directly back to clients or to other libraries.
 * The class handles any number of errors, and formats the output in the
 * "application defined" way.
 * 
 * @author Luke Hackett
 * @version 0.2
 */
public class XMLErrorBuilder {
	// XML document
	private Document xml;

	/**
	 * Default constructor
	 */
	public XMLErrorBuilder() {
		// Create a new XML document and set the ROOT element
		Element root = new Element("solver");
		// Add anm errors child
		root.addContent(new Element("errors"));
		// Formulate a new document
		xml = new Document(root);
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
