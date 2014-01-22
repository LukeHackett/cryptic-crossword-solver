package uk.ac.hud.cryptic;
/**
 * This JUnit Test Suite tests all test's in their individual classes for
 * the package uk.hud.ac.cryptic.servlet
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.ac.hud.cryptic.servlet.SolverTest;

/**
 * @author Mohammad Rahman
 * @version 0.1
 */

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		SolverTest.class
		//ServletTest.class
		})

public class ServletSuite {

}

