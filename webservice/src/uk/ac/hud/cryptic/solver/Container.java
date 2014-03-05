package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Thesaurus;
import uk.ac.hud.cryptic.util.WordUtils;

/**
 * Container solver algorithm
 * 
 * @author Mohammad Rahman
 * @version 0.1
 */
public class Container extends Solver {

	// A readable (and DB-valid) name for the solver
	private static final String NAME = "container";

	/**
	 * Default constructor for solver class
	 */
	public Container() {
		super();
	}

	/**
	 * Default constructor for solver class
	 * 
	 * @param clue
	 *            - the clue to be solved
	 */
	public Container(Clue clue) {
		super(clue);
	}

	@Override
	public SolutionCollection solve(Clue c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}


} // End of class Container
