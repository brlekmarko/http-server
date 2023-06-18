package hr.fer.zemris.java.custom.scripting.elems;

/**
 * Inherits element, and has a single read-only String property: symbol
 * 
 * @author Marko Brlek
 *
 */
public class ElementOperator extends Element{
	
	private String symbol;
	
	
	/**
	 * Constructor with symbol param
	 * 
	 * @param symbol value of String property
	 */
	public ElementOperator(String symbol) {
		super();
		this.symbol = symbol;
	}
	
	
	/** Returns value of private symbol property
	 * 
	 * @return value of symbol property 
	 */
	public String getSymbol() {
		return this.symbol;
	}
	
	
	/**
	 * Here returns the value of symbol property
	 * 
	 * @return value of symbol property
	 */
	@Override
	public String asText() {
		
		return this.getSymbol();
	}

}

