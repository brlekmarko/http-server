package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;

/**
 * A node representing an entire document.
 * 
 * @author Marko Brlek
 *
 */
public class DocumentNode extends Node{

	
	/**
	 * Constructor
	 */
	public DocumentNode() {
		super();
	}
	
	
	
	@Override
	public String toString() {

		return visitNode(this);
		
		
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof DocumentNode) {
			return this.toString().equals(other.toString());
		}
		
		return false;
		
	}
	
	public static String visitNode(Node node) {
		
		Node current;
		
		String toReturn = "";
		
		for(int i=0; i<node.numberOfChildren(); i++) {
			current = node.getChild(i);
			
			if(current instanceof TextNode) {
				TextNode currentText = (TextNode)current;
				toReturn+=currentText.getText();
			}
			if(current instanceof EchoNode) {
				toReturn+= "{$= ";
				EchoNode currentEcho = (EchoNode)current;
				Element[] elem = currentEcho.getElements();
				for(int j=0; j<elem.length; j++) {
					if(elem[j] instanceof ElementString) {
						toReturn+= "\"" + elem[j].asText() + "\" ";
					}
					else {
						toReturn += elem[j].asText() + " ";
					}
				}
				toReturn+= "$}";
			}
			if(current instanceof ForLoopNode) {
				toReturn+= "{$FOR ";
				ForLoopNode currentFor = (ForLoopNode)current;
				toReturn += currentFor.getVariable().asText() + " ";
				toReturn += currentFor.getStartExpression().asText() + " ";
				toReturn += currentFor.getEndExpression().asText() + " ";
				if(currentFor.getStepExpression() != null) {
					toReturn += currentFor.getStepExpression().asText() + " ";
				}
				toReturn+="$}";
				toReturn+= visitNode(currentFor);
				
				toReturn+="{$END$}";
				
			}
		}
		return toReturn;
	}
	
	public void accept(INodeVisitor visitor) {
		visitor.visitDocumentNode(this);
	}
}
