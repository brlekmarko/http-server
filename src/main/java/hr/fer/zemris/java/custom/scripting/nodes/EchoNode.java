package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;

/**
 * A node representing a command which generates some textual output dynamically.
 * 
 * @author Marko Brlek
 *
 */
public class EchoNode extends Node{
	
	
	private Element[] elements;
	
	
	/**
	 * Constructor with elements param
	 * 
	 * @param elements value of Elements[] property
	 */
	public EchoNode(Element[] elements) {
		super();
		this.elements = elements;
		
	}
	
	
	
	/**
	 * Returns value of Element[] property
	 * 
	 * @return value of Element[] property
	 */
	public Element[] getElements(){
		
		return this.elements;
	}
	
	public void accept(INodeVisitor visitor) {
		visitor.visitEchoNode(this);
	}

}
