package com.wittawat.wordseg.word;

/**
 * WordFilter which always answer either true or false.
 * 
 * @author Wittawat Jitkrittum
 */
public class AllWordFilter<E> implements Filter<E> {

    public static final AllWordFilter ACCEPT_ALL_FILTER = new AllWordFilter(true);
    public static final AllWordFilter ACCEPT_NOTHING_FILTER = new AllWordFilter(false);
    private boolean answer;

    public AllWordFilter(boolean answer) {
        this.answer = answer;
    }

    public boolean accepts(E word) {
        return answer;
    }
}
