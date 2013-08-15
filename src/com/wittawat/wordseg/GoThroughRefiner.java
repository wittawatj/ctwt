package com.wittawat.wordseg;

/**
 * Dummy <code>ResultRefiner</code> which does nothing
 * but let the input go as the output.
 * 
 * @author Wittawat Jitkrittum
 */
public class GoThroughRefiner implements ResultRefiner {

    public GoThroughRefiner() {
    }

    public TokResult refine(TokResult tokResult) {
        return tokResult;
    }
}
