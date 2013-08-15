package com.wittawat.wordseg.word;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

/**
 * A class to generate word statistics from BEST corpus.
 */
public class WordStatsGen {

    public static final String Key_VALUE_SEP = "\t";

    public static void writeMap(Map map, File dest) throws IOException {
        List wordList = new ArrayList(map.keySet());
        Collections.sort(wordList);
        BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
        for (Object w : wordList) {
            Object value = map.get(w);
            writer.write(w + Key_VALUE_SEP + value + "\n");

        }
        writer.close();
    }

    public static Map<String, Integer> loadHistogram(File source)
            throws IOException {
        Map<String, Integer> histogram = new HashMap<String, Integer>();
        LineIterator li = FileUtils.lineIterator(source);
        try {
            while (li.hasNext()) {
                String line = li.nextLine();
                String[] parts = line.split(Pattern.quote(Key_VALUE_SEP));
                String word = parts[0].trim();
                String freqStr = parts[1].trim();
                int freq = Integer.parseInt(freqStr);
                histogram.put(word, freq);
            }
        } finally {
            LineIterator.closeQuietly(li);
        }
        return histogram;

    }

    public static Map<String, Float> loadWordFloatMap(File source){
        Map<String, Float> map = new HashMap<String, Float>();

        LineIterator li;
        try {
            li = FileUtils.lineIterator(source);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            while (li.hasNext()) {
                String line = li.nextLine();
                String[] parts = line.split(Pattern.quote(Key_VALUE_SEP));
                String word = parts[0];
                String valueStr = parts[1];
                float value = Float.parseFloat(valueStr);
                map.put(word, value);
            }
        } finally {
            LineIterator.closeQuietly(li);
        }
        return map;

    }

    public static Map<String, Integer> genHistogram(File corpusDir,
            boolean onlyThaiWords) {
        Collection<File> textFiles = FileUtils.listFiles(corpusDir,
                new String[]{"txt"}, true);
        Map<String, Integer> histogram = new HashMap<String, Integer>();
        for (File file : textFiles) {
            try {
                String text = FileUtils.readFileToString(file, "UTF-8");
                String[] words = text.split(Pattern.quote("|"));
                for (String w : words) {
                    w = w.trim();
                    w = Pattern.compile("[\n\r\f]", Pattern.DOTALL).matcher(w).replaceAll("");

                    w = Pattern.compile("\\Q<\\E/?(NE|POEM|AB)\\Q>\\E").matcher(w).replaceAll("");

                    if (!w.equals("")) {
                        if (!onlyThaiWords ||
                                (w.matches("[\u0E00-\u0E4F\\s\\Q.\\E,\\Q-\\E/]+") && !w.matches("[\\s\\Q.\\E,\\Q-\\E/].*"))) { //From Thai unicode chart
                            if (histogram.containsKey(w)) {
                                Integer freq = histogram.get(w);
                                assert freq != null;
                                histogram.put(w, freq + 1);
                            } else {
                                histogram.put(w, 1);
                            }
                        }

                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return histogram;
    }

    public static Integer sumFrequency(Map<String, Integer> histogram) {
        int sum = 0;
        for (Integer v : histogram.values()) {
            sum += v;
        }
        return sum;
    }

    public static Map<String, Float> genSuffixProportionMap(
            Collection<String> words, int maxSuffix) {

        TreeSet<String> revWords = new TreeSet<String>();
        for (String w : words) {
            String rev = StringUtils.reverse(w);
            revWords.add(rev);
        }
        Map<String, Float> revPrefixMap = genPrefixProportionMap(revWords, maxSuffix);
        Map<String, Float> suffixMap = new HashMap<String, Float>();
        for (Entry<String, Float> e : revPrefixMap.entrySet()) {
            String revWord = e.getKey();
            float proportion = e.getValue();
            String word = StringUtils.reverse(revWord);
            suffixMap.put(word, proportion);
        }
        return suffixMap;

    }

    public static Map<String, Float> genPrefixProportionMap(
            Collection<String> words, int maxPrefix) {

        List<String> sortedWords = new ArrayList<String>(words);
        Collections.sort(sortedWords);

        Map<String, Float> prefixMap = new HashMap<String, Float>();

        for (int wIndex = 0; wIndex < sortedWords.size(); ++wIndex) {
            String w = sortedWords.get(wIndex);
            int endIndex = -1;
            for (int p = Math.min(1, w.length()); p <= Math.min(maxPrefix, w.length()); ++p) {
                String prefix = w.substring(0, p);
                if (!prefixMap.containsKey(prefix)) {
                    int end = endIndex == -1 ? sortedWords.size() - 1
                            : endIndex;
                    endIndex = wIndex;
                    /** Define new end */
                    for (int i = wIndex; i <= end; ++i) {
                        if (!sortedWords.get(i).startsWith(prefix)) {
                            break;
                        }
                        endIndex = i;
                    }
                    int wordWithPrefix = endIndex - wIndex + 1;
                    assert wordWithPrefix >= 1;
                    float prefixProportion = (float) wordWithPrefix / (float) sortedWords.size();
                    prefixMap.put(prefix, prefixProportion);

                }
            }
        }
        return prefixMap;
    }

    public static Map<String, Integer> genHistogram(String corpusDirPath,
            boolean onlyThaiWords) {
        return genHistogram(new File(corpusDirPath), onlyThaiWords);
    }

    // ///////////
    public static void main(String[] args) throws Exception {
        File f = new File("/home/nook/Desktop/BEST_histogram_no_tags.txt");
//		 String corpusPath =
//		 "/media/SHARE/QA_project_resources/InterBEST_Corpus/release1";
//		 Map<String, Integer> histogram = genHistogram(corpusPath, true);


//		 writeMap(histogram, f);

        //----------
        Map<String, Integer> loadedHist = loadHistogram(f);
//		Map<String, Float> prefixProportion = genPrefixProportionMap(
//				loadedHist.keySet(), 4);
//		File prefixFile = new File("/home/nook/Desktop/BEST_prefix_map.txt");
//		writeMap(prefixProportion, prefixFile);
        //----------
        Map<String, Float> suffixProportion = genSuffixProportionMap(
                loadedHist.keySet(), 4);
        File suffixFile = new File("/home/nook/Desktop/BEST_suffix_map.txt");
        writeMap(suffixProportion, suffixFile);
    }
}
