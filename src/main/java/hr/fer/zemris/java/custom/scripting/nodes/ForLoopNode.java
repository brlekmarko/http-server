package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.scripting.elems.Element;
import hr.fer.zemris.java.custom.scripting.elems.ElementVariable;

/**
 * A node representing a single for-loop construct.
 * 
 * @author Marko Brlek
 *
 */
public class ForLoopNode extends Node{
	
	
	private ElementVariable variable;
	private Element startExpression;
	private Element endExpression;
	private Element stepExpression;
	
	
	/**
	 * Constructor with all 4 params
	 * 
	 * @param variable ElementVariable which is used in for loop
	 * @param startExpression Element which represents for loop start expression
	 * @param endExpression Element which represents for loop end expression
	 * @param stepExpression for loop step
	 */
	public ForLoopNode(ElementVariable variable, Element startExpression, 
						Element endExpression, Element stepExpression) {
		super();
		this.variable = variable;
		this.startExpression = startExpression;
		this.endExpression = endExpression;
		this.stepExpression = stepExpression;
		
	}
	
	
	/**
	 * Constructor with 3 params, stepExpression is null
	 * 
	 * @param variable ElementVariable which is used in for loop
	 * @param startExpression Element which represents for loop start expression
	 * @param endExpression Element which represents for loop end expression
	 */
	public ForLoopNode(ElementVariable variable, Element startExpression, 
						Element endExpression) {
		this(variable, startExpression, endExpression, null);
		
	}
	
	
	
	/** Returns value of private variable property
	 * 
	 * @return value of variable property 
	 */
	public ElementVariable getVariable() {
		return this.variable;
	}
	
	
	/** Returns value of private startExpression property
	 * 
	 * @return value of startExpression property 
	 */
	public Element getStartExpression() {
		return this.startExpression;
	}
	
	
	/** Returns value of private endExpression property
	 * 
	 * @return value of endExpression property 
	 */
	public Element getEndExpression() {
		return this.endExpression;
	}
	
	
	/** Returns value of private stepExpression property
	 * 
	 * @return value of stepExpression property 
	 */
	public Element getStepExpression() {
		return this.stepExpression;
	}
	
	
	public void accept(INodeVisitor visitor) {
		visitor.visitForLoopNode(this);
	}

}
