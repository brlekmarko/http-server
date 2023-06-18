package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Inherits Element and has single read-only double property: value
 * 
 * @author Marko Brlek
 *
 */
public class ElementConstantDouble extends Element{
	
	private double value;
	
	
	/**
	 * Constructor with value param
	 * 
	 * @param value value of double property
	 */
	public ElementConstantDouble(double value) {
		super();
		this.value = value;
	}
	
	
	/** Returns value of private value property
	 * 
	 * @return value of value property 
	 */
	public double getValue() {
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

