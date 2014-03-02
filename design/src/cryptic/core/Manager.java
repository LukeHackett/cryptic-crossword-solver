package uk.ac.hud.cryptic.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.hud.cryptic.config.Settings;
import uk.ac.hud.cryptic.resource.Categoriser;
import uk.ac.hud.cryptic.solver.Acrostic;
import uk.ac.hud.cryptic.solver.Hidden;
import uk.ac.hud.cryptic.solver.Pattern;
import uk.ac.hud.cryptic.solver.Solver;
import uk.ac.hud.cryptic.util.DB;

/**
 * I'd expect the contents of this class will be temporary until a more
 * permanent solution is implemented. This class can currently take a
 * <code>Clue</code> object and distribute it to many of the <code>Solver</code>
 * implementations. The <code>SolutionCollection</code>s from each of these are
 * then combined into a single collection object.
 * 
 * @author Stuart Leader, Mohammad Rahman
 * @version 0.2
 */
public class Manager {

	/**
	 * This method could take some input from the Servlet / Controller in the
	 * form of a <code>Clue</code> object and return a
	 * <code>SolutionCollection</code> of the potential solutions that have been
	 * calculated.
	 * 
	 * @param clue
	 *            - the <code>Clue</code> object to get solutions for
	 * @return the calculated solutions to the given clue
	 */
	public SolutionCollection distributeAndSolveClue(Clue clue) {

		// This will hold the solvers to be run at runtime
		Collection<Solver> solvers = getSolversFromClasses(clue);

		// This will hold the returned data from the solvers
		Collection<Future<SolutionCollection>> solutions = initiateSolvers(solvers);

		// This will hold all solutions that have been returned
		SolutionCollection allSolutions = new SolutionCollection();

		// Now we need to 'unpack' the SolutionCollections
		for (Future<SolutionCollection> future : solutions) {
			try {
				allSolutions.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}

		// Adjust confidence scores based on cateogory matches
		Categoriser.getInstance().confidenceAdjust(clue, allSolutions);

		return allSolutions;
	}

	/**
	 * Instances of the solvers have been created and given the clue which is to
	 * be solved. Now they should run their algorithms in order to attempt to
	 * find the correct solution.
	 * 
	 * @param solvers
	 *            - the solvers which will search for the solution
	 * @return the Future objects which will hold the collections of solutions
	 *         once they have been found
	 */
	private Collection<Future<SolutionCollection>> initiateSolvers(
			Collection<Solver> solvers) {
		Collection<Future<SolutionCollection>> solutions = new ArrayList<>();

		// Create a thread pool to execute the solvers
		ExecutorService executor = Executors.newFixedThreadPool(solvers.size());

		// Fire off each solver to find that magic solution
		for (Solver s : solvers) {
			Future<SolutionCollection> future = executor.submit(s);
			solutions.add(future);
		}

		// All finished
		executor.shutdown();
		return solutions;
	}

	/**
	 * Load the solvers in from their corresponding Class files.
	 * 
	 * @param clue
	 *            - the clue which is to be solved
	 * @return a collection of Solver objects which have been loaded from their
	 *         Class file(s)
	 */
	private Collection<Solver> getSolversFromClasses(Clue clue) {
		Collection<Solver> solvers = new ArrayList<>();

		// Create a new instance of Settings file to reload solvers in
		// use
		// TODO Shouldn't do this as Settings is a Singleton
		// Settings settings = new Settings();
		Settings settings = Settings.getInstance();

		// Locate the properties file which contains available solvers
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				settings.getPropertyStream()))) {

			// An available solver
			String strLine;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {

				if (strLine.startsWith("#")) {
					// Ignore commented lines
					continue;
				}

				try {
					// System.out.println(strLine);
					// Load the class
					Class<?> cls = Class.forName(strLine);
					Constructor<?> c = cls.getDeclaredConstructor(Clue.class);
					// Instantiate class
					Solver solver = (Solver) c.newInstance(clue);
					// Add to solvers
					solvers.add(solver);

				} catch (ClassNotFoundException | NoSuchMethodException
						| SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return solvers;
	}

} // End of class Manager
