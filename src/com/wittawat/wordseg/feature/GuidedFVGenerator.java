package com.wittawat.wordseg.feature;

import com.wittawat.wordseg.*;
import com.wittawat.wordseg.data.BESTHiCharSource;
import com.wittawat.wordseg.utils.IntArrayIterator;
import com.wittawat.wordseg.utils.IntIterator;
import java.util.Iterator;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A <code>FVGenerator</code> which generates dataset
 * based on the specifiable character indices, class values
 * of different sources.
 * 
 * @author Wittawat Jitkrittum
 */
public class GuidedFVGenerator extends AbstractFVGenerator {

//    public static final GuidedFVGenerator INSTANCE = new GuidedFVGenerator();
    /**Needed for producing unlabeled dataset*/
    protected String plainTextSource;
    /**Needed for producing labeled dataset.*/
    protected BESTHiCharSource hiCharSource;

    public GuidedFVGenerator() {
    
    }

    public GuidedFVGenerator(FeatureUnit[] featureUnits) {
        super(featureUnits);
    }

    private boolean verifyBeforeProduceDataSet() {

        if (featureUnits == null || featureUnits.length == 0) {
            throw new IllegalStateException("featureUnits must be set before producing a dataset.");
        }

        return true;
    }

    protected double[] newFeatureVector(int charIndex) {
        double[] vector = new double[featureUnits.length + 1]; // 1 for class attribute
        for (int i = 0; i < vector.length - 1; ++i) { //exclude class attribute
            vector[i] = featureUnits[i].getAttributeValue(
                    charIndex);
        }
        return vector;
    }

    private int getClassIndex() {
        return featureUnits.length;
    }

    private Iterator<Integer> newContentIndexIterator(int charLength) {
        Iterator<Integer> indexes =
                instanceIndexes == null ? new IntIterator(0, charLength - 1) : new IntArrayIterator(instanceIndexes);

        return indexes;
    }

    private Instances newBlankDataSet() {
        Instances data = new Instances(getDataSetName(), getAttributes(), 0);
        data.setClassIndex(getClassIndex());
        return data;

    }

    public void setHiCharSource(BESTHiCharSource hiCharSource) {
        this.hiCharSource = hiCharSource;
        String content = hiCharSource.getPlainText();
        this.plainTextSource = content;
        setCharSourceToFeatureUnits(content);

        this.classUnit.setHiCharSource(hiCharSource);
    }

    public void setStringSource(String plainTextSource) {
        // Cached
        if (this.plainTextSource == null || !this.plainTextSource.equals(plainTextSource)) {
            this.plainTextSource = plainTextSource;
            setCharSourceToFeatureUnits(plainTextSource);
        }

    }

    public WordSegDataSet produceUnlabeledDataSet() {
        if (verifyBeforeProduceDataSet()) {
            if (plainTextSource != null) {
                int length = plainTextSource.length();
                Iterator<Integer> indexes =
                        newContentIndexIterator(length);

                // Construct Instances
                Instances dataSet = newBlankDataSet();
                while (indexes.hasNext()) {
                    int charIndex = indexes.next();
                    double[] vector = newFeatureVector(charIndex);
                    int classIndex = getClassIndex();
                    vector[classIndex] = -1;
                    dataSet.add(new Instance(1.0, vector));
                }


                // Construct WordSegDataSet
                WordSegDataSet wsDataSet = new WordSegDataSetImpl(
                        plainTextSource,
                        dataSet,
                        instanceIndexes,
                        false);
                return wsDataSet;
            } else {
                throw new RuntimeException("plainTextSource must be produced before unlabeled dataset can be produced.");
            }
        }
        return null;

    }

    public WordSegDataSet produceLabeledDataSet() {
        if (classUnit == null) {
            throw new IllegalStateException("classUnit must not be null to produce a training set.");
        }
        if (verifyBeforeProduceDataSet()) {
            if (hiCharSource != null) {
                int length = hiCharSource.length();
                Iterator<Integer> indexes =
                        newContentIndexIterator(length);

                // Construct Instances
                Instances trainingSet = newBlankDataSet();

                // Can be parallel ...
                while (indexes.hasNext()) {
                    int charIndex = indexes.next();
                    double[] vector = newFeatureVector(charIndex);
                    int classIndex = getClassIndex();
                    vector[classIndex] = this.classUnit.getClassValue(charIndex);
                    trainingSet.add(new Instance(1.0, vector));
                }


                // Construct WordSegDataSet
                WordSegDataSet wsDataSet = new WordSegDataSetImpl(
                        plainTextSource,
                        trainingSet,
                        instanceIndexes,
                        true);
                return wsDataSet;
            } else {
                throw new RuntimeException("hiCharSource must be set before labeled dataset can be produced.");
            }
        }

        return null;
    }

    public void close() {
    }
}
