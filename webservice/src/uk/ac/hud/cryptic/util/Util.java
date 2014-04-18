package uk.ac.hud.cryptic.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;

public class Util {

	/**
	 * Add a value to the collection which is contained in a key-value mapping
	 * in a map object. If the collection already exists, add to it. If the
	 * collection does not exist, create a new instance of the specified
	 * collection class and add this to the map with the passed value.
	 * 
	 * @param map
	 *            - the map to add the value to
	 * @param key
	 *            - the key the value should reside under
	 * @param value
	 *            - the value to add
	 * @param collection
	 *            - the collection which will contained the passed value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V, C extends Collection<V>> void addToMap(Map<K, C> map,
			K key, V value, Class<? extends Collection> collection) {
		// Map might already have an entry for the specified key
		if (map.containsKey(key)) {
			// In which case simply add the new value to the collection
			map.get(key).add(value);
		} else {
			// Otherwise we need to create a new collection of the type
			// specified
			try {
				// Use reflection to get an instance of the specified collection
				Constructor<?> constructor = collection.getConstructor();
				C c = (C) constructor.newInstance();
				// Add the new value to it
				c.add(value);
				// Add it to the map
				map.put(key, c);
			} catch (NoSuchMethodException | SecurityException
					| InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a collection of values to the collection which is contained in a
	 * key-value mapping in a map object. If the collection already exists, add
	 * to it. If the collection does not exist, create a new instance of the
	 * specified collection class and add this to the map with the passed
	 * values.
	 * 
	 * @param map
	 *            - the map to add the value to
	 * @param key
	 *            - the key the value should reside under
	 * @param value
	 *            - the collection of values to add
	 * @param collection
	 *            - the collection which will contained the passed value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <K, V, C extends Collection<V>> void addAllToMap(
			Map<K, C> map, K key, C values,
			Class<? extends Collection> collection) {
		// Map might already have an entry for the specified key
		if (map.containsKey(key)) {
			// In which case simply add the new value to the collection
			map.get(key).addAll(values);
		} else {
			// Otherwise we need to create a new collection of the type
			// specified
			try {
				// Use reflection to get an instance of the specified collection
				Constructor<?> constructor = collection.getConstructor();
				C c = (C) constructor.newInstance();
				// Add the new value to it
				c.addAll(values);
				// Add it to the map
				map.put(key, c);
			} catch (NoSuchMethodException | SecurityException
					| InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

} // End of class Util
