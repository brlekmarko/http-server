package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Inherits element, and has a single read-only String property: value
 * 
 * @author Marko Brlek
 *
 */
public class ElementString extends Element{
	
	private String value;
	
	
	/**
	 * Constructor with value param
	 * 
	 * @param value value of String property
	 */
	public ElementString(String value) {
		super();
		this.value = value;
	}
	
	
	/** Returns value of private value property
	 * 
	 * @return value of value property 
	 */
	public String getValue() {
		return this.value;
	}
	
	
	/**
	 * Here returns the value of value property
	 * 
	 * @return value of value property
	 */
	@Override
	public String asText() {
		
		return this.getValue();
	}

}
