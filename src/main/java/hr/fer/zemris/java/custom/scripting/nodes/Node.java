package hr.fer.zemris.java.custom.scripting.nodes;

import hr.fer.zemris.java.custom.collections.ArrayIndexedCollection;

/**
 * Base class for all graph nodes.
 * 
 * @author Marko Brlek
 *
 */
public abstract class Node {
	
	private ArrayIndexedCollection collection;
	
	/**
	 * Constructor
	 */
	public Node() {
		super();
	}
	
	
	
	/**
	 * Adds given child to an internally managed collection of children.
	 * Uses an instance of ArrayIndexedCollection for this.
	 * Collection is created on first call of this method.
	 * 
	 * 
	 * @param child Node which we want to add to collection
	 */
	public void addChildNode(Node child) {
		
		if(this.collection == null) {
			this.collection = new ArrayIndexedCollection();
		}
		this.collection.add(child);
	}
	
	
	/**
	 * Returns a number of (direct) children.
	 * 
	 * @return number of (direct) children.
	 */
	public int numberOfChildren() {
		if(this.collection == null) {
			return 0;
		}
		return this.collection.size();
	}
	
	
	
	/**
	 * Returns selected child or throws an appropriate exception if index is invalid.
	 * 
	 * @param index Index of child we want to get
	 * @return selected child
	 */
	public Node getChild(int index) {
		return (Node) this.collection.get(index);
	}

	public abstract void accept(INodeVisitor visitor);

}
