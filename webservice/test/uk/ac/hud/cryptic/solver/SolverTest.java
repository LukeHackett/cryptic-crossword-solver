/**
 * 
 */
package uk.ac.hud.cryptic.solver;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import uk.ac.hud.cryptic.util.DB;

/**
 * @author Mohammad Rahman, Stuart Leader
 */
public abstract class SolverTest {

	/**
	 * Test method for all implementing classes of Solver
	 * 
	 * @param type
	 *            - Relating to the Solver.Type enum
	 * @param solver
	 *            - The solver Class to test
	 */
	public void testSolve(String type, final Class<? extends Solver> solver,
			boolean unknownCharacters, int... testCount) {
		// Obtain some test data from the database
		Collection<Clue> clues = DB.getTestClues(type, unknownCharacters,
				testCount);

		// Solve 1 clue on each thread available
		int processors = Runtime.getRuntime().availableProcessors();

		// Create a thread pool to execute the solvers
		ExecutorService executor = Executors.newFixedThreadPool(processors);
		// Will hold final results
		Collection<Future<Boolean>> results = new ArrayList<>();

		// Test again both known and unknown solution patterns. In reality this
		// will normally be completely unknown, otherwise just partially known.
		// Fire off each solver to find that magic solution
		for (final Clue clue : clues) {
			results.add(executor.submit(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					boolean found = false;
					try {
						// Use reflection to instantiate Solver object
						Constructor<?> con = solver.getConstructor(Clue.class);
						Solver s = (Solver) con
								.newInstance(new Object[] { clue });

						// Call the implementation's solve method
						SolutionCollection sc = s.solve(clue);

						// See if the solution has been found
						found = sc.contains(clue.getActualSolution());

					} catch (Exception e) {
						// Something has REALLY gone wrong.
						fail("Test aborted due to unrecoverable error: "
								+ e.getMessage());
					}
					return found;
				}
			}));
		}

		// All finished
		executor.shutdown();

		// Now we need to 'unpack' the results
		for (Future<Boolean> future : results) {
			try {
				// Was it successful in finding the solution?
				assertTrue(future.get());
			} catch (InterruptedException | ExecutionException e) {
				// Something has REALLY gone wrong.
				fail("Test aborted due to unrecoverable error: "
						+ e.getMessage());
			}
		}
	}

}
