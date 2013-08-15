package com.wittawat.wordseg.utils;

import com.wittawat.wordseg.data.BESTHiCharSource;

/**
 * An evaluator to give many statistics by
 * comparing a segmentation result with a specified
 * baseline.
 *
 * Can evaluate by scoring NEs or not scoring NEs.
 * 
 * @author Wittawat Jitkrittum
 */
public class WordSegEvaluator {

    private BESTHiCharSource baseLine;
    private String result;
    private String delimiter = "|";
    /**If false, then errors from NEs are ignored. This is bias but useful in experiment.*/
    private boolean scoreNamedEntity = true;

    private Integer countCSW, countACW, countSW;

    public WordSegEvaluator(BESTHiCharSource baseLine, String result) {
        this.baseLine = baseLine;
        this.result = result;

    }
    public int countCorrectlySegmentedWords(){
        throw new UnsupportedOperationException();
    }
    public int countAllCorrectWords(){
        throw new UnsupportedOperationException();
    }
    public int countSegmentedWords(){
        throw new UnsupportedOperationException();
    }

    public double getRecall(){
        return (double)countCorrectlySegmentedWords() / (double) countAllCorrectWords();
    }
    public double getPrecision(){
        return (double)countCorrectlySegmentedWords() / (double) countSegmentedWords();
    }
    public double getFMeasure(){
        double r = getRecall();
        double p = getPrecision();
        double f = 2*r*p / (r + p);
        return f;
    }
    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isScoreNamedEntity() {
        return scoreNamedEntity;
    }

    public void setScoreNamedEntity(boolean scoreNamedEntity) {
        this.scoreNamedEntity = scoreNamedEntity;
    }
}
