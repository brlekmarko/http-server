package hr.fer.zemris.java.custom.collections;

/**
 * Uses ArrayIndexedCollection class and adapts it so the user sees it as a stack.
 * 
 * @author Marko Brlek
 *
 */
public class ObjectStack {
	
	private ArrayIndexedCollection storage;
	
	
	
	/**
	 * Creates an instance of ObjectStack and ArrayIndexedCollection which 
	 * will be used for storage purposes and imitate a stack.
	 * Starting size will be 16
	 */
	public ObjectStack() {
		super();
		storage = new ArrayIndexedCollection();
	}
	
	
	/**
	 * Checks if stack contains any elements.
	 * 
	 * @return true if empty, false if it is not
	 */
	public boolean isEmpty() {
		return storage.isEmpty();
	}
	
	
	
	/**
	 * Returns number of elements stored in this stack
	 * Same as ArrayIndexedCollection.size()
	 * 
	 * @return number of elements stored in this stack
	 */
	public int size() {
		return storage.size();
	}
	
	
	
	/**
	 * Adds value to the stack. Actually adds it at the end of array.
	 * 
	 * @param value  Object which we add to the stack.
	 */
	public void push(Object value) {
		storage.add(value);
	}
	
	
	
	/**
	 * Returns last element in stack and deletes it.
	 * 
	 * @return last element in stack
	 * @throws EmptyStackException  when called on an empty stack
	 */
	public Object pop() {
		if(this.isEmpty()) {
			throw new EmptyStackException("Stack is empty, can't pop.");
		}
		else {
			Object toReturn = storage.get(storage.size()-1);
			storage.remove(storage.size()-1);
			return toReturn;
		}
	}
	
	
	
	/**
	 * Returns last element in stack, but does NOT delete it.
	 * 
	 * @return last element in stack
	 * @throws EmptyStackException  when called on an empty stack
	 */
	public Object peek() {
		if(this.isEmpty()) {
			throw new EmptyStackException("Stack is empty, can't pop.");
		}
		else {
			return storage.get(storage.size()-1);
		}
	}
	
	
	
	/**
	 * Removes all elements from stack.
	 */
	public void clear() {
		storage.clear();
	}
	
	
	

}
