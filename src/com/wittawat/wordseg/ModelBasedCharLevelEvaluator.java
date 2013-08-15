package com.wittawat.wordseg;

import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.data.ContentContainer;
import com.wittawat.wordseg.utils.IntArrayIterator;
import com.wittawat.wordseg.utils.IntIterator;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A <code>CharLevelTokenizer</code> which uses a trained machine learning model
 * to determine the word end.
 * 
 * @author nook
 */
public class ModelBasedCharLevelEvaluator extends AbstractCharLevelEvaluator {

    private Classifier model;
    private FVGenerator fvGenerator;

    public ModelBasedCharLevelEvaluator(Classifier model, FVGenerator fvGenerator) {
        this(null, model, fvGenerator);
    }

    /** The featureGenerator must be the one used to train the model. */
    public ModelBasedCharLevelEvaluator(ContentContainer source, Classifier model, FVGenerator fvGenerator) {
        super(source);
        this.model = model;
        this.fvGenerator = fvGenerator;
    }

    public String[] getClassValues() {
        return fvGenerator.getClassUnit().getPossibleValues();
    }

    private Iterator<Integer> indexIterator(int contentLength) {
        int[] indexes = source.getIndexes();
        return indexes == null ? new IntIterator(0, contentLength - 1) : new IntArrayIterator(indexes);
    }

    public Map<Integer, double[]> getClassProbabilites() {

        Map<Integer, double[]> classProbsMap = new HashMap<Integer, double[]>();

        String content = source.getContent();
        int[] indexes = source.getIndexes();
        fvGenerator.setInstanceIndexes(indexes);

        fvGenerator.setStringSource(content);
        WordSegDataSet dataSet = fvGenerator.produceUnlabeledDataSet();
        Instances instances = dataSet.getDataSet();

        Iterator<Integer> charIndexes = indexIterator(content.length());
        int i = 0;
        while (charIndexes.hasNext()) {
            int charIndex = charIndexes.next();
            Instance ins = instances.instance(i);


            double[] classProbs = classProb(ins);
            classProbsMap.put(charIndex, classProbs);
            ++i;
        }

        return classProbsMap;
    }

    private double[] classProb(Instance ins) {
        
        try {
            double[] BIProbs = model.distributionForInstance(ins);
            if (BIProbs != null) {
                assert BIProbs.length == fvGenerator.getClassUnit().getPossibleValues().length;
                return BIProbs;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            double v = model.classifyInstance(ins);
//            System.out.println(v);
            return new double[]{1.0, 0.0};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // /////////////
    public static void main(String[] args) throws Exception {
    }
}
