package hr.fer.zemris.java.custom.scripting.lexer;

public enum SmartScriptLexerState {

	
	/**
	 * Default lexer behavior
	 */
	BASIC,
	
	
	/**
	 * After reading the {$ symbols, reads strings as variables instead of text
	 */
	INTAG,
	

}
