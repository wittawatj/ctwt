package com.wittawat.wordseg.feature;

/**
 * A numerical attribute which has a value of
 * the difference of two specified attributes.
 * 
 * @author Wittawat Jitkrittum
 */
public class ValueDiffFU extends AbstractNumericalFeatureUnit {

    private final FeatureUnit fu1;
    private final FeatureUnit fu2;

    public ValueDiffFU(String attributeName, FeatureUnit fu1, FeatureUnit fu2) {
        super(attributeName);
        this.fu1 = fu1;
        this.fu2 = fu2;
    }

    public ValueDiffFU(FeatureUnit fu1, FeatureUnit fu2) {
        super("("+fu1.getAttributeName() + " - " + fu2.getAttributeName()+")");
        this.fu1 = fu1;
        this.fu2 = fu2;
    }

    public double getAttributeValue(int currentIndex) {
        double v1 = fu1.getAttributeValue(currentIndex);
        double v2 = fu2.getAttributeValue(currentIndex);
        double diff = v1 - v2;
        return diff;

    }
}
