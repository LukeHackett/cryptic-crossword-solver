package uk.ac.hud.cryptic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.hud.cryptic.solver.Acrostic;
import uk.ac.hud.cryptic.solver.Anagram;
import uk.ac.hud.cryptic.solver.Hidden;
import uk.ac.hud.cryptic.solver.Homophone;
import uk.ac.hud.cryptic.solver.Pattern;
import uk.ac.hud.cryptic.solver.Solver;
import uk.ac.hud.cryptic.solver.Solver.Type;
import uk.ac.hud.cryptic.util.DB;

public class Manager {

	public static void main(String[] args) {
		// The clues to solve
		Collection<Clue> clues = DB.getTestClues(10, Type.HIDDEN,
				Type.ACROSTIC, Type.PATTERN);

		// Will record the success rate
		int successes = 0;

		for (Clue clue : clues) {

			// Create a thread pool to execute the solvers
			ExecutorService executor = Executors.newFixedThreadPool(5);
			// This will hold the returned data from the solvers
			Collection<Future<SolutionCollection>> solutions = new ArrayList<>();

			// TODO Something design-patterny should take the place of this
			Collection<Solver> solvers = new ArrayList<>();
			solvers.add(new Hidden(clue));
			solvers.add(new Acrostic(clue));
			solvers.add(new Anagram(clue));
			solvers.add(new Homophone(clue));
			solvers.add(new Pattern(clue));

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

			boolean found;
			// If found, mark as a success
			if (found = allSolutions.contains(clue.getActualSolution())) {
				successes++;
			}

			System.out.println("Results summary: "
					+ (found ? "[[PASS]]" : "[[FAIL]]"));
			System.out.println("\"" + clue.getClue() + "\" ("
					+ clue.getActualSolution() + ")");
			for (Solution s : allSolutions) {
				System.out.println(s);
			}
			System.out.println("--------------------------------");
		}
		System.out.println("The solution has been found " + successes
				+ " out of " + clues.size() + " times.");
	}

} // End of class manager
