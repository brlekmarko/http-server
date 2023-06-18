package hr.fer.zemris.java.custom.scripting.nodes;

/**
 * A node representing a piece of textual data.
 * 
 * @author Marko Brlek
 *
 */
public class TextNode extends Node{
	
	
	private String text;
	
	
	/**
	 * Constructor with text param
	 * 
	 * @param symbol value of String property
	 */
	public TextNode(String text) {
		super();
		this.text = text;
	}
	
	
	/**
	 * Returns value of string property
	 * 
	 * @return value of string propery
	 */
	public String getText() {
		return this.text;
	}
	
	public void accept(INodeVisitor visitor) {
		visitor.visitTextNode(this);
	}

}
