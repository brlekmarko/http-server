package hr.fer.zemris.java.custom.collections;

/**
 * Represents a model of an object capable of performing some operation on the passed object
 * 
 * @author Marko Brlek
 *
 */
public interface Processor {
	
	
	/**
	 * This method performs some operation on the passed object.
	 * 
	 * @param value  the object on which we want to perform the operation
	 * 
	 */
	public void process(Object value);

}
