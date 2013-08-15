package com.wittawat.wordseg;

import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.data.BESTHiCharSource;
import com.wittawat.wordseg.feature.AbstractFVGenerator;
import com.wittawat.wordseg.feature.GuidedFVGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.REPTree;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.Saver;

/**
 * Convenient (and dirty) methods to perform some
 * frequently used actions.
 * 
 * @author Wittawat Jitkrittum
 */
public class Actions {

    public static Instances loadDataSet(String dataSetName) throws IOException, FileNotFoundException {
        return loadDataSet(new File("dataset/" + dataSetName + ".arff"));
    }

    public static Instances loadDataSet(File dataSetPath) throws IOException, FileNotFoundException {
        ArffReader reader = new ArffReader(new BufferedReader(new FileReader(dataSetPath)));
        Instances dataSet = reader.getData();
        dataSet.setClassIndex(dataSet.numAttributes() - 1);
        return dataSet;
    }

    /**@return a textual description of the statistics obtained
    after applying the model to the supplied test set.*/
    public static String evaluateModel(Classifier model, Instances testSet) throws Exception {
        Evaluation eval = new Evaluation(testSet);
        eval.evaluateModel(model, testSet);

        StringBuilder buf = new StringBuilder();
        buf.append(eval.toSummaryString(true));
        buf.append("------------------------\n");
        buf.append(eval.toClassDetailsString());
        buf.append("------------------------\n");
        buf.append(eval.toMatrixString());
        buf.append("---------------- End of model evaluation description -----------");
        return buf.toString();
    }

    public static String evaluateModel(Classifier model, File bestCorpusFile) throws Exception {
        FVGenerator fvGen = AbstractFVGenerator.getDefaultFVGenerator();
        BESTHiCharSource charSource = new BESTHiCharSource(bestCorpusFile);
        fvGen.setHiCharSource(charSource);
        WordSegDataSet wsd = fvGen.produceLabeledDataSet();
        Instances dataSet = wsd.getDataSet();
        return evaluateModel(model, dataSet);

    }

    public static Classifier loadModel(String modelName, String folderPath) throws Exception {
        return loadModel(modelName, new File(folderPath));
    }

    public static Classifier loadModel(String modelName) throws Exception {
        return loadModel(modelName, new File("model/"));
    }

    public static Classifier loadModel(File modelFile) throws Exception {
        Classifier model = (Classifier) SerializationHelper.read(modelFile.getAbsolutePath());
        return model;
    }

    public static Classifier loadModel(String modelName, File folder) throws Exception {
        File modelFile = FileSystemView.getFileSystemView().createFileObject(folder, modelName + ".model");
        Classifier model = (Classifier) SerializationHelper.read(modelFile.getAbsolutePath());
//        System.out.println("A "+model.getClass().getSimpleName() +" model was loaded.");
        return model;

    }

    public static void saveModel(Classifier model, File dest) throws Exception {

        SerializationHelper.write(dest.getAbsolutePath(), model);
    }

    public static void saveModel(Classifier model, String modelName) throws Exception {
        File dest = new File("model/" + modelName + ".model");
        saveModel(model, dest);

    }

    public static void saveARFF(Instances dataSet, File dest)
            throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataSet);
        saver.setFile(dest);
        saver.writeBatch();
        saver.getWriter().flush();

    }

    public static Instances mergeDataSets(Instances set1, Instances set2) {

        if (set1 == null || set2 == null) {
            throw new RuntimeException("Both instances must not be null.");

        } else {
            Instances mergedDataSet = new Instances(set1);

            for (int i = 0; i < set2.numInstances(); ++i) {
                mergedDataSet.add(set2.instance(i));
            }
            return mergedDataSet;
        }
    }

    public static void mergeAndWrite(String relationName, String destPath, String... dataSetPaths) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setFile(new File(destPath));
        saver.setRetrieval(Saver.INCREMENTAL);
        boolean first = true;

        for (String p : dataSetPaths) {
            ArffReader reader = new ArffReader(new BufferedReader(new FileReader(p)));
            Instances dataSet = reader.getData();

            if (first) {
                dataSet.setRelationName(relationName);
                saver.setStructure(dataSet);


                first = false;
            }
            for (int i = 0; i < dataSet.numInstances(); ++i) {
                saver.writeIncremental(dataSet.instance(i));
            }

        }
        saver.getWriter().flush();
        


    }
//      public static void testTrainingAStacking(String corpusFile, Classifier metaClassifier, Classifier... baseClassifiers) throws Exception {
//
//
//        Instances trainSet = DataSetGenerator.genTrainingDataSetFromTCC("temp_stacking_dataset", new File(corpusFile));
//
//        System.out.println("Begin training a meta classifier: " + metaClassifier.getClass().getName());
//        System.out.println("Base classifiers: " + getClassifiersName(baseClassifiers));
//
//        metaClassifier.setDebug(true);
//        Classifier model = LearningHelper.trainNoCrossStackingModel(
//                metaClassifier, baseClassifiers, trainSet);
//
//        System.out.println("Finished training a stacking model. Writing the model...");
//        SerializationHelper.write("model/stacking.model", model);
//
//    }
    ////////////////////////////////////////
    ////////////////////////////////////////

    public static Instances buildTrainingDataFromFile(String dataSetName, File corpusFile, FVGenerator fvGenerator) {
        System.out.println("Building training data from: " + corpusFile.getAbsolutePath());
        BESTHiCharSource charSource = new BESTHiCharSource(corpusFile);
        fvGenerator.setDataSetName(dataSetName);
        fvGenerator.setHiCharSource(charSource);
        WordSegDataSet wds = fvGenerator.produceLabeledDataSet();
        Instances dataSet = wds.getDataSet();
        return dataSet;
    }

    public static void buildTrainingDataFromCorpus(String dataSetName,
            File corpusRoot, FVGenerator fvGenerator, File dest)
            throws IOException {
        Collection<File> children = FileUtils.listFiles(corpusRoot,
                new RegexFileFilter(".+\\.txt", IOCase.INSENSITIVE), DirectoryFileFilter.INSTANCE);

        ArffSaver saver = new ArffSaver();
        saver.setFile(dest);
        saver.setRetrieval(Saver.INCREMENTAL);
        boolean first = true;
        for (File textFile : children) {

            Instances dataSet = buildTrainingDataFromFile(dataSetName, textFile, fvGenerator);

            if (first) {
                saver.setStructure(dataSet);

                first = false;
            }
            for (int i = 0; i < dataSet.numInstances(); ++i) {
                saver.writeIncremental(dataSet.instance(i));
            }

        }
        saver.getWriter().flush();
    }

    public static void genTrainingData(String category, String destFileName) throws IOException {
        File dir = new File("/media/SHARE/QA_project_resources/Best_Corpus/train/" + category);
        File dest = new File("dataset/" + destFileName + ".arff");

        genTrainingData(category, dir, dest);
    }

    public static void genTrainingData(String dataSetName, File sourceFolder, File dest) throws IOException {
        FVGenerator generator =
                new GuidedFVGenerator(AbstractFVGenerator.getDefaultFeatureUnits());
//                AbstractFVGenerator.getDefaultFVGenerator();
        buildTrainingDataFromCorpus(dataSetName, sourceFolder, generator, dest);
    }

    public static void genTrainingData(String category) throws IOException {
        genTrainingData(category, category);
    }

    public static void genTrainingData() throws IOException {
//        String[] cats = {
//            "article_1", "article_2",
//            "article_3", "article_4", "article_5",
//            "buddhism_1", "buddhism_2",
//            "encyclopedia_1", "encyclopedia_2", "encyclopedia_3",
//            "law_1", "law_2", "law_3",
//            "news_1", "news_2", "news_3",
//            "novel_1", "novel_2", "novel_3",
//            "talk_1",
//            "wiki_1", "wiki_2", "wiki_3"};

        String[] cats = {
            "mix_1"};
        genTrainingData(cats);
    }

    public static void genTrainingData(String... cats) throws IOException {
        for (String cat : cats) {
            System.out.println("Generating dataset of category: " + cat);
            genTrainingData(cat);
            System.out.println("Finished generating dataset of category: " + cat);
        }
    }

    public static void genTrainingDataFromOneFile(File corpusFile)
            throws IOException {
        FVGenerator generator = AbstractFVGenerator.getDefaultFVGenerator();
        String name = corpusFile.getName().substring(0, corpusFile.getName().lastIndexOf("."));
        Instances ins = buildTrainingDataFromFile(name, corpusFile, generator);


        File dest = new File("dataset/" + name + ".arff");
        saveARFF(ins, dest);
    }

    public static void mergeAndWrite() throws IOException {
        System.out.println("Merging article datasets.");
        mergeAndWrite("article", "dataset/article.arff",
                "dataset/article_1.arff", "dataset/article_2.arff", "dataset/article_3.arff",
                "dataset/article_4.arff", "dataset/article_5.arff");

        System.out.println("Merging buddhism datasets.");
        mergeAndWrite("buddhism", "dataset/buddhism.arff",
                "dataset/buddhism_1.arff", "dataset/buddhism_2.arff");

        System.out.println("Merging encyclopedia datasets.");
        mergeAndWrite("encyclopedia", "dataset/encyclopedia.arff",
                "dataset/encyclopedia_1.arff", "dataset/encyclopedia_2.arff", "dataset/encyclopedia_3.arff");

        System.out.println("Merging law datasets.");
        mergeAndWrite("law", "dataset/law.arff",
                "dataset/law_1.arff", "dataset/law_2.arff", "dataset/law_3.arff");

        System.out.println("Merging news datasets.");
        mergeAndWrite("news", "dataset/news.arff",
                "dataset/news_1.arff", "dataset/news_2.arff", "dataset/news_3.arff");

        System.out.println("Merging novel datasets.");
        mergeAndWrite("novel", "dataset/novel.arff",
                "dataset/novel_1.arff", "dataset/novel_2.arff", "dataset/novel_3.arff");

        System.out.println("Merging wiki datasets.");
        mergeAndWrite("wiki", "dataset/wiki.arff",
                "dataset/wiki_1.arff", "dataset/wiki_2.arff", "dataset/wiki_3.arff");
    }

    /**@param dataSet data set used to train the based classifiers.*/
    public static Classifier trainNoCrossStackingModel(
            Classifier metaClassifier,
            Classifier[] trainedBaseClassifiers,
            Instances dataSet) throws Exception {

        NoCrossStacking stacking = new NoCrossStacking(metaClassifier, trainedBaseClassifiers);
        stacking.buildClassifier(dataSet);
        return stacking;
    }

    public static Classifier trainNoCrossStackingModel(Instances dataSet) throws Exception {
        Classifier meta =
                //                                                new NaiveBayes();
                //                new RandomTree();
                new REPTree();
        //                new J48();
//                new REPTree();
//        meta.setOptions(new String[]{"-S", "58653980"});

//        Classifier[] bases = {
//            loadModel("article_1.5M"),
//            loadModel("buddhism_1.4M"),
//            loadModel("encyclopedia_1.4M"),
//            loadModel("law_1.4M"),
//            loadModel("news_1.3M"),
//            loadModel("novel_1.4M"),
//            loadModel("talk_1"),
//            loadModel("wiki_1.5M"),
//        };

//        Classifier[] bases = {
//            loadModel("article_fold_1_of_3"),
//            loadModel("article_fold_2_of_3"),
//            //            loadModel("article_fold_3_of_3"),
//            loadModel("buddhism_fold_1_of_2"),
//            loadModel("buddhism_fold_2_of_2"),
//            loadModel("encyclopedia_fold_1_of_3"),
//            loadModel("encyclopedia_fold_2_of_3"),
//            //            loadModel("encyclopedia_fold_3_of_3"),
//            loadModel("law_fold_1_of_2"),
//            loadModel("law_fold_2_of_2"),
//            loadModel("news_fold_1_of_5"),
//            loadModel("news_fold_2_of_5"),
//            //            loadModel("news_fold_3_of_5"),
//            //            loadModel("news_fold_4_of_5"),
//            //            loadModel("news_fold_5_of_5"),
//            loadModel("novel_fold_1_of_4"),
//            loadModel("novel_fold_2_of_4"),
//            //            loadModel("novel_fold_3_of_4"),
//            //            loadModel("novel_fold_4_of_4"),
//            loadModel("talk_fold_1_of_1"),
//            loadModel("wiki_fold_1_of_2"),
//            loadModel("wiki_fold_2_of_2"),};

        Classifier[] bases = {
            loadModel("article"),
            loadModel("buddhism"),
            loadModel("encyclopedia"),
            loadModel("law"),
            loadModel("news"),
            loadModel("novel"),
            loadModel("talk"),
            loadModel("wiki")
        };
        return trainNoCrossStackingModel(meta, bases, dataSet);

    }

    public static Classifier trainNoCrossStackingModel() throws Exception {
        return trainNoCrossStackingModel(loadDataSet("1M_mixed"));
    }

    public static void mixMerge() throws IOException {
//        mergeAndWrite("200K_mixed", "dataset/200K_mixed.arff",
//                "dataset/article_200K.arff",
//                "dataset/buddhism_200K.arff",
//                "dataset/encyclopedia_200K.arff",
//                "dataset/law_200K.arff",
//                "dataset/news_200K.arff",
//                "dataset/novel_200K.arff",
//                "dataset/talk_200K.arff",
//                "dataset/wiki_200K.arff");
        mergeAndWrite("1.5M_mixed", "dataset/1.5M_mixed.arff",
                "dataset/article_fold_3_of_3.arff",
                "dataset/buddhism_fold_2_of_2.arff",
                "dataset/encyclopedia_fold_3_of_3.arff",
                "dataset/law_fold_2_of_2.arff",
                "dataset/news_fold_5_of_5.arff",
                "dataset/novel_fold_4_of_4.arff",
                "dataset/talk.arff",
                "dataset/wiki_fold_2_of_2.arff");


    }

    public static void main(String[] args) throws Exception {
//        mixMerge();

        genTrainingData();
//        mergeAndWrite();
//        genTrainingDataFromOneFile(new File("/media/SHARE/QA_project_resources/Best_Corpus/train/article/article_00002.txt"));

//        Instances dataSet = loadDataSet("200K_mixed");
//        Classifier stacking = trainNoCrossStackingModel(dataSet);
//        saveModel(stacking,
//                //                "stacking_reptree_full"
//                //    "stacking_reptree"
//                //                                "stacking_NB"
//                //                                "stacking_NB_full"
//                "stacking_randomtree_full" //                "stacking_J48"
//                );


//        String evaluation = evaluateModel(loadModel("stacking"), new File("/media/SHARE/QA_project_resources/Best_Corpus/train/article_4/article_00129.txt"));
//        System.out.println(evaluation);

    }
}

