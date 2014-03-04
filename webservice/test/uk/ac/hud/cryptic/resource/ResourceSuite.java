package uk.ac.hud.cryptic.resource;
/**
 * This JUnit Test Suite tests all test's in their individual classes for
 * the package uk.hud.ac.cryptic.resource
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author Mohammad Rahman
 * @version 0.2
 */

@RunWith(value = Suite.class)
@SuiteClasses(value = { 
		HomophoneDictionaryTest.class,
		DictionaryTest.class,
		ThesaurusTest.class
		})

public class ResourceSuite {

}

