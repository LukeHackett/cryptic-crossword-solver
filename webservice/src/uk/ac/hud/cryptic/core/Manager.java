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
import uk.ac.hud.cryptic.resource.Thesaurus;
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

	public static final String PREFIX = "stream2file";
    public static final String SUFFIX = ".tmp";
	
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

		// This will hold the returned data from the solvers
		Collection<Future<SolutionCollection>> solutions = new ArrayList<>();

		// This will hold the solvers to be ran at runtime
		Collection<Solver> solvers = new ArrayList<>();


		//TODO Need to find a better way to locate the properties file
		//InputStream p = Settings.getInstance().getPropertyPath(); << Does not reload


		//Instead create a new instance of Settings file to reload solvers in use
		Settings settings = new Settings();

		try {		

			//Locate the properties file which contains available solvers
			BufferedReader br = new BufferedReader(
					new InputStreamReader(settings.getPropertyPath()));

			// An available solver
			String strLine;

			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {

				// A Solver
				Class<?> cls;
				try {
					System.out.println(strLine);
					//Load the class
					cls = Class.forName(strLine);
					Constructor<?> c = cls.getDeclaredConstructor(Clue.class);
					//Instantiate class
					Solver solver = (Solver) c.newInstance(clue); // we instantiate it, no parameters
					// Add to solvers
					solvers.add(solver);

				} catch (ClassNotFoundException | NoSuchMethodException 
						| SecurityException | InstantiationException 
						| IllegalAccessException | IllegalArgumentException 
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}

			//Close the input stream
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Create a thread pool to execute the solvers
		ExecutorService executor = Executors.newFixedThreadPool(solvers.size());

		// Fire off each solver to find that magic solution
		for (Solver s : solvers) {
			Future<SolutionCollection> future = executor.submit(s);
			solutions.add(future);
		}

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

		// All finished
		executor.shutdown();

		// Adjust confidence scores based on synonym matches
		Thesaurus.getInstance().confidenceAdjust(clue, allSolutions);
		// Adjust confidence scores based on cateogory matches
		Categoriser.getInstance().confidenceAdjust(clue, allSolutions);

		return allSolutions;
	}

	/**
	 * A entry point to the class in order to test, in particular, the
	 * concurrent nature of the solver algorithms. This little test will obtain
	 * a number of test clues from the database and pass these to several solver
	 * algorithms simultaneously for solving.
	 */
	public static void main(String[] args) {
		// The clues to solve
		Collection<Clue> clues = DB.getTestClues(10, true,
				new Hidden().toString(), new Acrostic().toString(),
				new Pattern().toString());

		// Will record the success rate
		int successes = 0;

		for (final Clue clue : clues) {

			// Instantiate the manager and fire off the clue to the solvers
			Manager m = new Manager();			
			SolutionCollection allSolutions = m.distributeAndSolveClue(clue);
			

			boolean found;
			// If found, mark as a success
			if (found = allSolutions.contains(clue.getActualSolution())) {
				successes++;
			}

			// Print block for results
			System.out.println("Results summary: "
					+ (found ? "[[PASS]]" : "[[FAIL]]"));
			System.out.println("\"" + clue.getClue() + "\" ("
					+ clue.getActualSolution() + "), Type: " + clue.getType());
			for (Solution s : allSolutions) {
				System.out.println(s);
			}
			System.out.println("--------------------------------");
		}
		System.out.println("The solution has been found " + successes
				+ " out of " + clues.size() + " times.");
	}


} // End of class manager
