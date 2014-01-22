package uk.ac.hud.cryptic.servlet;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.ac.hud.cryptic.servlet.SolverTest;

/**
 * This test suite will test all Servlets found within the servlet package.
 * 
 * @author Mohammad Rahman, Luke Hackett
 * @version 0.1
 */
@RunWith(value = Suite.class)
@SuiteClasses(value = { SolverTest.class })
public class ServletSuite {

}
