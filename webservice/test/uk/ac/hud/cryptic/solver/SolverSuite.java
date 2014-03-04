package uk.ac.hud.cryptic.solver;
/**
 * This JUnit Test Suite tests all test's in their individual classes for
 * the package uk.hud.ac.cryptic.solver
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Mohammad Rahman
 * @version 0.1
 */

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		AcrosticTest.class,
		AnagramTest.class,
		HiddenTest.class,
		PatternTest.class,
		DoubleDefinitionTest.class,
		HomophoneTest.class,
		SpoonerismTest.class,
		PalindromeTest.class
		})

public class SolverSuite {

}

