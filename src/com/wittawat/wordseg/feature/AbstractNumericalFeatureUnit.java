package com.wittawat.wordseg.feature;

import weka.core.Attribute;

public abstract class AbstractNumericalFeatureUnit extends AbstractFeatureUnit {

    protected Attribute attribute;

    public AbstractNumericalFeatureUnit(String attributeName) {
        super(attributeName);

    }

    @Override
    public Attribute getAttribute() {
        if (attribute == null) {
            attribute = new Attribute(getAttributeName());
        }
        return attribute;
    }

    public Object getRawAttributeValue(int currentIndex) {
        // For the case of numerical features,
        // getRawAttributeValue and getAttributeValue should be the same
        return getAttributeValue(currentIndex);
    }
}
