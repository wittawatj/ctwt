
package com.wittawat.wordseg.word;

/**
 * General purpose filter.
 * @author Wittawat Jitkrittum
 */
public interface Filter<E> {

     boolean accepts(E item);
}
