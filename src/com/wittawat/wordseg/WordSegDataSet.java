package com.wittawat.wordseg;

import weka.core.Instances;

/**
 * A container of dataset for word segmentation.
 * Instance vectors and the source content are
 * contained.
 *
 * @author Wittawat Jitkrittum
 */
public interface WordSegDataSet {

    /**@return the plain string source used to generate the dataset.
     */
    String getStringSource();

    Instances getDataSet();

    /**@return the indices of the string source used to generate
    the instances in the dataset. The returned array has the same
    size as the the number of instances. May be null to denote
    that all indices are used.*/
    int[] getDataIndices();

    /**@return true if this dataset has labels. If false,
     * the class value will be -1.*/
    boolean isLabeled();
}
