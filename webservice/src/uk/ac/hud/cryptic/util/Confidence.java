package uk.ac.hud.cryptic.util;

/**
 * This class will manage the functions associated with confidence scores. As
 * there will be many common elements between the solvers that determines the
 * final confidence score, these common elements can be placed here.
 * 
 * @author Stuart Leader
 * @version 0.2
 */
public class Confidence {

	// Each generated solution will start with this confidence score
	public static final double INITIAL = 42d;
	// Multiplier if a solution is a synonym of the clue's definition word
	public static final double SYNONYM_MULTIPLIER = 1.5d;
	// Multiplier if a solution has a synonym of the clue's definition word
	public static final double REVERSE_SYNONYM_MULTIPLIER = 1.1d;
	// Multiplier for Hidden words words that aren't really hidden
	public static final double NOT_HIDDEN_MULTIPLIER = 0.85d;
	// Multiplier for solutions which are solved by an algorithm, for which
	// there is an indicator word(s) in the clue
	public static final double CATEGORY_MULTIPLIER = 1.25d;
	// Multiplier for homophone matches
	public static final double HOMOPHONE_MULTIPLIER = 1.1d;

	/**
	 * @param value
	 *            - the original confidence value
	 * @param factor
	 *            - the multiplication factor
	 * @return the resulting confidence score
	 */
	public static double multiply(double value, double factor) {
		double confidence = value * factor;
		// Ensure the new value resides in the permitted range
		return verify(confidence);
	}

	/**
	 * Ensure the confidence score resides with in the range 0 - 100 inclusive.
	 * 
	 * @param confidence
	 *            - the value to validate
	 * @return the confidence value in the range 0 - 100
	 */
	private static double verify(double confidence) {
		if (confidence < 0d) {
			confidence = 0d;
		} else if (confidence > 100d) {
			confidence = 100d;
		}
		return confidence;
	}

}
