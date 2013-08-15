package com.wittawat.wordseg.word;

/**
 * Filter only words which are longer than some
 * specified threshold.
 * @author Wittawat Jitkrittum
 */
public class WordAtLeastLengthFilter implements Filter<String> {

    private int atLeast;

    public WordAtLeastLengthFilter(int atLeast) {
        this.atLeast = atLeast;
    }

    public boolean accepts(String item) {
        return item.length() >= atLeast;
    }
}
