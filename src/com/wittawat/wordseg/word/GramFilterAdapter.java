package com.wittawat.wordseg.word;

import java.util.Map.Entry;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class GramFilterAdapter implements Filter<Entry<WordSequence, Integer>> {

    private WordSequenceFilter sequenceFilter;

    public GramFilterAdapter(WordSequenceFilter sequenceFilter) {
        this.sequenceFilter = sequenceFilter;
    }

    public boolean accepts(Entry<WordSequence, Integer> item) {
        WordSequence sequence = item.getKey();
        return sequenceFilter.accepts(sequence);
    }
}
