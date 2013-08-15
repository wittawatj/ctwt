package com.wittawat.wordseg.word;

/**
 * A table which maps a sequence into its frequency.
 *
 * @author Wittawat Jitkrittum
 */
public interface GramTable<T> {

    Integer get(Sequence<T> seq);

    /**@return the sum of all sequences with the size
    = 1.*/
    int sumUnigramFreq();
}
