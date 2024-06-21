package nz.ac.wgtn.swen225.lc.App;

/**
 * An Entry class representing a key-value pair for 
 * working with LHander to parse the level name and level file string.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 * 
 * @author greenthom.
 */
public class Entry<K, V> {
    private K k; // The key.
    private V v; // The value.

    /**
     * Gets the key of this entry.
     *
     * @return The key of the entry.
     */
    public K k() {
        return k;
    }

    /**
     * Gets the value of this entry.
     *
     * @return The value of the entry.
     */
    public V v() {
        return v;
    }

    /**
     * Sets the key of this entry.
     *
     * @param k The key to set.
     */
    public void setKey(K k) {
        this.k = k;
    }

    /**
     * Sets the value of this entry.
     *
     * @param v The value to set.
     */
    public void setValue(V v) {
        this.v = v;
    }

    /**
     * Constructs an Entry with the specified key and value.
     *
     * @param k The key of the entry.
     * @param v The value of the entry.
     */
    public Entry(K k, V v) {
        this.k = k;
        this.v = v;
    }
}
