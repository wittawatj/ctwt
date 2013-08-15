package com.wittawat.wordseg;

import weka.classifiers.Classifier;
import weka.classifiers.MultipleClassifiersCombiner;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.RevisionUtils;

/**
 * An own implementation of stacking classifier.
 * This class has a bit difference to Stacking from WEKA
 * in that this class does not perform a cross validation
 * on the base classifiers. In Stacking class of WEKA,
 * 10-fold cross validation is performed on the based classifiers
 * by default. This means that the base classifiers will be retrained.
 *
 * The intention of this class is to use a meta classifier to
 * perform a classification on the outputs of existing
 * classifiers (retaining the original class label) without
 * altering the original base classifiers. Thus, this
 * stacking can be used with serialized models unaltered.
 *
 * This class is modified from the original Stacking class's code
 * of WEKA.
 *
 * If any of the base classifier has 2 possible values of class label
 * then only one of them will be included.
 * 
 * @author Wittawat Jitkrittum
 */
public class NoCrossStacking extends MultipleClassifiersCombiner {

    /** The meta classifier */
    protected Classifier m_MetaClassifier = null;
    /** Format for meta data. SHould contain no instance. Only the
    header information is needed.*/
    protected Instances m_MetaFormat = null;
    /** Format for base data. SHould contain no instance. Only the
    header information is needed. */
    protected Instances m_BaseFormat = null;

    public NoCrossStacking(Classifier metaClassifier, Classifier[] baseClassifiers) {
        this.m_MetaClassifier = metaClassifier;
        setClassifiers(baseClassifiers);
    }

    @Override
    public void buildClassifier(Instances data) throws Exception {
        if (m_MetaClassifier == null) {
            throw new IllegalArgumentException("No meta classifier has been set");
        }
        
        if (data.classIndex() < 0) {
            throw new IllegalArgumentException("data must have a (correct) class attribute.");
        }

        // can classifier handle the data?
        getCapabilities().testWithFail(data);

        // remove instances with missing class
        Instances newData = new Instances(data);
        m_BaseFormat = new Instances(data, 0);
        newData.deleteWithMissingClass();


        // Create meta level
        generateMetaLevel(newData);


    }

    /**
     * Generates the meta data
     *
     * @param newData the data to work on
     * @throws Exception if generation fails
     */
    protected void generateMetaLevel(Instances newData)
            throws Exception {

        Instances metaData = metaFormat(newData);
        m_MetaFormat = new Instances(metaData, 0);

        for (int i = 0; i < newData.numInstances(); i++) {
            metaData.add(metaInstance(newData.instance(i)));
        }


        m_MetaClassifier.buildClassifier(metaData);
    }

    /**
     * Makes the format for the level-1 data.
     *
     * @param instances the level-0 format
     * @return the format for the meta data
     * @throws Exception if the format generation fails
     */
    protected Instances metaFormat(Instances instances) throws Exception {

        FastVector attributes = new FastVector();
        Instances metaFormat;

        for (int k = 0; k < m_Classifiers.length; k++) {
            Classifier classifier = (Classifier) getClassifier(k);
            String name = classifier.getClass().getName();
            if (m_BaseFormat.classAttribute().isNumeric()) {
                attributes.addElement(new Attribute(name));
            } else {

                int numValues = m_BaseFormat.classAttribute().numValues();
                assert numValues >= 2;
                if (numValues == 2) {
                    // Include only one as the feature to reduce dependency between attributes.
                    attributes.addElement(new Attribute(name + ":" +
                            m_BaseFormat.classAttribute().value(0)));
                } else {

                    for (int j = 0; j < numValues; j++) {
                        attributes.addElement(new Attribute(name + ":" +
                                m_BaseFormat.classAttribute().value(j)));
                    }
                }

            }
        }
        attributes.addElement(m_BaseFormat.classAttribute().copy());
        metaFormat = new Instances("Meta format", attributes, 0);
        metaFormat.setClassIndex(metaFormat.numAttributes() - 1);
        return metaFormat;
    }

    /**
     * Makes a level-1 instance from the given instance.
     *
     * @param instance the instance to be transformed
     * @return the level-1 instance
     * @throws Exception if the instance generation fails
     */
    protected Instance metaInstance(Instance instance) throws Exception {

        double[] values = new double[m_MetaFormat.numAttributes()];
        Instance metaInstance;
        int i = 0;
        for (int k = 0; k < m_Classifiers.length; k++) {
            Classifier classifier = getClassifier(k);
            if (m_BaseFormat.classAttribute().isNumeric()) {
                values[i++] = classifier.classifyInstance(instance);
            } else {
                double[] dist = classifier.distributionForInstance(instance);
                if (dist.length ==2) {
                    values[i++] = dist[0];
                } else {
                    assert dist.length > 2;
                    for (int j = 0; j < dist.length; j++) {
                        values[i++] = dist[j];
                    }
                }
            }
        }

        values[i] = instance.classValue();
        metaInstance = new Instance(1, values);
        metaInstance.setDataset(m_MetaFormat);
        return metaInstance;
    }

    public String getRevision() {
        return RevisionUtils.extract("$Revision: 1.0 $");
    }

    /**
     * Returns class probabilities.
     *
     * @param instance the instance to be classified
     * @return the distribution
     * @throws Exception if instance could not be classified
     * successfully
     */
    @Override
    public double[] distributionForInstance(Instance instance) throws Exception {

        return m_MetaClassifier.distributionForInstance(metaInstance(instance));
    }

    /**
     * Gets the meta classifier.
     *
     * @return the meta classifier
     */
    public Classifier getMetaClassifier() {

        return m_MetaClassifier;
    }

    /**
     * Output a representation of this classifier
     *
     * @return a string representation of the classifier
     */
    @Override
    public String toString() {

        if (m_Classifiers.length == 0) {
            return "Stacking: No base schemes entered.";
        }
        if (m_MetaClassifier == null) {
            return "Stacking: No meta scheme selected.";
        }
        if (m_MetaFormat == null) {
            return "Stacking: No model built yet.";
        }
        String result = "Stacking\n\nBase classifiers\n\n";
        for (int i = 0; i < m_Classifiers.length; i++) {
            result += getClassifier(i).toString() + "\n\n";
        }

        result += "\n\nMeta classifier\n\n";
        result += m_MetaClassifier.toString();

        return result;
    }
    //////////////////////

    
}
