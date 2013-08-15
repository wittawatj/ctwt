package com.wittawat.wordseg.action;

import com.wittawat.wordseg.Actions;
import java.io.File;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;

/**
 * CL tool to train the meta classifier in the stacking of
 * classifiers.
 *
 * @author Wittawat Jitkrittum
 */
public abstract class TrainStackedMeta {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("USAGE: program arffDataSetPath destModel baseModel1 baseModel2 [baseModel3 [baseModel4 [...]]]");
            System.exit(1);
        }
        String dataSetPath = args[0];
        String dest = args[1];
        Instances dataSet = Actions.loadDataSet(new File(dataSetPath));
        int baseCount = args.length - 2;
        Classifier[] bases = new Classifier[baseCount];
        for (int i = 2; i < args.length; ++i) {
            bases[i - 2] = Actions.loadModel(new File(args[i]));

        }


        Classifier meta =
                //                                new NaiveBayes();
                                new J48();
                //                new REPTree();
                

//        meta.setOptions(new String[]{"-S", "58653980"});


        Classifier resultModel = Actions.trainNoCrossStackingModel(meta, bases, dataSet);
        Actions.saveModel(resultModel, new File(dest));
    }
}
