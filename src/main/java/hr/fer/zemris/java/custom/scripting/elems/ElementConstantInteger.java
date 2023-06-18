package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Inherits Element and has single read-only int property: value
 * 
 * @author Marko Brlek
 *
 */
public class ElementConstantInteger extends Element{
	
	private int value;
	
	
	/**
	 * Constructor with value param
	 * 
	 * @param value value of int property
	 */
	public ElementConstantInteger(int value) {
		super();
		this.value = value;
	}
	
	
	/** Returns value of private value property
	 * 
	 * @return value of value property 
	 */
	public int getValue() {
		return this.value;
	}
	
	
	/**
	 * Here returns the value of value property
	 * 
	 * @return value of value property
	 */
	@Override
	public String asText() {
		return ""+this.value;
	}

}
