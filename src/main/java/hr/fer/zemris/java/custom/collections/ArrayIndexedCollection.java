package hr.fer.zemris.java.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

/**
 * Represents a resizable array-backed collection of objects
 * Duplicate elements are allowed; storage of null references is not allowed
 * 
 * @author Marko Brlek
 *
 */
public class ArrayIndexedCollection implements List{
	
	private int size;
	private Object[] elements;
	private long modificationCount = 0;
	
	
	
	
	/**
	 * Creates an instance of ArrayIndexedCollection
	 * Sets capacity of array to 16
	 * Sets size to zero because array is empty
	 */
	public ArrayIndexedCollection() {
		super();
		elements = new Object[16];
		size = 0;
	}
	
	
	
	/**
	 * Creates an instance of ArrayIndexedCollection 
	 * Sets capacity of array to initialCapacity
	 * Sets size to zero because array is empty
	 * 
	 * @param initialCapacity  value to which we set arrays capacity
	 * @throws IllegalArgumentException  if initialCapacity is less then 1
	 */
	public ArrayIndexedCollection(int initialCapacity) {
		super();
		
		if (initialCapacity < 1) {
			throw new IllegalArgumentException ("Initial capacity can't be less then 1");
		}else {
			elements = new Object[initialCapacity];
			size = 0;
		}
	}
	
	
	
	/**
	 * Creates an instance of ArrayIndexedCollection
	 * Copies elements of given Collection to this collection
	 * Sets capacity to 16 or to the size of given Collection, whichever is bigger
	 * 
	 * 
	 * @param other  collection whose elements we copy to this collection
	 * @throws NullPointerException  if given collection is null
	 */
	public ArrayIndexedCollection(Collection other) {
		super();
		if (other == null) {
			throw new NullPointerException("Passed collection can't be null");
		}else {
			if(other.size() > 16) {
				elements = new Object[other.size()];
				this.addAll(other);
			}else {
				elements = new Object[16];
				this.addAll(other);
			}
		}
	}
	
	
	
	/**
	 * Creates an instance of ArrayIndexedCollection
	 * Copies elements of given Collection to this collection
	 * Sets capacity to parameter initialCapacity or to the size of given Collection, whichever is bigger
	 *  
	 * 
	 * @param other  collection whose elements we copy to this collection
	 * @param initialCapacity  value to which we set arrays capacity
	 * @throws NullPointerException  if given collection is null
	 * @throws IllegalArgumentException  if initialCapacity is less then 1
	 */
	public ArrayIndexedCollection(Collection other, int initialCapacity) {
		super();
		if (other == null) {
			throw new NullPointerException("Passed collection can't be null");
		}
		else if (initialCapacity < 1) {
			throw new IllegalArgumentException ("Initial capacity can't be less then 1");
		}
		
		else {
			
			if(other.size() > initialCapacity) {
				elements = new Object[other.size()];
				this.addAll(other);
			}
			else {
				elements = new Object[initialCapacity];
				this.addAll(other);
				
			}
		}
	}
	
	
	
	/**
	 *Adds the given object into this collection (reference is added into first empty place in the elements array; 
	 * if the elements array is full, it should be reallocated by doubling its size)
	 * 
	 * @param value  Object which we want to add to this collection
	 * @throws NullPointerException  if given value is null
	 */
	@Override
	public void add(Object value) {
		if (value == null) {
			throw new NullPointerException("Given value can't be null.");
		}
		else {
			
			if(this.size < this.elements.length) {    //if there is still space in array
				this.elements[this.size] = value;     //add element to next free space
				this.size++;						  //there is now one more element
			}
			else {
				//the array is full, we need to double its size
				this.elements = new ArrayIndexedCollection(this, 2*this.elements.length).elements;
				//we use our new constructor to easily create new array and copy old contents
				
				this.elements[this.size] = value;
				this.size++;
			}
		}
		//complexity is O(1) if array isn't full, and O(n) if array is full, which happens rarely
		//average complexity is O(1)
		
	}
	
	
	
	/**
	 * Returns the object that is stored in backing array at position index.
	 * 
	 * @param index  which element we want to get, valid indexes are 0 to size-1
	 * @return object on given index
	 * @throws IndexOutOfBoundsException  if index is invalid
	 */
	@Override
	public Object get(int index) {
		if(index>=0 && index<=this.size-1) {
			return this.elements[index];
		}
		else {
			throw new IndexOutOfBoundsException("Given index is invalid.");
		}
		
		//complexity is O(1) because accessing array by index has that complexity
	}
	
	
	
	/**
	 * Removes all elements from this collection.
	 * Size becomes 0, capacity is unchanged.
	 */
	@Override
	public void clear() {
		
		for (int i = 0; i<this.size; i++) {
			this.elements[i] = null;
		}
	
		this.size = 0;
		
		this.modificationCount++;
		
	}
	
	
	
	/**
	 * Inserts (does not overwrite) the given value at the given position in array
	 * Shifts elements to the right so that an empty place is created at position
	 * 
	 * 
	 * @param value  Object which we want to insert in collection
	 * @param position  index at which we want to insert the object, valid positions are 0 to size (both included)
	 * @throws IndexOutOfBoundsException  if index is invalid
	 */
	@Override
	public void insert(Object value, int position) {
		
		if (position>=0 && position<=this.size) {
			//First we create a collection which contains all elements to the right of position (included)
			//From the original collection, we delete all the elements on the right
			//We are left with elements from index 0 to position-1
			//Right side collection contains our new element
			ArrayIndexedCollection rightSide = new ArrayIndexedCollection(this.size-position+1);
			rightSide.add(value);
			for(int i=position; i<this.size; i++) {
				rightSide.add(this.elements[i]);
				this.elements[i] = null;
			}
			this.size = position;
			
			this.addAll(rightSide);
			//we add all the elements on the right of it
			//add method will take care of doubling its size
			
			this.modificationCount++;
		}
		else {
			throw new IndexOutOfBoundsException("Given index is invalid.");
		}
		
		
		//complexity is O(n/2)+O(n/2) because of the for loop + addAll method
		//average is O(n)
		
	}
	
	
	
	/**
	 * Searches the collection and returns the index of the first occurrence of the given value 
	 * or -1 if the value is not found
	 * 
	 * @param value  object for which we are looking for
	 * @return  -1 if the value is not found, otherwise index of the first occurrence of value 
	 */
	@Override
	public int indexOf(Object value) {
		if(value == null) return -1;
		
		for (int i=0; i<this.size; i++) {
			if(this.elements[i].equals(value)) {
				return i;
			}
		}
		return -1;
		//average complexity is O(this.size/2) so O(n)
	}

	
	
	/**
	 * Removes element at specified index from collection. Element that was previously at 
	 * location index+1 after this operation is on location index, etc. 
	 * 
	 * @param index  position at which we are removing the element, legal indexes are 0 to size-1
	 * @throws IndexOutOfBoundsException  if index is invalid
	 */
	@Override
	public void remove(int index) {
		if(index>=0 && index<=size-1) {
			for(int i=index; i<this.size-1; i++) {    //start at position which we want to remove
				this.elements[i]=this.elements[i+1];  //replaces each element with the one to the right of it
			}
			this.elements[this.size]=null;  //remove duplicate at the end
			this.size--;
			this.modificationCount++;
		}
		else {
			throw new IndexOutOfBoundsException("Given index is invalid.");
		}
	}
	
	
	
	/**
	 * Checks the size of this collection
	 * Overrides empty method from Collection
	 * 
	 * @return the number of currently stored objects in this collection
	 */
	@Override
	public int size() {
		return this.size;
	}
	
	
	
	/**
	 * Checks if this collection contains given value
	 * Overrides empty method from Collection
	 * 
	 * @param value  Object for which we check if he is already in collection
	 * @return true only if the collection contains given value, as determined by equals method
	 */
	@Override
	public boolean contains(Object value) {
		
		return this.indexOf(value)!=-1;  //if index is -1, it doesn't contain it
	}
	
	
	
	/**
	 * Removes given value from this collection, if it contains the value
	 * Removes one occurrence of it
	 * Overrides empty method from Collection
	 * 
	 * @param value  Object which we want to remove from the collection
	 * @return  true only if the collection contains given value as determined by equals method
	 */
	@Override
	public boolean remove(Object value) {
		int position = this.indexOf(value);
		if(position == -1) return false;  //value is not contained
		
		this.remove(position);  //remove first occurrence of the value 
		return true;
	}
	
	
	
	/**
	 * Allocates new array with size equals to the size of this collections, fills it with collection content and 
	 * returns the array. This method never returns null.
	 * Overrides empty method from Collection
	 * 
	 * @return new array containing this collections content
	 */
	@Override
	public Object[] toArray() {
		Object[] toReturn = new Object[this.size];  //allocate new array with collections size
		for(int i=0; i<this.size; i++) {
			toReturn[i] = this.elements[i];  //fill it with collection content
		}
		return toReturn;
	}
	
		
	
	
	/**
	 * Method returns a string of collections content, primarily used to test outputs.
	 * 
	 * @return String containing every stored Objects toString result
	 */
	@Override
	public String toString() {
		String toReturn = "";
		for(int i=0; i<this.size; i++) {
			toReturn += this.elements[i].toString() + ";;";
		}
		return toReturn;
	}
	
	
	
	/**
	 * Returns length of array in which elements are collected.
	 * Used for testing purposes.
	 * 
	 * @return length of array in which elements are collected
	 */
	public int getCapacity() {
		return this.elements.length;
	}
	
	
	
	
	/**
	 * Implementation of ElementsGetter interface. Used to iterate over an ArrayIndexedCollection object.
	 * 
	 * 
	 * @author Marko Brlek
	 */
	private static class ArrayElementsGetter implements ElementsGetter{
		
		private int counter = 0;  //tells us how far we have gone
		
		private ArrayIndexedCollection sentCollection;
		
		private long savedModificationCount;
		
		
		
		/**
		 * The only constructor. The ArrayIndexedCollection over which we want to iterate is sent.
		 * 
		 * @param collection  ArrayIndexedCollection over which we want to iterate
		 */
		public ArrayElementsGetter(ArrayIndexedCollection collection) {
			super();
			this.sentCollection = collection;
			this.savedModificationCount = collection.modificationCount;
		}
		

		
		/**
		 * Checks if there are elements in ArrayIndexedCollection which have not
		 * been sent out yet.
		 * 
		 * @return true if there are some elements left to send out, false otherwise
		 * @throws ConcurrentModificationException if method is called after the ArrayIndexedCollection was changed
		 */
		@Override
		public boolean hasNextElement() throws ConcurrentModificationException{
			if(this.savedModificationCount != this.sentCollection.modificationCount) {
				throw new ConcurrentModificationException("Can't check because collection was recently changed.");
			}
			else {
				return this.counter < sentCollection.size;
			}
		}
		
		
		
		/**
		 * Returns the first element in the ArrayIndexedCollection which hasn't been sent out yet.
		 * If every element has been sent out and this method is called, throw NoSuchElementException
		 * 
		 * @return first element in the ArrayIndexedCollection which hasn't been sent out yet
		 * @throws NoSuchElementException if there are no elements left to get, meaning hasNextElement() is false
		 * @throws ConcurrentModificationException if method is called after the ArrayIndexedCollection was changed
		 */
		@Override
		public Object getNextElement() throws NoSuchElementException, ConcurrentModificationException {
			if(this.savedModificationCount != this.sentCollection.modificationCount) {
				throw new ConcurrentModificationException("Can't get element because collection was recently changed.");
			}
			else {
				if (this.hasNextElement()) {
					return sentCollection.get(this.counter++);
				}
				throw new NoSuchElementException("There are no elements left");
			}
		}
	}



	/**
	 * Creates a new instance of ElementsGetter object on this ArrayIndexedCollection and
	 * returns it.
	 * 
	 * @return new ElementsGetter object for this ArrayIndexedCollection
	 */
	@Override
	public ElementsGetter createElementsGetter() {
		return new ArrayElementsGetter(this);
	}
	
	
	
	
	
}
