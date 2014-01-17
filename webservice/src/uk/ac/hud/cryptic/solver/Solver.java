package uk.ac.hud.cryptic.solver;

import java.util.Collection;
import java.util.concurrent.Callable;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.resource.Dictionary;
import uk.ac.hud.cryptic.resource.Thesaurus;
import uk.ac.hud.cryptic.util.DB;

/**
 * This class provides a basis for all Solver algorithms to be built upon. All
 * algorithms will be required to run within their own dedicated thread. This is
 * to ensure that performance of the system as an entity remains high.
 * 
 * @author Luke Hackett, Stuart Leader
 * @version 0.1
 */
public abstract class Solver implements Callable<SolutionCollection> {

	/**
	 * An Enum representing the distinct cryptic crossword categories. Each of
	 * these contains the corresponding MySQL database name for the 'type'
	 * field, which is useful when, for example, retrieving test data for a
	 * particular clue type.
	 */
	public enum Type {
		ACROSTIC("acrostic"), ANAGRAM("anagram"), AND_LITERALLY_SO("&lit"), CHARADE(
				"charade"), CONTAINER("container"), DELETION("deletion"), DOUBLE_DEFINITION(
				"double definition"), EXCHANGE("exchange"), HIDDEN("hidden"), HOMOPHONE(
				"homophone"), PALINDROME("palindrome"), PATTERN("pattern"), PURELY_CRYPTIC(
				"purely cryptic"), REVERSAL("reversal"), SHIFTING("shifting"), SPOONERISM(
				"spoonerism"), SUBSTITUTION("substitution"), UNCATEGORISED(
				"uncategorised");

		private final String dbName;

		/**
		 * Constructor for the Enum takes the corresponding clue type's database
		 * name
		 * 
		 * @param name
		 *            - the database name for this clue type
		 */
		private Type(String name) {
			dbName = name;
		}

		/**
		 * Get the corresponding database name for this clue type
		 * 
		 * @return
		 */
		public String getDBName() {
			return dbName;
		}
	}

	protected static final Dictionary DICTIONARY = Dictionary.getInstance();
	protected static final Thesaurus THESAURUS = Thesaurus.getInstance();

	// The clue to solve
	private Clue clue;

	/**
	 * Constructor for class Solver. Takes the clue which is to be solved.
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	protected Solver(Clue clue) {
		this.clue = clue;
	}

	/**
	 * No-arg constructor
	 */
	protected Solver() {

	}

	/**
	 * The solve method will provide the core functionality of the cryptic
	 * crossword solver application. It will take a clue object and return a
	 * collection of possible solutions, if any.
	 * 
	 * @param c
	 *            - the clue to solve
	 * @return a <code>Collection</code> of potential solutions
	 */
	public abstract SolutionCollection solve(Clue c);

	/**
	 * Required by the Callable interface. Similar to the framework used by
	 * Runnable, but this allows a variable (<code>SolutionCollection</code>) to
	 * be returned
	 */
	@Override
	public SolutionCollection call() throws Exception {
		return solve(clue);
	}

	/**
	 * Test a solver implementation using test clues from the database. Results
	 * of the simple tests (whether the solution has been for or not amongst the
	 * generated solutions) are printed to <code>System.out</code>.
	 * 
	 * @param s
	 *            - the solver to test
	 * @param t
	 *            - the type of test clues to retreive from the database
	 */
	protected void testSolver(Solver s, Type t) {
		// Obtain some test data from the database
		Collection<Clue> clues = DB.getTestClues(t);

		// Counter of clues where solution was found
		int correctCount = 0;

		for (Clue clue : clues) {
			// Call the implementation's solve method
			SolutionCollection sc = s.solve(clue);

			// See if the solution has been found
			boolean found = sc.contains(clue.getActualSolution());
			if (found) {
				correctCount++;
			}

			// Print results to console
			System.out.print((found) ? "[Found] " : "[Not Found] ");
			System.out.print(clue.getClue() + ": (");
			System.out.println(clue.getActualSolution() + ")");
		}
		// Summary of results
		System.out.println(correctCount + " out of " + clues.size()
				+ " successfully found.");
	}
	
} // End of class Solver
