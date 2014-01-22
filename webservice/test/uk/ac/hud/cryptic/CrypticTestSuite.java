package uk.ac.hud.cryptic;

/**
 * This class provides access to all the test for the Cryptic Crossword Solver.
 * The class Acts as the Top level Suite which refers to each suite for each package
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * @author Mohammad Rahman
 * @version 0.1
 */
@RunWith(Suite.class)
@SuiteClasses({ CoreSuite.class, 
	ConfigSuite.class, 
	ResourceSuite.class, 
	ServletSuite.class, 
	SolverSuite.class, 
	UtilSuite.class })

public class CrypticTestSuite {

}

