package com.wittawat.wordseg.word;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class OrFilter<E> implements Filter<E> {

    private Filter<E>[] filters;

    public OrFilter(Filter<E>... filters) {
        this.filters = filters;
    }

    public boolean accepts(E item) {
        for (Filter<E> filter : filters) {
            if (filter.accepts(item)) {
                return true;
            }
        }
        return false;
    }
}
