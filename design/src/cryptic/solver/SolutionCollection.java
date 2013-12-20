package cryptic.solver;

import java.util.HashSet;

public class SolutionCollection extends HashSet<Solution>{
	private static final long serialVersionUID = -7712846091431654498L;
	private int avgConfidence;
	
	public SolutionCollection() {
		
	}
	
	public boolean add(Solution solution) {
		return false;
	}
	
	public boolean remove(Object obj) {
		if(!obj.getClass().equals(this.getClass())){
			return false;
		}
		return false;
	}
	
	public void clear(){
		
	}
		
	public SolutionCollection getSolutionsGreaterThan(int confidence) {
		return null;
	}
	
	public SolutionCollection getSolutionsLowerThan(int confidence) {
		return null;
	}	
	
	public SolutionCollection getSolutionsInbetween(int upper, int lower){
		return null;
	}
	
}
