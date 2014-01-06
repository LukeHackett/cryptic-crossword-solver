package org.json.zip;

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
 * A TrieKeep is a Keep that implements a Trie.
 */
class TrieKeep extends Keep {

	/**
	 * The trie is made of nodes.
	 */
	class Node implements PostMortem {
		private int integer;
		private Node[] next;

		/**
		 * Each non-leaf node contains links to up to 256 next nodes. Each node
		 * has an integer value.
		 */
		public Node() {
			integer = none;
			next = null;
		}

		/**
		 * Get one of a node's 256 links. If it is a leaf node, it returns null.
		 * 
		 * @param cell
		 *            A integer between 0 and 255.
		 * @return
		 */
		public Node get(int cell) {
			return next == null ? null : next[cell];
		}

		/**
		 * Get one of a node's 256 links. If it is a leap node, it returns null.
		 * The argument is treated as an unsigned integer.
		 * 
		 * @param cell
		 *            A byte.
		 * @return
		 */
		public Node get(byte cell) {
			return get(cell & 0xFF);
		}

		/**
		 * Compare two nodes. Their lengths must be equal. Their links must also
		 * compare.
		 */
		@Override
		public boolean postMortem(PostMortem pm) {
			Node that = (Node) pm;
			if (that == null) {
				JSONzip.log("\nMisalign");
				return false;
			}
			if (integer != that.integer) {
				JSONzip.log("\nInteger " + integer + " <> " + that.integer);
				return false;
			}
			if (next == null) {
				if (that.next == null) {
					return true;
				}
				JSONzip.log("\nNext is null " + integer);
				return false;
			}
			for (int i = 0; i < 256; i += 1) {
				Node node = next[i];
				if (node != null) {
					if (!node.postMortem(that.next[i])) {
						return false;
					}
				} else if (that.next[i] != null) {
					JSONzip.log("\nMisalign " + i);
					return false;
				}
			}
			return true;
		}

		/**
		 * Set a node's link to another node.
		 * 
		 * @param cell
		 *            An integer between 0 and 255.
		 * @param node
		 *            The new value for the cell.
		 */
		public void set(int cell, Node node) {
			if (next == null) {
				next = new Node[256];
			}
			if (JSONzip.probe) {
				if (node == null || next[cell] != null) {
					JSONzip.log("\nUnexpected set.\n");
				}
			}
			next[cell] = node;
		}

		/**
		 * Set a node's link to another node.
		 * 
		 * @param cell
		 *            A byte.
		 * @param node
		 *            The new value for the cell.
		 */
		public void set(byte cell, Node node) {
			set(cell & 0xFF, node);
		}

		/**
		 * Get one of a node's 256 links. It will not return null. If there is
		 * no link, then a link is manufactured.
		 * 
		 * @param cell
		 *            A integer between 0 and 255.
		 * @return
		 */
		public Node vet(int cell) {
			Node node = get(cell);
			if (node == null) {
				node = new Node();
				set(cell, node);
			}
			return node;
		}

		/**
		 * Get one of a node's 256 links. It will not return null. If there is
		 * no link, then a link is manufactured.
		 * 
		 * @param cell
		 *            A byte.
		 * @return
		 */
		public Node vet(byte cell) {
			return vet(cell & 0xFF);
		}
	}

	private int[] froms;
	private int[] thrus;
	private Node root;
	private Kim[] kims;

	/**
	 * Create a new Keep of kims.
	 * 
	 * @param bits
	 *            The log2 of the capacity of the Keep. For example, if bits is
	 *            12, then the keep's capacity will be 4096.
	 */
	public TrieKeep(int bits) {
		super(bits);
		froms = new int[capacity];
		thrus = new int[capacity];
		kims = new Kim[capacity];
		root = new Node();
	}

	/**
	 * Get the kim associated with an integer.
	 * 
	 * @param integer
	 * @return
	 */
	public Kim kim(int integer) {
		Kim kim = kims[integer];
		int from = froms[integer];
		int thru = thrus[integer];
		if (from != 0 || thru != kim.length) {
			kim = new Kim(kim, from, thru);
			froms[integer] = 0;
			thrus[integer] = kim.length;
			kims[integer] = kim;
		}
		return kim;
	}

	/**
	 * Get the length of the Kim associated with an integer. This is sometimes
	 * much faster than get(integer).length.
	 * 
	 * @param integer
	 * @return
	 */
	public int length(int integer) {
		return thrus[integer] - froms[integer];
	}

	/**
	 * Find the integer value associated with this key, or nothing if this key
	 * is not in the keep.
	 * 
	 * @param key
	 *            An object.
	 * @return An integer
	 */
	public int match(Kim kim, int from, int thru) {
		Node node = root;
		int best = none;
		for (int at = from; at < thru; at += 1) {
			node = node.get(kim.get(at));
			if (node == null) {
				break;
			}
			if (node.integer != none) {
				best = node.integer;
			}
			from += 1;
		}
		return best;
	}

	@Override
	public boolean postMortem(PostMortem pm) {
		boolean result = true;
		TrieKeep that = (TrieKeep) pm;
		if (length != that.length) {
			JSONzip.log("\nLength " + length + " <> " + that.length);
			return false;
		}
		if (capacity != that.capacity) {
			JSONzip.log("\nCapacity " + capacity + " <> " + that.capacity);
			return false;
		}
		for (int i = 0; i < length; i += 1) {
			Kim thiskim = kim(i);
			Kim thatkim = that.kim(i);
			if (!thiskim.equals(thatkim)) {
				JSONzip.log("\n[" + i + "] " + thiskim + " <> " + thatkim);
				result = false;
			}
		}
		return result && root.postMortem(that.root);
	}

	public void registerMany(Kim kim) {
		int length = kim.length;
		int limit = capacity - this.length;
		if (limit > JSONzip.substringLimit) {
			limit = JSONzip.substringLimit;
		}
		int until = length - (JSONzip.minSubstringLength - 1);
		for (int from = 0; from < until; from += 1) {
			int len = length - from;
			if (len > JSONzip.maxSubstringLength) {
				len = JSONzip.maxSubstringLength;
			}
			len += from;
			Node node = root;
			for (int at = from; at < len; at += 1) {
				Node next = node.vet(kim.get(at));
				if (next.integer == none
						&& at - from >= JSONzip.minSubstringLength - 1) {
					next.integer = this.length;
					uses[this.length] = 1;
					kims[this.length] = kim;
					froms[this.length] = from;
					thrus[this.length] = at + 1;
					if (JSONzip.probe) {
						try {
							JSONzip.log("<<" + this.length + " "
									+ new Kim(kim, from, at + 1) + ">> ");
						} catch (Throwable ignore) {
						}
					}
					this.length += 1;
					limit -= 1;
					if (limit <= 0) {
						return;
					}
				}
				node = next;
			}
		}
	}

	public void registerOne(Kim kim) {
		int integer = registerOne(kim, 0, kim.length);
		if (integer != none) {
			kims[integer] = kim;
		}
	}

	public int registerOne(Kim kim, int from, int thru) {
		if (length < capacity) {
			Node node = root;
			for (int at = from; at < thru; at += 1) {
				node = node.vet(kim.get(at));
			}
			if (node.integer == none) {
				int integer = length;
				node.integer = integer;
				uses[integer] = 1;
				kims[integer] = kim;
				froms[integer] = from;
				thrus[integer] = thru;
				if (JSONzip.probe) {
					try {
						JSONzip.log("<<" + integer + " "
								+ new Kim(kim, from, thru) + ">> ");
					} catch (Throwable ignore) {
					}
				}
				length += 1;
				return integer;
			}
		}
		return none;
	}

	/**
	 * Reserve space in the keep, compacting if necessary. A keep may contain at
	 * most -capacity- elements. The keep contents can be reduced by deleting
	 * all elements with low use counts, rebuilding the trie with the survivors.
	 */
	public void reserve() {
		if (capacity - length < JSONzip.substringLimit) {
			int from = 0;
			int to = 0;
			root = new Node();
			while (from < capacity) {
				if (uses[from] > 1) {
					Kim kim = kims[from];
					int thru = thrus[from];
					Node node = root;
					for (int at = froms[from]; at < thru; at += 1) {
						Node next = node.vet(kim.get(at));
						node = next;
					}
					node.integer = to;
					uses[to] = age(uses[from]);
					froms[to] = froms[from];
					thrus[to] = thru;
					kims[to] = kim;
					to += 1;
				}
				from += 1;
			}

			// It is possible, but highly unlikely, that too many items survive.
			// If that happens, clear the keep.

			if (capacity - to < JSONzip.substringLimit) {
				power = 0;
				root = new Node();
				to = 0;
			}
			length = to;
			while (to < capacity) {
				uses[to] = 0;
				kims[to] = null;
				froms[to] = 0;
				thrus[to] = 0;
				to += 1;

			}
		}
	}

	@Override
	public Object value(int integer) {
		return kim(integer);
	}
}
