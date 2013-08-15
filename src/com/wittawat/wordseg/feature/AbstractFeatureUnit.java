package com.wittawat.wordseg.feature;

public abstract class AbstractFeatureUnit implements FeatureUnit {

    protected String attributeName;
    protected String charSource;

    public AbstractFeatureUnit(String attributeName) {

        this.attributeName = attributeName;
    }

    public String getAttributeName() {

        return attributeName == null ? getClass().getSimpleName()
                : attributeName;
    }

    @Override
    public String toString() {
        return getAttributeName();

    }

    public String getCharSource() {
        return charSource;
    }

    public void setCharSource(String content) {
        this.charSource = content;
    }

}
