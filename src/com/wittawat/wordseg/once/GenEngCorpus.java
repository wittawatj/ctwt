
package com.wittawat.wordseg.once;

import com.aliasi.lm.CharSeqCounter;
import com.wittawat.wordseg.Actions;
import com.wittawat.wordseg.Data;
import com.wittawat.wordseg.feature.AbstractFVGenerator;
import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.feature.GuidedFVGenerator;
import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.word.WordStatsGen;
import java.io.File;
import java.util.Map;

/**
 * Generate an English dataset.
 * For homework of "Pattern Information Processing" (Spring 2010, Tokyo Tech).
 * @author Wittawat Jitkrittum
 */
public class GenEngCorpus {
    public static void main(String[] args) throws Exception{

        int maxLength = 6;
        File corpus = new File("/media/SHARE/titech/2010_spring/Pattern_Information_Processing/corpus_textfiles.com");
        File dest = new File("/media/SHARE/titech/2010_spring/Pattern_Information_Processing/hw1/textfiles_dataset.arff");
        File gramFile = new File("/media/SHARE/titech/2010_spring/Pattern_Information_Processing/hw1/textfiles.com_" + maxLength + "_gram_chars_trie.obj");

        File prefixMapFile = new File("/media/SHARE/titech/2010_spring/Pattern_Information_Processing/hw1/prefix_5_tab.txt");
        File suffixMapFile = new File("/media/SHARE/titech/2010_spring/Pattern_Information_Processing/hw1/suffix_5_tab.txt");

        Map<String, Float> prefixMap = WordStatsGen.loadWordFloatMap(prefixMapFile);
        Map<String, Float> suffixMap = WordStatsGen.loadWordFloatMap(suffixMapFile);
        CharSeqCounter counter = Data.getCharGramsTrie(gramFile, maxLength);
        char[] consideringChars = MyStringUtils.getEnglishCharsInUnicode();
        FVGenerator fvg = new GuidedFVGenerator(AbstractFVGenerator.getStatsFeatureUnits(counter , consideringChars, prefixMap, suffixMap));

        Actions.buildTrainingDataFromCorpus("textfiles.com", corpus, fvg, dest);
    }

}
