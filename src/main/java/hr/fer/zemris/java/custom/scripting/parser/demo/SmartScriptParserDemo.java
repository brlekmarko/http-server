package hr.fer.zemris.java.custom.scripting.parser.demo;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexerException;
import hr.fer.zemris.java.custom.scripting.nodes.DocumentNode;
import hr.fer.zemris.java.custom.scripting.nodes.EchoNode;
import hr.fer.zemris.java.custom.scripting.nodes.ForLoopNode;
import hr.fer.zemris.java.custom.scripting.nodes.Node;
import hr.fer.zemris.java.custom.scripting.nodes.TextNode;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParserException;

public class SmartScriptParserDemo {
	
	
	public static void main(String[] args) {
		
		
		String docBody = "Ovo se ruši ${ = \\n $}";
		
		SmartScriptParser parser = null;
		
		try {
		 parser = new SmartScriptParser(docBody);
		} catch(SmartScriptParserException e) {
			
		 System.out.println("Unable to parse document!");
		 System.exit(-1);
		 
		} catch(SmartScriptLexerException e) {
			System.out.println("Unable to lex document!");
			 System.exit(-1);
		}
		catch(Exception e) {
		 System.out.println("If this line ever executes, you have failed this class!");
		 System.exit(-1);
		}
			
		DocumentNode document = parser.getDocumentNode();
		String originalDocumentBody = document.toString();
		System.out.println(originalDocumentBody);
			
	}
	
	public static void visitNode(Node node) {
		
		Node current;
		
		
		for(int i=0; i<node.numberOfChildren(); i++) {
			current = node.getChild(i);
			
			if(current instanceof TextNode) {
				TextNode currentText = (TextNode)current;
				System.out.println("Text " + currentText.getText());
			}
			if(current instanceof EchoNode) {
				EchoNode currentEcho = (EchoNode)current;
				Element[] elem = currentEcho.getElements();
				for(int j=0; j<elem.length; j++) {
					System.out.println("Echo " + elem[j].asText());
				}
			}
			if(current instanceof ForLoopNode) {
				ForLoopNode currentFor = (ForLoopNode)current;
				System.out.println("For " + currentFor.getVariable().asText());
				System.out.println("For " + currentFor.getStartExpression().asText());
				System.out.println("For " + currentFor.getEndExpression().asText());
				if(currentFor.getStepExpression() != null) {
					System.out.println("For " + currentFor.getStepExpression().asText());
				}
				visitNode(currentFor);
				
			}
		}
	}

}
