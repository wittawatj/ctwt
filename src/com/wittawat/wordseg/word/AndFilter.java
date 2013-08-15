package com.wittawat.wordseg.word;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class AndFilter<E> implements Filter<E> {

    private List<Filter<E>> filters = new LinkedList<Filter<E>>();

    public AndFilter(Filter<E>... filters) {
        for(Filter<E> f : filters){
            addFilter(f);
        }
    }

    public boolean accepts(E item) {
        for (Filter<E> filter : filters) {
            if (!filter.accepts(item)) {
                return false;
            }
        }
        return true;
    }
    public AndFilter<E> addFilter(Filter<E> f){
        filters.add(f);
        return this;
    }
   
}
