package fca;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * 
 */

/**
 * @author kaio
 *
 */
public class DyadicFormalConcept {

	
	private Set<Integer> extent;
	private Set<Integer> intent;
	
	
	
	/**
	 * 
	 */
	public DyadicFormalConcept() {
		// TODO Auto-generated constructor stub
	}
	
	
	public DyadicFormalConcept(Set<Integer> extent, Set<Integer> intent) {
		this.extent = extent;
		this.intent = intent;
	}
	
	

	@Override
	public String toString() {
		return "{" + this.extent.toString() + ", " + this.intent.toString() + "}";
	}
	
	

	public Set<Integer> getExtent() {
		return extent;
	}

	public void setExtent(Set<Integer> extent) {
		this.extent = extent;
	}

	public Set<Integer> getIntent() {
		return intent;
	}

	public void setIntent(Set<Integer> intent) {
		this.intent = intent;
	}



}
