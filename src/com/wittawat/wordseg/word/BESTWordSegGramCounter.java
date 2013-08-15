package com.wittawat.wordseg.word;

import com.wittawat.wordseg.utils.*;
import com.wittawat.wordseg.data.BESTCorpus;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * A <code>GramCounter</code> designed for
 * word segmentation.
 * 
 * @author Wittawat Jitkrittum
 */
public class BESTWordSegGramCounter extends CharGramCounter {

    public BESTWordSegGramCounter(String content, int maxGram) {
        super(MyStringUtils.cleanBESTTags(content), maxGram);
    }

    /**Eliminate the delimiters that are not at the edge of
    the gram. This is to ease the process of word segmentation
    later.*/
    @Override
    protected void addOneGram(Map<String, Integer> grams, String gram) {
        if (gram.length() >= 3) {
            gram = gram.charAt(0) +
                    gram.substring(1, gram.length() - 1).replace(BESTCorpus.DELIMITER, "") +
                    gram.charAt(gram.length() - 1);
        }
        super.addOneGram(grams, gram);
    }

    public static void generateNGrams(File f, Map<String, Integer> grams, int maxGrams) throws IOException {
        String content = FileUtils.readFileToString(f);
        content = MyStringUtils.removeAllNewLines(content);
        CharGramCounter counter = new BESTWordSegGramCounter(content, maxGrams);
        counter.countGrams(grams);
    }

    /**Only consider .txt files (recursively).*/
    public static void generateNGramsFromFolder(File folder, Map<String, Integer> grams, int maxGrams) throws IOException {
        Iterator<File> files = FileUtils.iterateFiles(folder, new String[]{"txt"}, true);
        while (files.hasNext()) {

            File f = files.next();
            System.out.println("Generating "+maxGrams+"-gram from: "+f.getAbsolutePath());
            generateNGrams(f, grams, maxGrams);
        }
    }
    public static Map<String, Integer> filter(Map<String, Integer> grams){
        Map<String, Integer> newGrams=  new HashMap<String, Integer>();
        Filter<String> fil = new ThaiCharFilter();
        for(Map.Entry<String, Integer> e : grams.entrySet()){
            String chars = e.getKey();
            if(fil.accepts(chars) && e.getValue() > 1){
                newGrams.put(chars, e.getValue());
            }
        }
        return newGrams;
    }
    //////////////////////////////////

    public static void main(String[] args) throws Exception {
        File corpusRoot = new File("/media/SHARE/QA_project_resources/Best_Corpus/train");
        int maxGrams = 6;
        Map<String, Integer> grams = new HashMap<String, Integer>();
        generateNGramsFromFolder(corpusRoot, grams, maxGrams);

        grams = filter(grams);
        WordStatsGen.writeMap(grams, new File("data/"+maxGrams+"_gram_chars.txt"));
        
//        for (Map.Entry<String, Integer> e : grams.entrySet()) {
//            System.out.printf("%10s : %5d\n", e.getKey(), e.getValue());
//        }
    }
}
