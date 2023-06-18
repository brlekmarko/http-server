package hr.fer.zemris.java.custom.collections;

/**
 * Represents some general collection of objects
 * 
 * @author Marko Brlek
 *
 */
public interface Collection {

	
	
	
	/**
	 * Checks if the collection is empty.
	 * 
	 * @return true if collection contains no objects and false otherwise
	 */
	public default boolean isEmpty() {
		
		return this.size()==0;
	}
	
	
	/**
	 * Checks the size of this collection
	 * 
	 * @return the number of currently stored objects in this collection
	 */
	public int size();
	
	
	/**
	 * Adds an object to this collection
	 * 
	 * @param value  adds the given object into this collection
	 */
	public void add(Object value);
	
	
	
	/**
	 * Checks if this collection contains given value
	 * 
	 * @param value  Object for which we check if he is already in collection
	 * @return true only if the collection contains given value, as determined by equals method
	 */
	public boolean contains(Object value);
	
	
	
	/**
	 * Removes given value from this collection, if it contains the value
	 * 
	 * @param value  Object which we want to remove from the collection
	 * @return  true only if the collection contains given value as determined by equals method and removes one occurrence of it
	 */
	public boolean remove(Object value);
	
	
	
	/**
	 * Allocates new array with size equals to the size of this collections, fills it with collection content and 
	 * returns the array. This method never returns null.
	 * 
	 * @return new array containing this collections content
	 */
	public Object[] toArray();
	
	
	
	/**
	 * Method calls processor.process(.) for each element of this collection. The order in which elements 
	 * will be sent is undefined in this class
	 * 
	 * @param processor  concrete processor with implemented process method
	 */
	public default void forEach(Processor processor) {
		
		ElementsGetter collectionGetter = this.createElementsGetter();
		
		collectionGetter.processRemaining(processor);
		
	}
	
	
	
	/**
	 * Method adds into the current collection all elements from the given collection.
	 * This other collection remains unchanged.
	 * 
	 * @param other  another collection whose content we add to this collection
	 */
	public default void addAll(Collection other) {
		
		class AddProcessor implements Processor{  // we define a local concrete processor class
			
			public void process(Object value) {  // we override his process method
				Collection.this.add(value);			// to add the value to THIS INSTANCE of the Collection class
			}
		}
		
		
		other.forEach(new AddProcessor());		// send new concrete processor as argument
	}
	
	
	
	/**
	 * Removes all elements from this collection
	 */
	public void clear();
	
	
	
	/**
	 * Creates a new instance of ElementsGetter object on this collection and
	 * returns it.
	 * 
	 * @return new ElementsGetter object for this collection
	 */
	public ElementsGetter createElementsGetter();
	
	
	
	/**
	 * Adds all elements from given Collection to this Collection which the 
	 * given tester finds acceptable.
	 * 
	 * @param col  Collection whose elements we want to add to this collection
	 * @param tester  Tester which specifies how to tell if elements are acceptable
	 */
	default void addAllSatisfying(Collection col, Tester tester) {
		
		ElementsGetter colGetter = col.createElementsGetter();
		
		colGetter.processRemaining((value) -> {
			if(tester.test(value)) {
				this.add(value);
			}
		});
		
	}

	
	
	
}
