package com.wittawat.wordseg.feature;

import weka.core.Attribute;

/**
 * A feature generator for one feature (attribute) in an instance. An instance
 * is constructed for each character read.
 * 
 * @author nook
 */
public interface FeatureUnit extends CharSourceBased {

    /**@return the attribute definition of this feature unit.*/
    Attribute getAttribute();

    /**@return the string character source from which this Feature
    unit will generate feature.*/
    String getCharSource();

    double getAttributeValue(int currentIndex);

    /**@return the raw representational value. Can be used with
    <code>SourcableModel</code>.*/
    Object getRawAttributeValue(int currentIndex);

    String getAttributeName();
}
