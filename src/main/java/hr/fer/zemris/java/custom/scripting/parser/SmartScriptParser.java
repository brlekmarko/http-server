package hr.fer.zemris.java.custom.scripting.parser;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;
import hr.fer.zemris.java.custom.collections.ObjectStack;
import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;

/**
 * Takes inputed text, gives it to lexer, then arrange tokens in a parsing tree
 * using nodes.
 * 
 * 
 * @author Marko Brlek
 *
 */
public class SmartScriptParser {
	
	
	private DocumentNode documentNode; //root of the tree
	
	/**
	 * Constructor with inputed program as param
	 * 
	 * @param body inputed program
	 */
	public SmartScriptParser(String body) {
		
		SmartScriptLexer lexer = new SmartScriptLexer(body);
		
		this.documentNode = new DocumentNode();
		
		parseInput(lexer);
		
		
	}
	
	
	
	/**
	 * Goes over lexer tokens one by one and arrange it in a parsing tree
	 * 
	 * @param lexer SmartScriptLexer which breaks text down into tokens
	 */
	public void parseInput(SmartScriptLexer lexer) {
		
		ObjectStack stack = new ObjectStack();
		
		stack.push(documentNode);
		
		Element token = lexer.nextToken();
		
		while(token!=null && !(token instanceof ElementOperator && token.asText().equals("EOF"))) { //do until we reach EOF
			if (token instanceof ElementOperator && token.asText().equals("{$")) { //if we found tag opening, expect tag name next
				token = lexer.nextToken();
				
				if (token instanceof ElementOperator && token.asText().equals("FOR")) { //we are in a for tag, expect a ElementVariable and 2 or 3
																						//element of type string, double or integer
					Element token1 = lexer.nextToken();
					if(!(token1 instanceof ElementVariable)) { //check if valid type
						throw new SmartScriptParserException("Invalid first for argument");
					}
					Element token2 = lexer.nextToken();
					if(!(token2 instanceof ElementVariable) && !(token2 instanceof ElementString) &&
							!(token2 instanceof ElementConstantDouble) && !(token2 instanceof ElementConstantInteger)) { //check if valid type
						throw new SmartScriptParserException("Invalid second for argument");
					}
					Element token3 = lexer.nextToken();
					if(!(token3 instanceof ElementVariable) && !(token3 instanceof ElementString) &&
							!(token3 instanceof ElementConstantDouble) && !(token3 instanceof ElementConstantInteger)) { //check if valid type
						throw new SmartScriptParserException("Invalid third for argument");
					}
					Element token4 = lexer.nextToken();
					
					if((token4 instanceof ElementOperator) && token4.asText().equals("$}")){ //we had only 3 arguments, it is still valid
						
						ForLoopNode forNode = new ForLoopNode((ElementVariable) token1, token2, token3);
						Node previous = (Node) stack.peek();
						previous.addChildNode(forNode);
						stack.push(forNode);
						
						token = lexer.nextToken();
						continue;
					}
					else if(!(token4 instanceof ElementVariable) && !(token4 instanceof ElementString) &&
							!(token4 instanceof ElementConstantDouble) && !(token4 instanceof ElementConstantInteger)) { //if invalid type
						throw new SmartScriptParserException("Invalid fourth for argument");
					}
					else {
						Element token5 = lexer.nextToken();
						if((token5 instanceof ElementOperator) && token5.asText().equals("$}")){ //we had all 4 arguments, all valid
							ForLoopNode forNode = new ForLoopNode((ElementVariable) token1, token2, token3, token4);
							Node previous = (Node) stack.peek();
							previous.addChildNode(forNode); //child of top of stack node
							stack.push(forNode); //not empty tag, so we put it in stack
							
							token = lexer.nextToken();
							continue;
						}
						else {
							throw new SmartScriptParserException("Invalid for loop");
						}
					}

				}
				
				else if (token instanceof ElementOperator && token.asText().equals("END")) { //found END tag, need to pop FOR tag from stack
					
					Element token1 = lexer.nextToken();
					if((token1 instanceof ElementOperator) && token1.asText().equals("$}")){
						stack.pop();
						token = lexer.nextToken();
						if(stack.size() == 0) { //number of ends not matching fors
							
							throw new SmartScriptParserException("Too many ENDS");
						}
					}else {
						throw new SmartScriptParserException("Invalid END tag"); //END tag has no variables
					}
					
				}
				else if (token instanceof ElementOperator) {
					//for = tag
					
					token = lexer.nextToken();
					
					ArrayIndexedCollection collection = new ArrayIndexedCollection();
					
					
					
					
					while (!((token instanceof ElementOperator) && (token.asText().equals("$}")))) {
						
						if((token instanceof ElementOperator) && token.asText().equals("EOF")) { //found EOF in tag
							throw new SmartScriptParserException("Invalid = tag");
						}
						
						collection.add(token);
						token = lexer.nextToken();
					}
					
					Element[] elements = new Element[collection.size()];
					
					
					for (int i = 0; i < collection.size(); i++) { //can't cast whole array so have to do one by one
						elements[i] = (Element) collection.get(i);
					}
					
					EchoNode echoNode = new EchoNode(elements);
					Node previous = (Node) stack.peek();
					previous.addChildNode(echoNode);
					
					token = lexer.nextToken();
					
				}
				else {
					throw new SmartScriptParserException("Invalid token");
				}
			}
			else { //we are not in tag, read everything as text
				
				String text = "" + token.asText();
				
				token = lexer.nextToken();
				
				while (!(token instanceof ElementOperator) && 
						!(token.asText().equals("EOF")||(token.asText().equals("{$")))) { //until we reach EOF or tag opening
					text += " " + token.asText();
					token = lexer.nextToken();
				}
				
				TextNode textNode = new TextNode(text);
				Node previous = (Node) stack.peek();
				previous.addChildNode(textNode);
			}
		}
		
		
	}
	
	/**
	 * Returns the root of the parsing tree.
	 * 
	 * @return root of parsing tree
	 */
	public DocumentNode getDocumentNode() {
		return this.documentNode;
	}

}
