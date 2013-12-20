package cryptic.solver;
public class Solution {
	private String solution;
	private int[] length;
	private double confidence;
	private SolutionTrace trace;

	
	public Solution(String solution, int len) {
		
	}
	
	public Solution(String solution, int[] len){
		
	}
	
	public void addTrace(String trace){
		
	}
	
	public void getSolutionTrace(){
		
	}
	
	
	
	/*
	 * GETTERS 
	 */
	public String getSolution(){
		return solution;
	}
	
	public String[] getSolutionWords(){
		return null;
	}
	
	
	public int[] getLength(){
		return length;
	}
	
	public double getConfidence(){
		return confidence;
	}
	
	public boolean hasGreaterConfidence(){
		return false;
	}
	
	public boolean hasLowerConfidence(){
		return false;
	}

	public boolean equals(Solution sol){
		return false;
	}
	
	public String toString(){
		return "";
	}

}
