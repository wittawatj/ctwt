package com.wittawat.wordseg.action;

import com.wittawat.wordseg.Data;
import com.wittawat.wordseg.NukeTokenizer;
import com.wittawat.wordseg.NukeTokenizer3;
import java.io.File;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * CL tool to perform word segmentation only once
 * and terminate.
 * 
 * @author Wittawat Jitkrittum
 */
public class WordSeg {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("USAGE: program input [outputFile]");
            System.exit(1);
        }
        long start = new Date().getTime();
        String inputPath = args[0];
        boolean isUseDict = true;
        String outputPath = null;
        if (args.length >= 2) {
            if(args[1]!=null && !args[1].trim().equals("")){
                outputPath = args[1];
            }else{
                outputPath = null;
            }
            
        }
        System.out.println("Loading model ...");
        Classifier model = Data.getDefaultModel();
        System.out.println("\n" + new NukeTokenizer3("โปรแกรมตัดคำไทยด้วยเทคนิคการจำแนกประเภท", model, isUseDict).tokenize() + "\n");
        System.out.println("Finished loading model.");

        String content = FileUtils.readFileToString(new File(inputPath));
        NukeTokenizer tokenizer = new NukeTokenizer3(content, model, isUseDict);
        String tokenized = tokenizer.tokenize();
        if(outputPath == null){
            System.out.println(tokenized);
        }else{
            FileUtils.writeStringToFile(new File(outputPath), tokenized);
        }

        long end = new Date().getTime();
        System.out.println("Total time: " + (end - start) + " ms");

    }
}
