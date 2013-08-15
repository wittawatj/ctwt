package com.wittawat.wordseg.word;

import com.wittawat.wordseg.utils.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;

/**
 * A character n-gram counter.
 * 
 * @author Wittawat Jitkrittum
 */
public class CharGramCounter {

    private String content;
    private int maxGram;

    public CharGramCounter(String content, int maxGram) {
        this.content = content;
        if (maxGram <= 0) {
            throw new IllegalArgumentException("maxGram must be > 0");
        }
        this.maxGram = maxGram;
    }

    protected void addOneGram(Map<String, Integer> grams, String gram) {
        Integer freq = grams.get(gram);
        if (freq == null) {
            grams.put(gram, 1);
        } else {
            grams.put(gram, 1 + freq);

        }

    }

    public void countGrams(Map<String, Integer> grams) {


        int length = content.length();
        for (int i = 0; i < length; ++i) {
            for (int j = i - maxGram + 1; j <= i; ++j) {
                if (j >= 0) {
                    String gram = content.substring(j, i + 1);
                    addOneGram(grams, gram);
                }
            }
        }

    }
//////////////////////////////////

    public static void main(String[] args) throws Exception {
        String content = FileUtils.readFileToString(new File("/media/SHARE/QA_project_resources/Best_Corpus/train/law/law_00009.txt"));
        content =
                MyStringUtils.removeAllNewLines(content);
        CharGramCounter counter = new CharGramCounter(content, 10);
        Map<String, Integer> grams = new HashMap<String, Integer>();
        counter.countGrams(grams);
        System.out.println(grams);
    }
}
