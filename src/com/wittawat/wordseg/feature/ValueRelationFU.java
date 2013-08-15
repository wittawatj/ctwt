package com.wittawat.wordseg.feature;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 * A nominal attribute which contains only
 * 2 possible values of {g,l}. g if the
 * specified first <code>FeatureUnit</code>
 * is greater than or equal to the specified
 * second <code>FeatureUnit</code>, l otherwise.
 *
 * Care must be taken to ensure that the specified
 * feature units are numerical.
 *
 * @author Wittawat Jitkrittum
 */
public class ValueRelationFU extends AbstractFeatureUnit {

    private Attribute attribute;
    protected static FastVector POSSIBLE_VALUES;
    public static final String VALUE_GEQ = "g";
    public static final String VALUE_LT = "l";

    static {
        // Initialize possible values;
        POSSIBLE_VALUES = new FastVector(2);
        POSSIBLE_VALUES.addElement(VALUE_GEQ);
        POSSIBLE_VALUES.addElement(VALUE_LT);
    }
    private final FeatureUnit fu1;
    private final FeatureUnit fu2;

    public ValueRelationFU(String attributeName, FeatureUnit fu1, FeatureUnit fu2) {
        super(attributeName);
        this.fu1 = fu1;
        this.fu2 = fu2;
    }

    public ValueRelationFU(FeatureUnit fu1, FeatureUnit fu2) {
        super("("+fu1.getAttributeName() + " >= " + fu2.getAttributeName()+")");
        this.fu1 = fu1;
        this.fu2 = fu2;
    }

    public Attribute getAttribute() {
        if (attribute == null) {
            assert POSSIBLE_VALUES != null;
            attribute = new Attribute(getAttributeName(), POSSIBLE_VALUES);

        }
        return attribute;

    }

    public double getAttributeValue(int currentIndex) {
        Object v = getRawAttributeValue(currentIndex);
        int index = POSSIBLE_VALUES.indexOf(v);
        assert index >= 0;
        return index;
    }

    public Object getRawAttributeValue(int currentIndex) {
        double fu1V = fu1.getAttributeValue(currentIndex);
        double fu2V = fu2.getAttributeValue(currentIndex);

        String v = fu1V >= fu2V ? VALUE_GEQ : VALUE_LT;
        return v;
    }
}
