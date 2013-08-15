package com.wittawat.wordseg;

import com.wittawat.wordseg.utils.Pair;
import java.util.List;

/**
 * A tokenization result with probabilities.
 * High prob => tokenize,
 * Low prob => not tokenize.
 * 
 * @author Wittawat Jitkrittum
 */
public class TokResult {

    private List<Pair<Integer, Double>> probIndexes;
    private String content;

    public TokResult(List<Pair<Integer, Double>> probIndexes, String content) {
        this.probIndexes = probIndexes;
        this.content = content;
    }

    /**@return a list of indexes and the corresponding probabilites
    of the content tokenized.*/
    public List<Pair<Integer, Double>> getProbIndexes() {
        return probIndexes;
    }

    /**@return the content tokenized.*/
    public String getContent() {
        return content;
    }
}
