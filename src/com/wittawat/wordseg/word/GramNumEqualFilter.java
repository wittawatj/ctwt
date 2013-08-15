package com.wittawat.wordseg.word;

/**
 * Filter only grams which contain exactly specified n grams.
 * 
 * @author Wittawat Jitkrittum
 */
public class GramNumEqualFilter implements WordSequenceFilter {

    private int num;

    public GramNumEqualFilter(int num) {
        if (num <= 0) {
            throw new IllegalArgumentException("num must be > 0");
        }
        this.num = num;
    }

    public boolean accepts(WordSequence item) {
        return item.getSequence().length == num;
    }
}
