package com.wittawat.wordseg.utils;

/**
 * A Trie for fast word (key) lookup.
 * Each key maps to a user's object.
 * 
 * @author Wittawat Jitkrittum
 */
public abstract class Trie<E> {

    /**This object is used when a key is added with a null object.*/
    protected static final Nill NILL = new Nill();
    protected Object userObject = null;

    public Trie lookUpNode(String key) {
        Trie cur = this;
        for (int i = 0; i < key.length() && cur != null; ++i) {
            char ch = key.charAt(i);
            cur = cur.lookUpNode(ch);
        }
        return cur;
    }

    /**@return true if the key was added*/
    public boolean containsKey(String key) {
        Trie node = lookUpNode(key);
        return node != null && node.isKeyEnds();

    }

    /**@return the user object associated with the key*/
    public E get(String key) {
        Trie node = lookUpNode(key);
        if (node == null) {
            return null;
        }
        E toReturn = (E) (node.userObject == NILL ? null : node.userObject);
        return toReturn;
    }

    /**Add a key/value map to this Trie.
    @return the last node constructed.*/
    public Trie put(String key, E userObject) {
        if(key == null || key.equals("")){
            throw new IllegalArgumentException("key cannot be null ot blank string.");
        }
        Object obj = userObject == null ? NILL : userObject;
        Trie cur = this;
        for (int i = 0; i < key.length(); ++i) {
            char ch = key.charAt(i);
            cur = cur.addChildNode(ch);
        }
        cur.userObject = obj;
        return cur;

    }

    /**Identical to put(key, null).*/
    public Trie put(String key) {
        return put(key, null);
    }

    /**
     * @return the number of characters of the minimum
     * prefix of this word matched.
     *  */
    public int shortestMatch(String word) {
        Trie cur = this;
        for (int i = 0; i < word.length(); ++i) {
            char ch = word.charAt(i);
            cur = cur.lookUpNode(ch);
            if (cur == null) {
                return 0;
            }
            if (cur.isKeyEnds()) {
                return i + 1;
            }
        }
        return 0;

    }

    /**@return the number of characters of the maximum
    prefix of this word matched.*/
    public int longestMatch(String word) {
        Trie cur = this;
        int longestMatched = 0;
        for (int i = 0; i < word.length(); ++i) {
            char ch = word.charAt(i);
            cur = cur.lookUpNode(ch);
            if (cur == null) {
                return longestMatched;
            }
            if (cur.isKeyEnds()) {
                longestMatched = i + 1;
            }
        }
        return longestMatched;
    }

    public E getUserObject() {
        return (E) userObject;
    }

    public void setUserObject(E userObject) {
        this.userObject = userObject;

    }

    /**@return true if there exists a key which ends at
    this node.*/
    public  boolean isKeyEnds() {
        return userObject != null;
    }

    /**@return the next node in the Trie given the
     * character or null if not exists. */
    public abstract Trie lookUpNode(char ch);

    /**Add a new child node or do nothing if
     * the node already exists.
    @return the child node.*/
    protected abstract Trie addChildNode(char ch);

    /////////////////////////////////////////
    /////////////////////////////////////////
    /**A class with the solw purpose to represent
    a "nothing" meaning.*/
    protected static class Nill {

        private static String ERR = "This should not be called.";

        protected Nill() {
        }

       
    }
}
