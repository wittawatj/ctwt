package com.wittawat.wordseg.word;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class DefaultGramTable<T> implements GramTable<T> {

    private Map<Sequence<T>, Integer> gramTable;
    private int unigramFreq = 0;

    public DefaultGramTable(Map<Sequence<T>, Integer> gramTable) {
        this.gramTable = new HashMap<Sequence<T>, Integer>(gramTable);


        for (Map.Entry<Sequence<T>, Integer> e : this.gramTable.entrySet()) {
            int gramSize = e.getKey().length();
            int freq = e.getValue();

            if (gramSize == 1) {
                unigramFreq += freq;
            }
        }

    }

    public Integer get(Sequence<T> seq) {
        return gramTable.get(seq);
    }

    public int sumUnigramFreq() {
        return unigramFreq;
    }
}
