package hr.fer.zemris.java.custom.scripting.exec;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marko Brlek
 * 
 * Class that represents a map of stacks. It can store multiple values for a single key.
 * Each key has its own stack
 */
public class ObjectMultistack {

	Map<String, MultistackEntry> map = new HashMap<String, MultistackEntry>();


	
	/**
	 * Pushes a new value to the stack, or creates a new stack if the key does not exist
	 * @param keyName - name of the key
	 * @param valueWrapper - value to be added to the stack
	 */
	public void push(String keyName, ValueWrapper valueWrapper) {
		// If the key already exists, add the new entry to the top of the stack
		if(map.containsKey(keyName)) {
			MultistackEntry entry = map.get(keyName);
			// New entry is created with the old entry as the next entry
			// New entry is now the top of the stack
			MultistackEntry newEntry = new MultistackEntry(valueWrapper, entry);
			map.put(keyName, newEntry);
		// If the key does not exist, create a new entry and add it to the map
		} else {
			MultistackEntry newEntry = new MultistackEntry(valueWrapper);
			map.put(keyName, newEntry);
		}
	}
	
	
	/**
	 * Removes the top value from the stack
	 * If the stack would be left empty, removes the key from the map
	 * @param keyName - name of the key
	 * @return - value from the top of the stack
	 * @throws IllegalArgumentException - if the key does not exist
	 */
	public ValueWrapper pop(String keyName) {
		if(map.containsKey(keyName)) {
			MultistackEntry entry = map.get(keyName);
			// If the entry is the only entry in the stack, remove the key from the map
			if(entry.getNext() == null) {
				map.remove(keyName);
			// If the entry is not the only entry in the stack, set the next entry as the top of the stack
			} else {
				map.put(keyName, entry.getNext());
			}
			return entry.getValue();
		} else {
			throw new IllegalArgumentException("Key does not exist");
		}
	}
	
	
	/**
	 * Returns the value from the top of the stack, but does not remove it
	 * @param keyName - name of the key
	 * @return - value from the top of the stack
	 * @throws IllegalArgumentException - if the key does not exist
	 */
	public ValueWrapper peek(String keyName) {
		if(map.containsKey(keyName)) {
			MultistackEntry entry = map.get(keyName);
			return entry.getValue();
		} else {
			throw new IllegalArgumentException("Key does not exist");
		}
		
	}
	
	/**
	 * @param keyName
	 * @return
	 */
	public boolean isEmpty(String keyName) {
		// We can do this because in pop we remove the key if the stack is empty
		return !map.containsKey(keyName);
	}




}
