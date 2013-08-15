package com.wittawat.wordseg.action;

import com.wittawat.wordseg.Actions;
import com.wittawat.wordseg.BasicWordTokenizer;
import com.wittawat.wordseg.NukeTokenizer3;
import com.wittawat.wordseg.NukeTokenizer3Verbose;
import com.wittawat.wordseg.feature.AbstractFVGenerator;
import com.wittawat.wordseg.feature.FVGenerator;
import java.io.File;
import java.util.Iterator;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * Tokenize files in a directory and output to another directory.
 * @author Wittawat Jitkrittum
 */
public class TokDir {

    private File sourceFolder;
    private File destFolder;
    private String[] extensions = {"txt"};
    private boolean outputProbs ;
    static Classifier model;
    static FVGenerator fvg;

    public static FVGenerator getFvg() {
        if (fvg == null) {
            fvg = AbstractFVGenerator.getDefaultFVGenerator();
        }
        return fvg;
    }

    public static Classifier getModel() throws Exception {
        if (model == null) {
            model = Actions.loadModel("stacking_full");
        }

        return model;
    }

    public TokDir(File sourceFolder, File destFolder, boolean outputProbs) throws Exception {

        this.sourceFolder = sourceFolder;
        this.destFolder = destFolder;
        assert sourceFolder.isDirectory();
        assert destFolder.isDirectory();
        this.outputProbs = outputProbs;

    }

    public TokDir(String sourceFolder, String destFolder, boolean outputProbs) throws Exception {
        this(new File(sourceFolder), new File(destFolder), outputProbs);
    }

    public void tokenizeWrite() throws Exception {
        Iterator<File> files = FileUtils.iterateFiles(sourceFolder, extensions, true);
        while (files.hasNext()) {

            File src = files.next();
            String fName = src.getName();
            File dest = FileSystemView.getFileSystemView().createFileObject(destFolder, fName);

            String content = FileUtils.readFileToString(src);
            NukeTokenizer3 tokenizer = outputProbs ? new NukeTokenizer3Verbose(content, getModel(), getFvg()) : new NukeTokenizer3(content, getModel(), getFvg());
            String tokenized = outputProbs ? tokenizer.tokenizeWithProb() : tokenizer.tokenize();
            FileUtils.writeStringToFile(dest, tokenized);

            System.out.println("Finished tokenizing: " + src.getAbsolutePath());
        }

    }

    public static void main(String[] args) throws Exception {
        String sourceDir = "/media/SHARE/QA_project_resources/Best_Corpus/test/corpus_test";
        String destDir = "/media/SHARE/QA_project_resources/Best_Corpus/test/results_prob";

        TokDir td = new TokDir(sourceDir, destDir, true);
        td.tokenizeWrite();
    }
}
