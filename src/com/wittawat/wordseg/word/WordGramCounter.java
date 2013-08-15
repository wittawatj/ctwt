package com.wittawat.wordseg.word;

import com.wittawat.wordseg.data.BESTHiCharSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * n-gram counter for words.
 * @author Wittawat Jitkrittum
 */
public class WordGramCounter {

    private Iterator<String> wordSequence;
    private int maxGram;

    public WordGramCounter(Iterator<String> wordSequence, int maxGram) {
        this.wordSequence = wordSequence;
        if (maxGram <= 0) {
            throw new IllegalArgumentException("maxGram must be > 0");
        }
        this.maxGram = maxGram;
    }

    protected void addOneNGram(Map<WordSequence, Integer> grams, WordSequence ngram) {
        Integer freq = grams.get(ngram);
        if (freq == null) {
            grams.put(ngram, 1);
        } else {
            grams.put(ngram, 1 + freq);

        }

    }

    private void addToWindow(List<String> window, String word) {
        window.add(word);
        if (window.size() > maxGram) {
            window.remove(0);
        }

    }

    private String[] subArray(List<String> window, int from, int to) {
        int length = to - from + 1;
        String[] array = new String[length];
        int a = 0;
        for (int i = from; i <= to; ++i) {
            array[a] = window.get(i);
            ++a;
        }
        return array;

    }

    public void countGrams(Map<WordSequence, Integer> grams, WordSequenceFilter filter) {

        List<String> window = new ArrayList<String>();
        while (wordSequence.hasNext()) {
            String word = wordSequence.next();
            addToWindow(window, word);
            int windowLength = window.size();
            for (int j = 0; j < windowLength; ++j) {
                int lastWindowIndex = windowLength - 1;
                String[] ngram = subArray(window, j, lastWindowIndex);
                WordSequence sequence = new WordSequence(ngram);
                if (filter == null || filter.accepts(sequence)) {
                    addOneNGram(grams, sequence);
                }
            }

        }

    }

    public void countGrams(Map<WordSequence, Integer> grams) {
        countGrams(grams, null);
    }

    public static List<Map.Entry<WordSequence, Integer>> sortByAlphanet(Set<Map.Entry<WordSequence, Integer>> entries) {
        List<Map.Entry<WordSequence, Integer>> entryList = new ArrayList<Map.Entry<WordSequence, Integer>>(entries);
        Collections.sort(entryList, new Comparator<Map.Entry<WordSequence, Integer>>() {

            public int compare(Entry<WordSequence, Integer> o1, Entry<WordSequence, Integer> o2) {
                String[] array1 = o1.getKey().getSequence();
                String[] array2 = o2.getKey().getSequence();
                String str1 = StringUtils.join(array1, "|");
                String str2 = StringUtils.join(array2, "|");
                return str1.compareTo(str2);
            }
        });
        return entryList;

    }

    public static List<Map.Entry<WordSequence, Integer>> sortByFreq(Set<Map.Entry<WordSequence, Integer>> entries, final boolean ascending) {

        List<Map.Entry<WordSequence, Integer>> entryList = new ArrayList<Map.Entry<WordSequence, Integer>>(entries);
        Collections.sort(entryList, new Comparator<Map.Entry<WordSequence, Integer>>() {

            public int compare(Entry<WordSequence, Integer> o1, Entry<WordSequence, Integer> o2) {
                int f1 = o1.getValue();
                int f2 = o2.getValue();
                int order = ascending ? 1 : -1;
                int diff = order * (f1 - f2);
                return diff;
            }
        });
        return entryList;

    }

    public static Map<WordSequence, Integer> filter(Map<WordSequence, Integer> ngrams, Filter<Entry<WordSequence, Integer>> filter) {
        System.out.println("Filtering N-grams of size "+ngrams.size()+" with "+filter.getClass().getName());
        Map<WordSequence, Integer> container = new HashMap<WordSequence, Integer>();
        for (Entry<WordSequence, Integer> e : ngrams.entrySet()) {
            if (filter.accepts(e)) {
                WordSequence key = e.getKey();
                Integer value = e.getValue();
                container.put(key, value);
            }

        }
        assert container.size() <= ngrams.size();
        return container;

    }
    ///////////////////////////////////////////////

    public static void main(String[] args) throws Exception {

        File corpusFile = new File("/media/SHARE/QA_project_resources/Best_Corpus/train/news/news_00003.txt");
        BESTHiCharSource charSource = new BESTHiCharSource(corpusFile);
        int maxGram = 4;
        WordGramCounter gramCounter = new WordGramCounter(charSource.iterator(), maxGram);
        Map<WordSequence, Integer> grams = new HashMap<WordSequence, Integer>();
        gramCounter.countGrams(grams);
        
    
        List<Entry<WordSequence, Integer>> entryList =
                sortByFreq(grams.entrySet(), true) //                sortByAlphanet(grams.entrySet())
                ;
        // Print
        for (Map.Entry<WordSequence, Integer> e : entryList) {

            int freq = e.getValue();
            System.out.println(e.getKey() + " : " + freq);
        }

    }
}
