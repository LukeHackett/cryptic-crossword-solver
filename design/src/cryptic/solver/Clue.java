package cryptic.solver;
public class Clue {
	private String clue;
	private String pattern;
	private SolutionCollection solutions;
	
	public Clue(String clue, String pattern, String solutionLength){
		this.clue = clue;
		this.pattern = pattern;
		this.solutions = new SolutionCollection();
	}


	public String getClue(){
		return clue;
	}
	
	public String getPattern(){
		return pattern;
	}
	
	public SolutionCollection getSolutions(){
		return null;
	}



}
