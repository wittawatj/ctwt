package com.wittawat.wordseg.feature;

/**
 * A dummy <code>FUGroup</code> which just
 * contains many <code>FeatureUnit</code> of choice.
 * @author Wittawat Jitkrittum
 */
public class ContainerFUGroup extends AbstractFUGroup {

    public FeatureUnit[] featureUnits;

    public ContainerFUGroup(FeatureUnit... featureUnits) {
        this.featureUnits = featureUnits;
    }

    public FeatureUnit[] getFeatureUnits() {
        return featureUnits;

    }
}
