package hr.fer.zemris.java.custom.collections;

public interface List extends Collection{
	
	
	/**
	 * Returns the object that is stored in Collection at position index.
	 * 
	 * @param index  which element we want to get, valid indexes are 0 to size-1
	 * @return object on given index
	 * @throws IndexOutOfBoundsException  if index is invalid
	 */
	Object get(int index) throws IndexOutOfBoundsException;
	
	
	
	/**
	 * Inserts (does not overwrite) the given value at the given position in Collection
	 * Shifts elements to the right so that an empty place is created at position
	 * 
	 * 
	 * @param value  Object which we want to insert in collection
	 * @param position  index at which we want to insert the object, valid positions are 0 to size (both included)
	 * @throws IndexOutOfBoundsException  if index is invalid
	 */
	void insert(Object value, int position) throws IndexOutOfBoundsException;
	
	
	/**
	 * Searches the Collection and returns the index of the first occurrence of the given value 
	 * or -1 if the value is not found
	 * 
	 * @param value  object for which we are looking for
	 * @return  -1 if the value is not found, otherwise index of the first occurrence of value 
	 */
	int indexOf(Object value);
	
	
	
	/**
	 * Removes element at specified index from Collection. Element that was previously at 
	 * location index+1 after this operation is on location index, etc. 
	 * 
	 * @param index  position at which we are removing the element, legal indexes are 0 to size-1
	 * @throws IndexOutOfBoundsException  if index is invalid
	 */
	void remove(int index) throws IndexOutOfBoundsException;
	

}
