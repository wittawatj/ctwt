package com.wittawat.wordseg.word;

import java.util.Map.Entry;

/**
 * A filter which accpets an N-gram which has at least
 * the specified frequency.
 *
 * @author Wittawat Jitkrittum
 */
public class MinFreqGramFilter implements Filter<Entry<WordSequence, Integer>> {

    private int minFreq;

    public MinFreqGramFilter(int minFreq) {
        if (minFreq <= 0) {
            throw new IllegalArgumentException("minFreq must be  > 0");
        }
        this.minFreq = minFreq;
    }

    public boolean accepts(Entry<WordSequence, Integer> item) {
        Integer v = item.getValue();
        return v != null && v >= minFreq;
    }
}
