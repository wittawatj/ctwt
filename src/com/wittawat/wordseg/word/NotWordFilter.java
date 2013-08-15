package com.wittawat.wordseg.word;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class NotWordFilter implements Filter<String> {

    private Filter filter;

    public NotWordFilter(Filter filter) {
        this.filter = filter;
    }

    public boolean accepts(String word) {
        return !filter.accepts(word);
    }
}
