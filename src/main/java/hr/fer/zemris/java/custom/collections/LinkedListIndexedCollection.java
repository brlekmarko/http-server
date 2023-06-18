package hr.fer.zemris.java.custom.collections;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;


/**
 * Represents a linked list-backed collection of objects
 * Duplicate elements are allowed; storage of null references is not allowed
 * 
 * @author Marko Brlek
 *
 */
public class LinkedListIndexedCollection implements List{
	
	private static class ListNode{
		
		public ListNode previous;
		public ListNode next;
		public Object value;
		
		
		public ListNode(Object value) {
			this.previous = null;
			this.next = null;
			this.value = value;
		}
		
		public ListNode(Object value, ListNode previous) {
			this.previous = previous;
			this.next = null;
			this.value = value;
		}
	}
	
	private int size;
	private ListNode first;
	private ListNode last;
	private long modificationCount = 0;
	
	
	
	/**
	 * Creates an instance of LinkedListIndexedCollection
	 * Sets size to zero because list is empty
	 * First and last nodes are null because list is empty
	 */
	public LinkedListIndexedCollection() {
		size = 0;
		first = null;
		last = null;
	}
	
	
	
	/**
	 * Creates an instance of LinkedListIndexedCollection
	 * Method addAll takes care of first and last nodes and size
	 * 
	 * @param other  collection whose elements we copy to this collection
	 */
	public LinkedListIndexedCollection(Collection other) {
		
		this.addAll(other);
	}
	
	
	
	/**
	 * Adds the given object into this collection at the end of collection; 
	 * newly added element becomes the element at the biggest index
	 * 
	 * @param value  element which we want to add to collection
	 * @throws NullPointerException  if parameter value is null
	 */
	@Override
	public void add(Object value) {
		if(value == null) {
			throw new NullPointerException("Can't add null to this collection");
		}
		else if(this.size == 0) {
			this.first = new ListNode(value);  //list is empty so we create the first node
			this.last = this.first;   //first node is also the last node
			this.size++;
			this.modificationCount++;
		}
		else {
			ListNode newNode = new ListNode(value, this.last);  //list is not empty so we create new node and attach it to the last
			this.last.next = newNode;  //so the last one knows a new one exists
			this.last = newNode;  //the new one is now the last one
			this.size++;
			this.modificationCount++;
		}
	}
	
	
	
	/**
	 * Returns the object that is stored in linked list at position index.
	 * 
	 * @param index  which element we want to get, valid indexes are 0 to size-1
	 * @return object on given index
	 * @throws IndexOutOfBoundsException  if index is invalid
	 */
	@Override
	public Object get(int index) {
		if(index>=0 && index<=this.size-1) {
			if(index < this.size/2) { //start from front
				ListNode currentNode = this.first;
				for(int i=0; i<index; i++) {
					currentNode = currentNode.next;
				}
				return currentNode.value;
			}
			else { //start from back
				ListNode currentNode = this.last;
				for(int i=this.size-1; i>index; i--) {
					currentNode = currentNode.previous;
				}
				return currentNode.value;
			}
		}
		else {
			throw new IndexOutOfBoundsException("Given index is invalid.");
		}
		
		//complexity is never greater than O(n/2+1) because we start at the side closer to the index
	}
	
	
	
	/**
	 * Removes all elements from the collection. Collection “forgets” about current linked list.
	 */
	@Override
	public void clear() {
		this.first = null;
		this.last = null;
		this.size = 0;
		this.modificationCount++;
	}
	
	
	
	/**
	 * Inserts (does not overwrite) the given value at the given position in linked-list. 
	 * Elements starting from this position are shifted one position
	 * 
	 * @param value  Object which we want to insert into the collection
	 * @param position  Index at which we want to insert the Object, legal positions are 0 to size
	 * 
	 * @throws NullPointerException  when value is null
	 * @throws IndexOutOfBoundsException  when position is invalid
	 */
	@Override
	public void insert(Object value, int position) {
		
		if(value == null) {
			throw new NullPointerException("Can't add null to this collection");
		}
		else if(position<0 || position>this.size){
			throw new IndexOutOfBoundsException("Given index is invalid.");
		}
		else if(position == 0) {  //new node is also the first node
			ListNode newNode = new ListNode(value);
			newNode.next = this.first;
			this.first = newNode;
			if(this.size == 0) {
				this.last = newNode;  //if it was empty it is now also the last node
			}
			this.size++;
			this.modificationCount++;
		}
		else if(position == this.size) {  //treat it as a normal add because it goes to the end
			this.add(value);
		}
		else {  //go through nodes till we get the one at index position
			ListNode currentNode;
			if(position < this.size/2) { //start from front
				currentNode = this.first;
				for(int i=0; i<position; i++) {
					currentNode = currentNode.next;
				}
			}
			else { //start from back
				currentNode = this.last;
				for(int i=this.size-1; i>position; i--) {
					currentNode = currentNode.previous;
				}
			}
			//reattach the nodes so the new node is in between the currentNode and currentNode's previous node
			ListNode newNode = new ListNode(value, currentNode.previous);
			newNode.next = currentNode;
			currentNode.previous.next = newNode;
			currentNode.previous = newNode;
			this.size++;
			this.modificationCount++;
				
		}
		
		//maximum complexity is n/2+1 because we have to jump nodes until we get to the one at position
		//reattaching nodes is O(1)
		//average complexity is n/2/2 = n/4
	}
	
	
	
	/**
	 * Searches the collection and returns the index of the first occurrence of the given value 
	 * or -1 if the value is not found
	 * 
	 * 
	 * @param value  Object for which we are looking for
	 * @return Index of first occurrence of the given value, or -1 if not found
	 */
	@Override
	public int indexOf(Object value) {
		if(value == null) return -1;
		ListNode currentNode = this.first;
		int counter = 0;
		if(currentNode.value.equals(value)) return counter;  //initial check because we immediately jump onto next one
		while (currentNode.next != null) {
			currentNode = currentNode.next;
			counter++;
			if(currentNode.value.equals(value)) {
				return counter;
			}
		}
		return -1;
		
	}
	
	
	
	/**
	 * Removes element at specified index from collection. 
	 * Element that was previously at location index+1 after this operation is on location index, etc
	 * 
	 * 
	 * @param index  Position at which we want to remove the element, legal indexes are 0 to size-1
	 * @throws IndexOutOfBoundsException  if index is invalid.
	 */
	@Override
	public void remove(int index) {
		if(index<0 || index>size-1) {
			throw new IndexOutOfBoundsException("Given index is invalid.");
		}
		else if(this.size == 1) {  //if there was only 1 element, list is now empty
			this.first = null;
			this.last = null;
			this.size--;
			this.modificationCount++;
		}
		else if(index == 0) {  //if we remove the first element, this.first.next is now the first element
			this.first.next.previous = null;
			this.first = this.first.next;
			this.size--;
			this.modificationCount++;
		}
		else if(index == size-1) {  //if we remove the last element, this.last.previous is now the last element
			this.last.previous.next = null;
			this.last = this.last.previous;
			this.size--;
			this.modificationCount++;
		}
		else {   //go through nodes till we get the one at index position
			ListNode currentNode;
			if(index < this.size/2) { //start from front
				currentNode = this.first;
				for(int i=0; i<index; i++) {
					currentNode = currentNode.next;
				}
			}
			else { //start from back
				currentNode = this.last;
				for(int i=this.size-1; i>index; i--) {
					currentNode = currentNode.previous;
				}
			}
			//reattach the nodes so the currentNode.previous and currentNode.next are connected
			currentNode.previous.next = currentNode.next;
			currentNode.next.previous = currentNode.previous;
			this.size--;
			this.modificationCount++;
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
		ListNode currentNode = this.first;
		int counter = 0;
		
		while(currentNode != null) {
			toReturn[counter] = currentNode.value;  //fill it with collection content
			currentNode = currentNode.next;
			counter++;
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
		ListNode currentNode = this.first;
		
		while(currentNode != null) {
			toReturn += currentNode.value.toString() + ";;";
			currentNode = currentNode.next;
		}
		return toReturn;
	}
	
	
	
	/**
	 * Implementation of ElementsGetter interface. Used to iterate over an LinkedListIndexedCollection object.
	 * 
	 * 
	 * @author Marko Brlek
	 */
	private static class LinkedListElementsGetter implements ElementsGetter{
		
		
		private LinkedListIndexedCollection sentCollection;
		
		private ListNode currentNode;
		
		private long savedModificationCount;
		
			
		
		/**
		 * The only constructor. The LinkedListIndexedCollection over which we want to iterate is sent.
		 * 
		 * @param collection  LinkedListIndexedCollection over which we want to iterate
		 */
		public LinkedListElementsGetter(LinkedListIndexedCollection collection) {
			super();
			this.sentCollection = collection;
			this.currentNode = collection.first;
			this.savedModificationCount = collection.modificationCount;
		}
		

		
		/**
		 * Checks if there are elements in LinkedListIndexedCollection which have not
		 * been sent out yet.
		 * 
		 * @return true if there are some elements left to send out, false otherwise
		 * @throws ConcurrentModificationException if method is called after the LinkedListIndexedCollection was changed
		 */
		@Override
		public boolean hasNextElement() {
			if(this.savedModificationCount != this.sentCollection.modificationCount) {
				throw new ConcurrentModificationException("Can't check because collection was recently changed.");
			}
			else {
				return this.currentNode != null;
			}
		}
		
		
		
		/**
		 * Returns the first element in the LinkedListIndexedCollection which hasn't been sent out yet.
		 * If every element has been sent out and this method is called, throw NoSuchElementException
		 * 
		 * @return first element in the LinkedListIndexedCollection which hasn't been sent out yet
		 * @throws NoSuchElementException if there are no elements left to get, meaning hasNextElement() is false
		 * @throws ConcurrentModificationException if method is called after the LinkedListIndexedCollection was changed
		 */
		@Override
		public Object getNextElement() throws NoSuchElementException {
			if(this.savedModificationCount != this.sentCollection.modificationCount) {
				throw new ConcurrentModificationException("Can't get element because collection was recently changed.");
			}
			else {
				if (this.hasNextElement()) {
					
					Object toReturn = this.currentNode.value;
					
					this.currentNode = this.currentNode.next;
					
					return toReturn;
				}
				throw new NoSuchElementException("There are no elements left");
			}
		}
	}



	/**
	 * Creates a new instance of ElementsGetter object on this LinkedListIndexedCollection and
	 * returns it.
	 * 
	 * @return new ElementsGetter object for this LinkedListIndexedCollection
	 */
	@Override
	public ElementsGetter createElementsGetter() {
		return new LinkedListElementsGetter(this);
	}
}
