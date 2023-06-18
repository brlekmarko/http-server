package hr.fer.zemris.java.custom.scripting.lexer.demo;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.lexer.SmartScriptLexer;
import hr.fer.zemris.java.custom.scripting.parser.SmartScriptParser;

public class SmartScriptLexerDemo {
	
	
	public static void main(String[] args) {
		
		String ulaz = "Ovo je isto OK {$ = \"String ide\n"
				+ "u \\\"više\\\" \nredaka\n"
				+ "ovdje a stvarno četiri\" $}";
		
		SmartScriptLexer lexer = new SmartScriptLexer(ulaz);
		
		Element token = null;
		
		while(!((token instanceof ElementOperator) && token.asText().equals("EOF"))) {
			
			token = lexer.nextToken();
			System.out.println("NEW " + token.asText());
			
		}
		
		
		SmartScriptParser parser = new SmartScriptParser(ulaz);
		
		System.out.println(parser.getDocumentNode().toString());
		
		SmartScriptLexer lexer2 = new SmartScriptLexer(parser.getDocumentNode().toString());
		
		token = null;
		
		while(!((token instanceof ElementOperator) && token.asText().equals("EOF"))) {
			
			token = lexer2.nextToken();
			System.out.println("NEW " + token.asText());
			
		}
	}

}
