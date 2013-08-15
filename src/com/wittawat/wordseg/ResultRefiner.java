package com.wittawat.wordseg;

/**
 * A refiner of tokenization result.
 * Get a <code>TokResult</code> and produce another <code>TokResult</code>
 * where content is kept the same.
 * 
 * @author Wittawat Jitkrittum
 */
public interface ResultRefiner {

    TokResult refine(TokResult tokResult);
}
