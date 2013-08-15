package com.wittawat.wordseg.word;

/**
 * A generic sequence of items of class T.
 * It is recommended that T is immutable.
 *
 * @author Wittawat Jitkrittum
 */
public interface Sequence<T> {

    int length();

    T get(int i);

    /**@return a new sequence after adding the items.*/
    Sequence<T> add(Sequence<T> items);

    /**From, to indexes work like substring() of String class.*/
//    Sequence<T> subsequence(int fromInclusive, int toExclusive);


}
