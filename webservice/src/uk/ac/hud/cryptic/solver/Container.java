package uk.ac.hud.cryptic.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import uk.ac.hud.cryptic.core.Clue;
import uk.ac.hud.cryptic.core.Solution;
import uk.ac.hud.cryptic.core.SolutionCollection;
import uk.ac.hud.cryptic.core.SolutionPattern;
import uk.ac.hud.cryptic.resource.Categoriser;

/**
 * Container solver algorithm
 * 
 * ------------Example-------------
 * Clue - Stash or put in stage (7)
 * Answer - Storage
 * --------------------------------
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
		SolutionCollection solutions = new SolutionCollection();

		// Get clue pattern
		final SolutionPattern pattern = c.getPattern();
		// Get length of full answer
		int solutionLength = pattern.getTotalLength();

		// Get clue with no punctuation
		String clue = c.getClueNoPunctuation(true);

		// Clue length must be greater than solution length
		if (clue.length() < solutionLength) {
			return solutions;
		}

		// Separate the words for fast comparison
		String[] cluewords = c.getClueWords();
		ArrayList<String> words = new ArrayList<String>();	
		for(int i =0; i < c.getClueWords().length; i++){
			words.add(cluewords[i]);
		}

		// To Store possible indicators
		Map<Integer,String> containerIndicators = new HashMap<Integer,String>();


		// Read in indicators for container clue types
		Collection<String> indicators = Categoriser.getInstance()
				.getIndicators(NAME);

		// Temporarily hold indicators in file
		ArrayList<String> theIndicators = new ArrayList<String>();
		ArrayList<Integer> count = new ArrayList<Integer>();
		for (String indicator : indicators) {
			theIndicators.add(indicator);
		}

		//Search the clue for possible indicators that match from file
		//NOTE* Indicators may be more than one word
		for (int y = 0; y < words.size();y++) {
			if(theIndicators.contains(words.get(y))){
				//Position of indicator in the clue and the indicator string
				//To be used to remove indicator words
				count.add(y);
				containerIndicators.put(y,words.get(y));
			}
		}

		// No matches from indicators return no solutions from solver
		if(containerIndicators.isEmpty()){
			return solutions;
		}

		//A copy of the words to alter
		ArrayList<String> wordscp = words;

		// Take out the indicator from the words
		for (Map.Entry<Integer, String> entry : containerIndicators.entrySet()){
			int r = entry.getKey();
			wordscp.remove(r);
		}

		// Retrieve synonyms from the thesaurus for each word
		Map<String, Set<String>> synonyms = new HashMap<>();
		synonyms = getSynonyms(wordscp,synonyms);


		//remove one word at a time and then look for matching
		//container words equal to a synonym of the removed word

		Set<String> syns= null;
		ArrayList<String> copy = wordscp;
		for (String word : wordscp) {
			syns = synonyms.get(word);
			solutions.addAll(findmatches(word, syns, copy, solutionLength));
		}
		
		System.out.println(solutions);
		return solutions;
	}


	/**
	 * A method which retrieves synonyms for a list of words
	 * @param wordscp the list of words
	 * @param synonyms a map to list the synonyms
	 * @return the synonyms of the list of words
	 */
	private Map<String, Set<String>> getSynonyms(ArrayList<String> wordscp, 
			Map<String, Set<String>> synonyms) {
		for (String word : wordscp) {
			synonyms.put(word, THESAURUS.getSynonyms(word));
		}
		return synonyms;
	}



	/**
	 * This method returns possible solutions by checking eliminating one word at a time
	 * and checking if the remainder of the words put together in the form of a container
	 * is a synonym of the word that has been eliminated
	 * @param word the word to eliminate
	 * @param syns the synonyms of the word to be elimated
	 * @param copy a list of words that maybe the fodder of the container
	 * @param solutionLength the length of the solution
	 * @return
	 */
	private Collection<? extends Solution> findmatches(String word, Set<String> syns, 
			ArrayList<String> copy, int solutionLength) {

		//Create a copy to be alterd
		ArrayList<String> wordsToCheck = new ArrayList<String>(copy);
		System.out.println("word: " + word);
		System.out.println("Synonyms of '" + word + "' : " + syns);
		System.out.println("wordsToCheck: " + wordsToCheck);

		//Remove the word which of which the synonym 
		//list is for from the list of words
		for(String st : copy) {
			if(st.equals(word)) {                
				wordsToCheck.remove(st);
			}
		}

		//Create a  collection of solutions
		SolutionCollection somesolutions = new SolutionCollection();

		for(String w: wordsToCheck){
			for(String synon: syns){
				if(synon.contains(w) && synon.length() == solutionLength){
					String syncopy = synon;
					syncopy = syncopy.replace(w, "");
					if(syncopy.equals(word)){
						Solution s = new Solution(synon, NAME);			
						somesolutions.add(s);}
				}
			}
		}
		return somesolutions;
	}

	@Override
	public String toString() {
		return NAME;
	}

} // End of class Container
