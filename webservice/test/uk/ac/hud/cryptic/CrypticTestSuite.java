package uk.ac.hud.cryptic;

/**
 * This class provides access to all the test for the Cryptic Crossword Solver.
 * The class Acts as the Top level Suite which refers to each suite for each package
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.ac.hud.cryptic.config.ConfigSuite;
import uk.ac.hud.cryptic.core.CoreSuite;
import uk.ac.hud.cryptic.resource.ResourceSuite;
import uk.ac.hud.cryptic.servlet.ServletSuite;
import uk.ac.hud.cryptic.solver.SolverSuite;
import uk.ac.hud.cryptic.util.UtilSuite;



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

