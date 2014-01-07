package uk.ac.hud.cryptic.solver;

import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.resource.Dictionary;
import uk.ac.hud.cryptic.resource.Thesaurus;

/**
 * This class provides a basis for all Solver algorithms to be built upon. All
 * algorithms will be required to run within their own dedicated thread. This is
 * to ensure that performance of the system as an entity remains high.
 * 
 * @author Luke Hackett, Stuart Leader
 * @version 0.1
 */
public abstract class Solver implements Runnable {

	protected static final Dictionary DICTIONARY = Dictionary.getInstance();
	protected static final Thesaurus THESAURUS = Thesaurus.getInstance();

	public abstract SolutionCollection solve(Clue c);

}
