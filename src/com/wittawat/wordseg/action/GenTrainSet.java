package com.wittawat.wordseg.action;

import com.wittawat.wordseg.Actions;
import com.wittawat.wordseg.feature.AbstractFVGenerator;
import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.feature.GuidedFVGenerator;
import java.io.File;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

/**
 * CL tool used to generate word segmentation dataset
 * using the default <code>FVGenerator</code>.
 *
 * @author Wittawat Jitkrittum
 */
public abstract class GenTrainSet {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("USAGE: program dataSetName destARFF sourceFile1 [sourceFile2 [sourceFile3 [...]]]");
            System.exit(1);
        }

        FVGenerator fvg = new GuidedFVGenerator();
        String dataSetName = args[0];
        File dest = new File(args[1]);
        ArffSaver saver = new ArffSaver();
        saver.setFile(dest);
        saver.setRetrieval(Saver.INCREMENTAL);
        boolean first = true;
        for (int i = 2; i < args.length; ++i) {
            File corpusFile = new File(args[i]);
            Instances dataSet = Actions.buildTrainingDataFromFile(dataSetName, corpusFile, fvg);
            if (first) {
                saver.setStructure(dataSet);

                first = false;
            }
            int num = dataSet.numInstances();
            System.out.println("Num instances: "+num);
            for (int j = 0; j < num; ++j) {
                saver.writeIncremental(dataSet.instance(j));
            }
        }
        saver.getWriter().flush();



    }
}
