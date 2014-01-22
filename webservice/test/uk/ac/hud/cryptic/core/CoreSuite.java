package uk.ac.hud.cryptic.core;
/**
 * This JUnit Test Suite tests all test's in their individual classes for
 * the package uk.hud.ac.cryptic.core
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import uk.ac.hud.cryptic.core.ClueTest;

/**
 * @author Mohammad Rahman
 * @version 0.1
 */

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		ClueTest.class
		//ManagerTest.class,
		//SolutionTest.class,
		//SolutionCollectionTest.class,
		//SolutionPatternTest.class
		})

public class CoreSuite {

}

