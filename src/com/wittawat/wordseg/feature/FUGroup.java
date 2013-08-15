package com.wittawat.wordseg.feature;

import weka.core.Attribute;

/**
 * A group of <code>FeatureUnit</code>. Normally,
 * each attribute corresponds to one <code>FeatureUnit</code>.
 * However, there are many cases where the same unit is
 * used many times with only a minor difference e.g.
 * character index to consider (ex. ci or ci+1).
 *
 * In such case, it is more efficient to have a group.
 *
 * @author Wittawat Jitkrittum
 */
public interface FUGroup extends CharSourceBased {

    /**@return an array of attributes in this group.*/
//    Attribute[] getAttributes();

//    int getAttributeCount();

    /**
     * @return the value of attributes given the charSource and the index of
     *         the character current being read.
     */
//    double[] getAttributeValues(int currentIndex);

    /**@return a flat array of all feature units in this group.*/
    FeatureUnit[] getFeatureUnits();
}
