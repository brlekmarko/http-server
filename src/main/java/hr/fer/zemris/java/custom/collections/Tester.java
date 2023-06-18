package hr.fer.zemris.java.custom.collections;


/**
 * Interface whose implementations test if given object is acceptable or not.
 * 
 * @author Marko Brlek
 *
 */
public interface Tester {
	

	/**
	 * Checks if given object is acceptable of not.
	 * 
	 * @param obj  Object which we want to test
	 * @return true if Object is acceptable, false otherwise
	 */
	boolean test(Object obj);
	
}
