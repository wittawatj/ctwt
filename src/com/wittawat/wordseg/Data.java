package com.wittawat.wordseg;

import com.aliasi.chunk.Chunker;
import com.aliasi.dict.Dictionary;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.dict.TrieDictionary;
import com.aliasi.io.BitInput;
import com.aliasi.lm.BitTrieReader;
import com.aliasi.lm.TrieCharSeqCounter;
import com.aliasi.tokenizer.CharacterTokenizerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * Helper class to process data needed for word segmentation.
 *
 * @author Wittawat Jitkrittum
 */
public class Data {
    public static final String DEFAULT_MODEL_NAME = "default";
    public static final String DEFAULT_PERSON_TITLES__DICT_PATH = "data/person_titles.txt";
    public static final String DEFAULT_NE_DICT_PATH = "data/ne.txt";
    private static Dictionary<String> personTitleDictionary;
    private static Dictionary<String> neDictionary;
    private static Classifier defaultModel;

    public static Classifier getDefaultModel() throws Exception {
        if (defaultModel == null) {
            defaultModel = Actions.loadModel(
                    DEFAULT_MODEL_NAME
                    );

        }
        return defaultModel;
    }

    public static Dictionary<String> getPersonTitleDictionary() throws IOException {
        if (personTitleDictionary == null) {
            personTitleDictionary =
                    new TrieDictionary<String>();
//                    new MapDictionary<String>();
            for (String w : (List<String>) FileUtils.readLines(new File(DEFAULT_PERSON_TITLES__DICT_PATH))) {
                personTitleDictionary.addEntry(new DictionaryEntry<String>(w, "TITLE"));
            }
        }
        return personTitleDictionary;
    }
    private static Dictionary<String> dictionary;
    private static Chunker defaultChunker;

    public static Chunker getDefaultChunker() throws IOException {

        if (defaultChunker == null) {
            defaultChunker = new ExactDictionaryChunker(getDictionary(),
                    CharacterTokenizerFactory.INSTANCE, false, false);
        }
        return defaultChunker;
    }

    public static Dictionary<String> getNEDictionary() throws IOException {
        if (neDictionary == null) {
            Dictionary<String> dict = new MapDictionary<String>();

            for (String w : (List<String>) FileUtils.readLines(new File(DEFAULT_NE_DICT_PATH))) {
                dict.addEntry(new DictionaryEntry<String>(w, "NE", 1.0));
            }
            neDictionary = dict;
        }
        return neDictionary;

    }

    public static Dictionary<String> getDictionary() throws IOException {
        if (dictionary == null) {


            Dictionary<String> dict =
                    //                    new TrieDictionary<String>();
                    new MapDictionary<String>();



            // NE
            for (String w : (List<String>) FileUtils.readLines(new File(DEFAULT_NE_DICT_PATH))) {
                dict.addEntry(new DictionaryEntry<String>(w, "NE", 1.0));
            }
            
            // Lexeme
//            for (String w : (List<String>) FileUtils.readLines(new File("data/lexeme_utf_sort.txt"))) {
//                dict.addEntry(new DictionaryEntry<String>(w, "OTHER", 0.5));
//            }

            // Other from Lexitron+Wiki titles

//            for (String w : (List<String>) FileUtils.readLines(new File("data/lexitron_wiki_length_order.txt"))) {
//                dict.addEntry(new DictionaryEntry<String>(w, "OTHER", 0.5));
//            }


            // Other
//            for (String w : (List<String>) FileUtils.readLines(new File(
//                    //                    "data/Nook_other_10chars.txt"
//                    "data/BEST_other_13.txt"))) {
//                dict.addEntry(new DictionaryEntry<String>(w, "OTHER", 0.5));
//            }



            // Abbreviation
//            for (String w : (List<String>) FileUtils.readLines(new File("data/BEST_AB_sort.txt"))) {
//                dict.addEntry(new DictionaryEntry<String>(w, "AB", 0.4));
//            }
            dictionary = dict;
        }
        return dictionary;
    }

    public static TrieCharSeqCounter getCharGramsTrie(File serializedFile, int maxLength) throws FileNotFoundException, IOException {

        InputStream fis = new FileInputStream(serializedFile);

        BitInput bitIn = new BitInput(fis);

        BitTrieReader trieReader = new BitTrieReader(bitIn);
        TrieCharSeqCounter trie = TrieCharSeqCounter.readCounter(trieReader, maxLength);

        fis.close();
        bitIn.close();

        return trie;

    }
    public static TrieCharSeqCounter getCharGramsTrie() throws FileNotFoundException, IOException {
        int maxLength = 6;
        final File serializedTrie = new File("data/" + maxLength + "_gram_chars_trie.obj");
        return getCharGramsTrie(serializedTrie, maxLength);
    }
}
