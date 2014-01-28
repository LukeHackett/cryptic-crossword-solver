package uk.ac.hud.cryptic.solver;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.hud.cryptic.solver.Solver.Type;

public class PatternTest extends SolverTest {

	// Corresponding Type for this test (for database)
	private static Type type;
	// The Class to test (should make it easier to streamline these solver
			// tests)
	private static Class<? extends Solver> solver;
	// Max number of clues to solve
	private static int testCount;
	// If true, "VISIBLE" instead of "???????"
	private static boolean unknownCharacters;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		type = Type.PATTERN;
		solver = Pattern.class;
		testCount = 20;
		unknownCharacters = true;
	}

	@Test
	public void test() {
		// Test the solver against known clues from the DB
		testSolve(type, solver, unknownCharacters, testCount);
	}

}
