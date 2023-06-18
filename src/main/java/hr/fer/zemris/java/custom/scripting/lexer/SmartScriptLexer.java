package hr.fer.zemris.java.custom.scripting.lexer;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantDouble;
import hr.fer.zemris.java.custom.scripting.elems.ElementConstantInteger;
import hr.fer.zemris.java.custom.scripting.elems.ElementFunction;
import hr.fer.zemris.java.custom.scripting.elems.ElementOperator;
import hr.fer.zemris.java.custom.scripting.elems.ElementString;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * Breaks down inputed text into Element tokens
 * 
 * 
 * @author Marko Brlek
 *
 */
public class SmartScriptLexer {
	
	private char[] data; // ulazni tekst
	private Element token; // trenutni token
	private int currentIndex; // indeks prvog neobrađenog znaka
	private SmartScriptLexerState state;
	
	
	
	/**
	 * Constructor
	 * 
	 * @param text which is to be broken into tokens
	 */
	public SmartScriptLexer(String text) { 
		if(text == null) {
			throw new NullPointerException("Text can't be null.");
		}
		this.data = text.toCharArray();
		this.currentIndex = 0;
		this.state = SmartScriptLexerState.BASIC;
	}
	
	
	/**
	 * Generates and returns the next token.
	 * 
	 * @return the next token
	 * @throws LexerException
	 */
	public Element nextToken() { 
		
		
		extractNextToken();
		
		return this.token;
	}
	
	
	/** Returns the last generated token. Can be called multiple times.
	 *  Does not initiate generation of next token.
	 *  
	 * @return  last generated token
	 */
	public Element getToken() {
		
		return this.token;
	}
	
	
	/**
	 * Moves the current index to the next non-blank character.
	 */
	private void skipBlanks() {
		
		
		while (this.currentIndex < this.data.length) {
			
			char c = this.data[this.currentIndex];
			
			if (c== ' ' || c=='\t' || c=='\r' || c=='\n') {  //ignores these characters
				this.currentIndex++;
			}
			else {
				break;
			}
			
		}
	}
	
	
	
	/**
	 * Scans the inputed program from the current position onwards.
	 * Looks for the next token.
	 * 
	 * 
	 * @throws LexerException if errors in given program
	 */
	private void extractNextToken() {
		
		if(this.token!=null && (this.token instanceof ElementOperator) 
							&& this.token.asText().equals("EOF")) {  //if last token was EOF, refuse
			throw new SmartScriptLexerException("There is no next token");
		}
		
		if(this.currentIndex == this.data.length) { //if we are on the end of program, send EOF
			this.token = new ElementOperator("EOF");
			return;
		}
		
		
		if(state == SmartScriptLexerState.BASIC) {  //basic state looks for text outside of tags, stores as ElementString
			
			String text = "";
			
			Character c;
			
			while(this.currentIndex < this.data.length) {
				
				c = this.data[this.currentIndex];
				
				if(c == '{' && (this.currentIndex+1 < this.data.length) && this.data[this.currentIndex+1] == '$') {  //if we reached tag opening
					
					if(!text.equals("")) {
						break;
					}
					else {
						this.token =  new ElementOperator("{$");  //throw out tag opening
						this.state = SmartScriptLexerState.INTAG; //switch state
						this.currentIndex+=2;
						return;
					}
				}
					
				if(c == '\\') { //check for escaping
					this.currentIndex++;
					
					if(this.currentIndex == this.data.length) {
						throw new SmartScriptLexerException("Invalid escaping");
					}
					
					c = this.data[this.currentIndex];
					if (c == '\\' || c == '{') { //outside of string tags, only these escapes are allowed
						text += c;
						this.currentIndex++;
					}
					else {
						throw new SmartScriptLexerException("Invalid escaping");
					}
				}
			
				else {
					text += c;
					this.currentIndex++;
				}
				
			}
			
			this.token = new ElementString(text);
			return;
			
		}
		
		else if (state == SmartScriptLexerState.INTAG) { //inside of {$ $} tags, checks for variables, numbers, strings with ""
			
			String text = "";
						
			
			if((this.token instanceof ElementOperator) && this.token.asText().equals("{$")){ //looking for tag name
				
				text = lookForTagName("");
				this.token = new ElementOperator(text);
				
			}
			else {
				
				skipBlanks(); 
				
				
				Character c = this.data[this.currentIndex];
				
				if(c == '"') { //we are in string
					//text += c;
					this.currentIndex++;
					
					while(this.currentIndex < this.data.length) {
						c = this.data[this.currentIndex];
						
						if(c == '"') { //reached end of string, send out ElementString
							//text += c;
							this.currentIndex++;
							this.token = new ElementString(text);
							return;
						}
						else if(c =='\\' && (this.currentIndex+1 < this.data.length)) { //check for escaping
							Character c1 = this.data[this.currentIndex+1];
							if(c1 == '\\' || c1 == '"') { //only these are allowed, plus \n \r \t
								text += c + c1;
								this.currentIndex+=2;
							}
							else if(c1 == 'n') {
								text += '\n';
								this.currentIndex+=2;
							}
							else if(c1 == 'r') {
								text += '\r';
								this.currentIndex+=2;
							}
							else if(c1 == 't') {
								text += '\t';
								this.currentIndex+=2;
							}
							else {
								throw new SmartScriptLexerException("Invalid escaping in string");
							}
						}
						else {
							text+=c;
							this.currentIndex++;
						}
					}
					throw new SmartScriptLexerException("String was not closed");
				}
				
				else if(Character.isLetter(c)) { //means we are looking for variable name
					text += c;
					this.currentIndex++;
					text = lookForVariableName(text);
					this.token = new ElementVariable(text);
					
				}
				
				else if(Character.isDigit(c)
						|| (c == '-' && (this.currentIndex+1<this.data.length) && Character.isDigit(this.data[this.currentIndex+1]))) {
					//if digit or if minus sign and digit after
					//means we are looking for int/double number
					text += c;
					this.currentIndex++;
					text = lookForNumber(text);
					
					if(text.contains(".")) {
						Double value = Double.parseDouble(text);
						this.token = new ElementConstantDouble(value);
						return;
					}
					else {
						int value = Integer.parseInt(text);
						this.token = new ElementConstantInteger(value);
						return;
					}
				}
				
				else if(c == '@') { //means we are looking for function name, rules are same as variable name
					text += c;
					this.currentIndex++;
					text = lookForVariableName(text);
					
					this.token = new ElementFunction(text);
				}
				
				else if(c == '+' || c == '-' || c == '/' || c == '*' || c == '^') { //found operator
					text += c;
					this.currentIndex++;
					
					this.token = new ElementOperator(text);
					return;
				}
				
				else if(c == '$' && (this.currentIndex+1 < this.data.length) && this.data[this.currentIndex+1] == '}') { //found end of tag
					this.token =  new ElementOperator("$}"); //send it out
					this.state = SmartScriptLexerState.BASIC; //switch state
					this.currentIndex+=2;
					return;
				}
				else {
					throw new SmartScriptLexerException("Invalid tag");
				}
			}
		}
		
		

		
	}
	
	
	/**
	 * This method goes over inputed program symbol by symbol until next one doesnt
	 * satisfy variable name rules. Then it returns found variable name
	 * 
	 * @param text String with text that was collected so far
	 * @return longest possible variable name, includes given param at start of String
	 */
	public String lookForVariableName(String text) {
		
		String newText = text;
		
		Character c;

		
		while(this.currentIndex < this.data.length) {
			 
			c = this.data[this.currentIndex];
			
			if(newText.length() == 0) {  //needs to start with a letter
				if(Character.isLetter(c)) {
					newText+=c;
					this.currentIndex++;
				}
				else {
					throw new SmartScriptLexerException("Invalid variable name");
				}
			}
			else { //can also be digits or underscore
				
				if(Character.isLetter(c) || Character.isDigit(c) || c=='_') {
					newText+=c;
					this.currentIndex++;
					
				}
				else {
					break;
				}
			}
		}
		
		return newText;
		
	}
	
	/**
	 * This method goes over inputed program symbol by symbol until next one doesnt
	 * satisfy tag name rules. Then it returns found tag name.
	 * Rules are same as variable name, except it allows "="
	 * 
	 * @param text String with text that was collected so far
	 * @return longest possible tag name, includes given param at start of String
	 */
	public String lookForTagName(String text) {
		
		String newText = text;
		
		
		skipBlanks(); //spaces are irrelevant in tags
		
		Character c = this.data[this.currentIndex];

		
		if(c == '=' || Character.isLetter(c)) {
			newText += c;
			this.currentIndex++;
			if(c == '=') {
				return newText;
			}
			else {
				newText = lookForVariableName(newText);
				return newText.toUpperCase();
			}

		}
		else {
			throw new SmartScriptLexerException("Invalid tag name");
		}
	}
	
	
	/**
	 * This method goes over inputed program symbol by symbol until next one doesnt
	 * satisfy number rules. Then it returns found number.
	 * Number can be negative, and has 0 or 1 dots
	 * 
	 * @param text String with text that was collected so far
	 * @return longest possible tag name, includes given param at start of String
	 */
	public String lookForNumber(String text) {
		
		String newText = text;
		
		boolean decimal = false;  //make sure it can have only one dot
		
		Character c;
		
		while(this.currentIndex < this.data.length) {
			
			c = this.data[this.currentIndex];
			
			if(Character.isDigit(c) || (decimal==false && c=='.')) {
				newText += c;
				this.currentIndex++;
				
				if(c=='.') decimal=true;

			}
			else if(c == ' ') {
				this.currentIndex++;
				break;
			}
			else {
				//throw new SmartScriptLexerException("Invalid number");
				break;
			}
		}
		
		return newText;
		
		
	}
	

}
