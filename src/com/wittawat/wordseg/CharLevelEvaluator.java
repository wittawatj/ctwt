package com.wittawat.wordseg;

import com.wittawat.wordseg.data.ContentContainer;
import java.util.Map;

/**
 * A character evaluator which consider one character by one to determine whether the
 * character being considered should be a word boundary (or other classes).
 * 
 * @author nook
 */
public interface CharLevelEvaluator {

    /**Get the string source to evaluate. Only evaluate
    at the locations marked.*/
    ContentContainer getSource();

    void setSource(ContentContainer content);

    /**@return the class values which correspond to
    the class probabilities values returned by
    getClassProbabilites()*/
    String[] getClassValues();

    /**@return a map of each marked character position to its
    probabilities. In order to find the meaning of each probability,
    <code>ClassFU</code> used to by the model is needed.*/
    Map<Integer, double[]> getClassProbabilites();
}
