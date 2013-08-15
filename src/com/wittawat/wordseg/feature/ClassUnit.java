package com.wittawat.wordseg.feature;

import com.wittawat.wordseg.data.BESTHiCharSource;
import weka.core.Attribute;

/**
 * A special kind of feature generator (similar to <code>FeatureUnit</code>) 
 * where another source is needed to provide the correct class labels.
 * Typicall, the source which provides labels would be from BEST corpus.
 *
 * @author Wittawat Jitkrittum
 */
public interface ClassUnit {

    /**The character is the end of a word.*/
    public static final String CLASS_E = "E";
    /**The character is not the end of a word.*/
    public static final String CLASS_I = "I";

    String[] getPossibleValues();

    Attribute getClassAttribute();

    /**Needed to be called before getClassValue( int currentIndex).*/
    void setHiCharSource(BESTHiCharSource charSource);

    BESTHiCharSource getHiCharSource();

    double getClassValue(int currentIndex);
}
