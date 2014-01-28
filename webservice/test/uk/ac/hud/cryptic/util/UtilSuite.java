package uk.ac.hud.cryptic.util;

/**
 * This JUnit Test Suite tests all test's in their individual classes for the
 * package uk.hud.ac.cryptic.util
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Mohammad Rahman, Stuart Leader
 * @version 0.1
 */

@RunWith(value = Suite.class)
@SuiteClasses(value = { DBTest.class, WordUtilsTest.class })
public class UtilSuite {

}
