package com.wittawat.wordseg;

import weka.core.Instances;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class WordSegDataSetImpl implements WordSegDataSet {

    private String stringSource;
    private Instances dataSet;
    private int[] dataIncides;
    private boolean trainingSet;

    public WordSegDataSetImpl(String stringSource, Instances dataSet, int[] dataIncides, boolean trainingSet) {
        this.stringSource = stringSource;
        this.dataSet = dataSet;
        this.dataIncides = dataIncides;
        this.trainingSet = trainingSet;
    }

    public int[] getDataIndices() {
        return dataIncides;
    }

    public Instances getDataSet() {
        return dataSet;
    }

    public String getStringSource() {
        return stringSource;
    }

    public boolean isLabeled() {
        return trainingSet;
    }
}
