package org.json.zip;

import java.util.HashMap;

import org.json.Kim;

/*
 * Copyright (c) 2013 JSON.org Permission is hereby granted, free of charge, to
 * any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit
 * persons to whom the Software is furnished to do so, subject to the following
 * conditions: The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software. The Software
 * shall be used for Good, not Evil. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * A keep is an associative data structure that maintains usage counts of each
 * of the associations in its keeping. When the keep becomes full, it purges
 * little used associations, and ages the survivors. Each key is assigned an
 * integer value. When the keep is compacted, each key can be given a new value.
 */
class MapKeep extends Keep {
	private Object[] list;
	private HashMap map;

	/**
	 * Create a new Keep.
	 * 
	 * @param bits
	 *            The capacity of the keep expressed in the number of bits
	 *            required to hold an integer.
	 */
	public MapKeep(int bits) {
		super(bits);
		list = new Object[capacity];
		map = new HashMap(capacity);
	}

	/**
	 * Compact the keep. A keep may contain at most this.capacity elements. The
	 * keep contents can be reduced by deleting all elements with low use
	 * counts, and by reducing the use counts of the survivors.
	 */
	private void compact() {
		int from = 0;
		int to = 0;
		while (from < capacity) {
			Object key = list[from];
			long usage = age(uses[from]);
			if (usage > 0) {
				uses[to] = usage;
				list[to] = key;
				map.put(key, new Integer(to));
				to += 1;
			} else {
				map.remove(key);
			}
			from += 1;
		}
		if (to < capacity) {
			length = to;
		} else {
			map.clear();
			length = 0;
		}
		power = 0;
	}

	/**
	 * Find the integer value associated with this key, or nothing if this key
	 * is not in the keep.
	 * 
	 * @param key
	 *            An object.
	 * @return An integer
	 */
	public int find(Object key) {
		Object o = map.get(key);
		return o instanceof Integer ? ((Integer) o).intValue() : none;
	}

	@Override
	public boolean postMortem(PostMortem pm) {
		MapKeep that = (MapKeep) pm;
		if (length != that.length) {
			JSONzip.log(length + " <> " + that.length);
			return false;
		}
		for (int i = 0; i < length; i += 1) {
			boolean b;
			if (list[i] instanceof Kim) {
				b = ((Kim) list[i]).equals(that.list[i]);
			} else {
				Object o = list[i];
				Object q = that.list[i];
				if (o instanceof Number) {
					o = o.toString();
				}
				if (q instanceof Number) {
					q = q.toString();
				}
				b = o.equals(q);
			}
			if (!b) {
				JSONzip.log("\n[" + i + "]\n " + list[i] + "\n " + that.list[i]
						+ "\n " + uses[i] + "\n " + that.uses[i]);
				return false;
			}
		}
		return true;
	}

	/**
	 * Register a value in the keep. Compact the keep if it is full. The next
	 * time this value is encountered, its integer can be sent instead.
	 * 
	 * @param value
	 *            A value.
	 */
	public void register(Object value) {
		if (JSONzip.probe) {
			int integer = find(value);
			if (integer >= 0) {
				JSONzip.log("\nDuplicate key " + value);
			}
		}
		if (length >= capacity) {
			compact();
		}
		list[length] = value;
		map.put(value, new Integer(length));
		uses[length] = 1;
		if (JSONzip.probe) {
			JSONzip.log("<" + length + " " + value + "> ");
		}
		length += 1;
	}

	/**
	 * Return the value associated with the integer.
	 * 
	 * @param integer
	 *            The number of an item in the keep.
	 * @return The value.
	 */
	@Override
	public Object value(int integer) {
		return list[integer];
	}
}
