package com.wittawat.wordseg.utils;

import java.util.Iterator;

/**
 * A synchronized iterator which supports multithreaded
 * access.
 * @author Wittawat Jitkrittum
 */
public class SynchronizedIterator<E> implements Iterator<E> {

    private Iterator<E> iterator;

    public SynchronizedIterator(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    public synchronized boolean hasNext() {
        return iterator.hasNext();
    }

    public synchronized E next() {
        return iterator.next();

    }

    public synchronized void remove() {
        iterator.remove();
    }
}
