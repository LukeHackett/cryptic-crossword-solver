package uk.ac.hud.cryptic.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * A cache to speed up common requests to find all matching elements to a given
 * pattern. Initial results indicate this speeds up Anagram solving in some
 * cases by >75%.
 * 
 * @author Stuart Leader
 * @version 0.2
 */
public class Cache {

	// The maximum number of elements that may be cached
	protected static final int MAX_CAPACITY = 1000;

	// Our cache object
	private Map<String, Collection<String>> cache;
	// Used to manage the capacity of the cache
	private Queue<String> keys;

	/**
	 * The one and only constructor
	 */
	public Cache() {
		// Initialise our objects
		cache = new HashMap<>();
		keys = new LinkedList<>();
	}

	/**
	 * A check to see if the cache contains a given solution pattern
	 * 
	 * @param key
	 *            - the solution pattern
	 * @return <code>true</code> if the cache contains the matches for the given
	 *         pattern, <code>false</code> otherwise
	 */
	public boolean containsKey(String key) {
		return cache.containsKey(key);
	}

	/**
	 * Add a new element to the cache. If the capacity is reached, the oldest
	 * cache element is removed (excluding the predefined items - these can
	 * stay)
	 * 
	 * @param key
	 *            - the key of the cache item
	 * @param value
	 *            - the value of the cache item
	 */
	public void put(String key, Collection<String> value) {
		// Pointless if the cache already contains the given key
		if (!cache.containsKey(key)) {
			// If capacity has been reached
			if (cache.size() >= MAX_CAPACITY) {
				// Remove the oldest cache element
				cache.remove(keys.poll());
			}
			// Add the new item
			cache.put(key, value);
			keys.add(key);
		}
	}

	/**
	 * Get the dictionary items which match the given key
	 * 
	 * @param key
	 *            - the key to retrieve the matching entries for
	 * @return the dictionary entries which match the supplied solution pattern
	 */
	public Collection<String> get(String key) {
		return cache.get(key);
	}

} // End of class Cache

