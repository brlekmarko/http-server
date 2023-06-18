package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Inherits element, and has a single read-only String property: name
 * 
 * @author Marko Brlek
 *
 */
public class ElementVariable extends Element{
	
	private String name;
	
	
	/**
	 * Constructor with name param
	 * 
	 * @param name value of String property
	 */
	public ElementVariable(String name) {
		super();
		this.name = name;
	}
	
	
	/** Returns value of private name property
	 * 
	 * @return value of name property 
	 */
	public String getName() {
		return this.name;
	}
	
	
	/**
	 * Here returns the value of name property
	 * 
	 * @return value of name property
	 */
	@Override
	public String asText() {
		
		return this.getName();
	}

}
