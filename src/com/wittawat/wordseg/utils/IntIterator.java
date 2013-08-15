package com.wittawat.wordseg.utils;

import java.util.Iterator;

/**
 * A number iterator.
 * 
 * @author Wittawat Jitkrittum
 */
public class IntIterator implements Iterator<Integer> {

    private int from;
    private int to;
    private int increment;
    private int current;

    public IntIterator(int from, int to, int increment) {
        if (to < from) {
            throw new IllegalArgumentException("from must be <= to");
        }
        if (increment < 0) {
            throw new IllegalArgumentException("increment must be >= 0");
        }
        this.from = from;
        this.to = to;
        this.increment = increment;
        this.current = from;
    }

    public IntIterator(int from, int to) {
        this(from, to, 1);
    }

    public boolean hasNext() {

        return current <= to;
    }

    public Integer next() {
        int toReturn = current;
        current += increment;
        return toReturn;
    }

    public void remove() {
    }

    /////////////////
    public static Iterable<Integer> intIterator(final int from, final int to, final int increment) {
        return new Iterable<Integer>() {

            public Iterator<Integer> iterator() {
                return new IntIterator(from, to, increment);
            }
        };

    }

    public static Iterable<Integer> intIterator(final int from, final int to) {
        return intIterator(from, to, 1);
    }
}
