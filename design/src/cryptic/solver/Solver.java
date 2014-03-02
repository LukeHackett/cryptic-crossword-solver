package uk.ac.hud.cryptic.solver;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.resource.Categoriser;
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

	protected static final Dictionary DICTIONARY = Dictionary.getInstance();
	protected static final Thesaurus THESAURUS = Thesaurus.getInstance();

	// The clue to solve
	private Clue clue;

	/**
	 * Default constructor for solver class
	 */
	protected Solver() {

	}

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
	 * Required by the Callable interface. Similar to the framework used by
	 * Runnable, but this allows a variable (<code>SolutionCollection</code>) to
	 * be returned
	 */
	@Override
	public SolutionCollection call() throws Exception {
		return solve(clue);
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
	 * Get a String representation of this solver. This should also correspond
	 * with the database name for this type of clue. e.g. "hidden" or "&lit"
	 *
	 * @return the human-friendly name for this type of clue
	 */
	@Override
	public abstract String toString();

} // End of class Solver
