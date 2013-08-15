package com.wittawat.wordseg.feature;

import com.wittawat.wordseg.data.BESTHiCharSource;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 * The class attribute <code>FeatureUnit</code>. Only two possible values exist,
 * E or I. E means the current character is the end of a word.
 * 
 * @author nook
 * */
public class ClassFU implements ClassUnit {

    private Attribute attribute;
    private FastVector possibleValues;
    private BESTHiCharSource charSource;

    public ClassFU(String attributeName) {
        possibleValues = new FastVector();
        for (String v : getPossibleValues()) {
            possibleValues.addElement(v);
        }
        attribute = new Attribute(attributeName, possibleValues);
    }

    public Attribute getClassAttribute() {
        return attribute;
    }

    public String[] getPossibleValues() {
        return new String[]{CLASS_E, CLASS_I};
    }

    public BESTHiCharSource getHiCharSource() {
        return charSource;

    }

    public void setHiCharSource(BESTHiCharSource charSource) {
        this.charSource = charSource;

    }

    public double getClassValue(int currentIndex) {
        if (charSource.isWordEnd(currentIndex)) {
            return possibleValues.indexOf(CLASS_E);
        }
        return possibleValues.indexOf(CLASS_I);
    }
}
