package hr.fer.zemris.java.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Implementations of this interface help iterate over Collection objects.
 * Every ElementsGetter iterates over his own Collection.
 * You can create multiple ElementsGetters on the same Collection at the same time
 * and each one works separately, creating the same results. 
 * 
 * 
 * @author Marko Brlek
 *
 */
public interface ElementsGetter {
	
	
	/**
	 * Checks if there are elements in Collection which have not
	 * been sent out yet.
	 * 
	 * @return true if there are some elements left to send out, false otherwise
	 * @throws ConcurrentModificationException if method is called after the Collection was changed
	 */
	public boolean hasNextElement() throws ConcurrentModificationException;
	
	
	
	
	/**
	 * Returns the first element in the Collection which hasn't been sent out yet.
	 * If every element has been sent out and this method is called, throw NoSuchElementException
	 * 
	 * @return first element in the Collection which hasn't been sent out yet
	 * @throws NoSuchElementException if there are no elements left to get, meaning hasNextElement() is false
	 * @throws ConcurrentModificationException if method is called after the Collection was changed
	 */
	public Object getNextElement() throws NoSuchElementException, ConcurrentModificationException;
	
	
	
	
	/**
	 * This method calls the Processors process(Object) method on Collection elements which
	 * have not been iterated over yet.
	 * 
	 * @param p  Processor whose process(Object) method we want to call
	 * @throws ConcurrentModificationException if method is called after the Collection was changed
	 */
	public default void processRemaining(Processor p){
		
		while (this.hasNextElement()) {
			p.process(this.getNextElement());
		}
		
	}

}
