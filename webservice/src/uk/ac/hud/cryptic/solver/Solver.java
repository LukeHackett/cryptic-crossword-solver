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
	protected static void testSolver(final Class<? extends Solver> solver) {
		try {
			Solver s = solver.newInstance();

			// Obtain some test data from the database
			Collection<Clue> clues = DB.getTestClues(s.toString(), true, 99999);

			// Solve 1 clue on each thread available
			int processors = Runtime.getRuntime().availableProcessors();
			// Create a thread pool to execute the solvers
			ExecutorService executor = Executors.newFixedThreadPool(processors);

			// Will hold final results
			Collection<Future<Boolean>> results = new ArrayList<>();

			// Timer of entire process
			long timeStart = System.currentTimeMillis();

			// Fire off each solver to find that magic solution
			for (final Clue clue : clues) {
				results.add(executor.submit(new Callable<Boolean>() {
					@Override
					public Boolean call() {
						boolean found = false;
						try {
							// Solve time measurement
							long timeStart = System.currentTimeMillis();
							// Invoke the constructor of the passed class' name
							Constructor<?> con = solver
									.getConstructor(Clue.class);
							Solver s = (Solver) con
									.newInstance(new Object[] { clue });

							// Call the implementation's solve method
							SolutionCollection sc = s.solve(clue);
							THESAURUS.confidenceAdjust(clue, sc);
							Categoriser.getInstance()
									.confidenceAdjust(clue, sc);

							// See if the solution has been found
							found = sc.contains(clue.getActualSolution());

							// Output results all at once
							String output = "";
							// Print results to console
							output = found ? "[Found] " : "[Not Found] ";
							output += clue.getClue() + ": ";
							if (found) {
								output += clue
										.getPattern()
										.recomposeSolution(
												sc.getSolution(
														clue.getActualSolution())
														.getSolution());
							} else {
								output += clue.getActualSolution();
							}
							output += " ["
									+ (System.currentTimeMillis() - timeStart)
									+ "ms]";
							System.out.println(output);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return found;
					}
				}));
			}

			// All finished
			executor.shutdown();

			// Now we need to 'unpack' the results
			int count = 0;
			for (Future<Boolean> future : results) {
				try {
					// Was it successful in finding the solution?
					boolean result = future.get();
					if (result) {
						count++;
					}
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			// Some time statistics
			long timeTotal = System.currentTimeMillis() - timeStart;
			long timeAvg = timeTotal / clues.size();
			boolean multiThreaded = processors > 1;

			// Summary of results
			System.out.println(count + " out of " + clues.size()
					+ " successfully found.");
			System.out.println("Total time is " + timeTotal
					+ "ms and average time is " + timeAvg + "ms, solving over "
					+ processors + (multiThreaded ? " separate" : "")
					+ " thread" + (multiThreaded ? "s" : "") + ".");
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | SecurityException e1) {
			e1.printStackTrace();
		}
	}
} // End of class Solver
