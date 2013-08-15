package com.wittawat.wordseg.word;

/**
 * A <code>SequenceFilter</code> which accepts a sequence
 * if all words inside the sequence have at most the specified
 * length.
 * 
 * @author Wittawat Jitkrittum
 */
public class MaxWordLengthFilter implements WordSequenceFilter {

    private int maxWordLength;

    public MaxWordLengthFilter(int maxWordLength) {
        if (maxWordLength <= 0) {
            throw new IllegalArgumentException("maxWordLength must be > 0");
        }
        this.maxWordLength = maxWordLength;
    }

    public boolean accepts(WordSequence item) {
        for (String w : item.getSequence()) {
            if (w.length() > maxWordLength) {
                return false;
            }
        }
        return true;
    }
}
