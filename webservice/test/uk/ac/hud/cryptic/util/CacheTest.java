package uk.ac.hud.cryptic.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CacheTest {

	private static int capacity;
	private static Cache<String, String> cache;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		capacity = Cache.MAX_CAPACITY;
	}

	@Before
	public void setUp() throws Exception {
		cache = new Cache<>();
	}

	@After
	public void tearDown() throws Exception {
		cache = null;
	}

	@Test
	public void testContainsKey() {
		final String key = "key";
		final String value = "value";

		// Nothing there yet
		assertFalse(cache.containsKey(key));

		// Add to cache
		cache.put(key, value);

		// See if it's present
		assertTrue(cache.containsKey(key));

		// Add it again, should still be present
		cache.put(key, value);
		assertTrue(cache.containsKey(key));

		// Push the key out of the cache
		// Fill with other keys
		int i;
		for (i = 0; i < capacity; i++) {
			cache.put(key + i, value);
		}

		// Should now be missing
		assertFalse(cache.containsKey(key));
	}

	@Test
	public void testPut() {
		final String key = "key";
		final String value = "value";

		// Key not present, should return null
		assertNull(cache.get(key));

		// Key is present, should return it's value
		cache.put(key, value);
		assertEquals(value, cache.get(key));

		// Key has been "pushed" out of the cache, should return null
		// Fill with other keys
		int i;
		for (i = 0; i < capacity; i++) {
			cache.put(key + i, value);
		}

		// Lookup key which should now be gone
		assertNull(cache.get(key));

		// Finally, add it again and test its presence
		cache.put(key, value);
		assertEquals(value, cache.get(key));
	}

	@Test
	public void testPrePut() {
		final String key = "key";
		final String preput1 = "preput1";
		final String preput2 = "preput2";
		final String preput3 = "preput3";
		final String value = "value";

		// Add as "pre-put" items
		cache.prePut(preput1, value);
		cache.prePut(preput2, value);
		cache.prePut(preput3, value);

		// Check their presence
		assertTrue(cache.containsKey(preput1));
		assertTrue(cache.containsKey(preput2));
		assertTrue(cache.containsKey(preput3));

		// Fill the cache - shouldn't wipe these out unlike normal 'put'
		int i;
		for (i = 0; i < capacity; i++) {
			cache.put(key + i, value);
		}

		// Check their presence
		assertTrue(cache.containsKey(preput1));
		assertTrue(cache.containsKey(preput2));
		assertTrue(cache.containsKey(preput3));
	}

	@Test
	public void testGet() {
		// Not much point testing as this is just ConcurrentHashMap's get method
		final String key = "key";
		final String value = "value";

		// Not added yet
		assertNull(cache.get(key));

		// Add it
		cache.put(key, value);

		// Get it
		assertEquals(value, cache.get(key));
	}

} // End of class CacheTest
