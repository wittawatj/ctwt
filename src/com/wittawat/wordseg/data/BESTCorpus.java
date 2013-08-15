package com.wittawat.wordseg.data;

import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.word.WordGramCounter;
import com.wittawat.wordseg.word.AllWordFilter;
import com.wittawat.wordseg.word.AndFilter;
import com.wittawat.wordseg.word.RegexFilter;
import com.wittawat.wordseg.word.Filter;

import com.wittawat.wordseg.word.GramFilterAdapter;
import com.wittawat.wordseg.word.GramNumEqualFilter;
import com.wittawat.wordseg.word.MaxWordLengthFilter;
import com.wittawat.wordseg.word.MinFreqGramFilter;
import com.wittawat.wordseg.word.OrFilter;
import com.wittawat.wordseg.word.ThaiCharFilter;
import com.wittawat.wordseg.word.WordAtLeastLengthFilter;
import com.wittawat.wordseg.word.WordSequenceFilter;
import com.wittawat.wordseg.word.WordSequence;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 * Helper class for BEST, Thai word segmentation corpus.
 * @author Wittawat Jitkrittum
 */
public class BESTCorpus {

    public static final String TAG_NE_START = "<NE>";
    public static final String TAG_NE_END = "</NE>";
    public static final String TAG_AB_START = "<AB>";
    public static final String TAG_AB_END = "</AB>";
    public static final String TAG_POEM_START = "<POEM>";
    public static final String TAG_POEM_END = "</POEM>";
    public static final Pattern WRONG_TAG_PATTERN = Pattern.compile(genTagMistakes("NE") + "|" + genTagMistakes("AB") + "|" + genTagMistakes("POEM"));
    public static final Pattern TAG_PATTERNS = Pattern.compile(TAG_NE_START + "|" + TAG_NE_END + "|" + TAG_AB_START + "|" + TAG_AB_END + "|" + TAG_POEM_START + "|" + TAG_POEM_END);
    public static final String DELIMITER = "|";

    public static Hashtable<WordSequence, Integer> gatherNGrams(File corpusFolder, final int maxGrams, int nThreads) {
        return gatherNGrams(corpusFolder, maxGrams, nThreads, null);
    }

    public static Hashtable<WordSequence, Integer> gatherNGrams(File corpusFolder,
            final int maxGrams,
            int nThreads, final WordSequenceFilter filter) {
        final Hashtable<WordSequence, Integer> container = new Hashtable<WordSequence, Integer>();
        Vector<File> files = new Vector<File>(FileUtils.listFiles(corpusFolder, new String[]{"txt"}, true));

        // Parallel processing
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        int fileCount = files.size();
        final CountDownLatch latch = new CountDownLatch(fileCount);
        for (int i = 0; i < fileCount; ++i) {
            final File f = files.get(i);
            threadPool.execute(new Runnable() {

                public void run() {
                    try {
                        System.out.println("gathering N-gram from: " + f.getAbsolutePath());
                        String content = FileUtils.readFileToString(f);
                        content = MyStringUtils.removeAllNewLines(content);
                        BESTHiCharSource charSource = new BESTHiCharSource(new MemoryWordIterator(content));
                        WordGramCounter counter = new WordGramCounter(charSource.iterator(), maxGrams);
                        counter.countGrams(container, filter);

                        latch.countDown();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

        }
        try {
            latch.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        threadPool.shutdown();


        return container;
    }

    public static Hashtable<WordSequence, Integer> gatherNGrams(File corpusFolder, final int maxGrams) {
        return gatherNGrams(corpusFolder, maxGrams, 15);
    }

    public static void gatherWords(Set<String> container, String content, String delimiterRegex, Filter filter) {

        String[] words = content.split(delimiterRegex, -2);
        for (String w : words) {

            w = w.trim();
            if (!w.equals("")) {
                if (filter.accepts(w)) {
                    w = MyStringUtils.cleanBESTTags(w);
                    container.add(w);
                }
            }
        }

    }

    /**Exclude spaces. Remove BEST tags.*/
    public static void gatherWords(Set<String> container, String content, String delimiterRegex) {
        gatherWords(container, content, delimiterRegex, AllWordFilter.ACCEPT_ALL_FILTER);


    }

    public static boolean containBESTTags(String content) {
        return TAG_PATTERNS.matcher(content).find();
    }

    private static String genTagMistakes(String tagName) {
        return "<" + tagName + "[^>]|[^</]" + tagName + ">|[^<]/" + tagName + ">|</" + tagName + "[^>]";
    }

    public static Set<String> gatherCorpusWords(File folder, Filter filter) throws IOException {
        String delimiterRegex = Pattern.quote("|");
        Iterator<File> files = FileUtils.iterateFiles(folder, new String[]{"txt"}, true);
        Set<String> wordSet = new HashSet<String>();
        while (files.hasNext()) {

            File f = files.next();
            System.out.println("Gathering words from: " + f.getAbsolutePath());
            String content = FileUtils.readFileToString(f);
            gatherWords(wordSet, content, delimiterRegex, filter);
        }
        return wordSet;
    }

    public static Set<String> gatherCorpusWords(File folder) throws IOException {
        return gatherCorpusWords(folder, AllWordFilter.ACCEPT_ALL_FILTER);
    }

    public static void writeCorpusWords(Set<String> words, File dest) throws IOException {
        List<String> sorted = new ArrayList<String>(words);
//        Collections.sort(sorted);

        Collections.sort(sorted, new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }
        });
        FileUtils.writeLines(dest, sorted);
    }

    /**Check BEST corpus against a set of specification in
    the manual.*/
    public static void checkBESTCorpusStructure(File f) throws IOException {
        List<String> lines = FileUtils.readLines(f);
        int lineCount = lines.size();

        //Detect line without | at the end and no | at the beginning of the next line
        for (int i = 0; i < lineCount - 1; ++i) {
            String line = lines.get(i);
            if (!line.trim().equals("")) {
                String nextLine = lines.get(i + 1);
                if (!line.endsWith("|") && !nextLine.startsWith("|")) {
                    String linePortion = line.substring(Math.max(line.length() - 12, 0));
                    System.out.println(f.getName() + ":" + (i + 1) + ":" + linePortion + "   (No | at the end of line)");
                }
            }

        }

        // Wield characters, wrong tags
        for (int i = 0; i < lineCount; ++i) {
            String line = lines.get(i);

            // Check strangs characters
            if (line.contains("﻿")) {
                System.out.println(f.getName() + ":" + (i + 1) + "   (Contain a strange character)");
            }

            // Wrong tags
            Matcher m = WRONG_TAG_PATTERN.matcher(line);
            while (m.find()) {
                String portion = line.substring(Math.max(0, m.start() - 10), Math.min(line.length() - 1, m.end() + 10));
                System.out.println(f.getName() + ":" + (i + 1) + ":" + portion + "   (Wrong tag format).");
            }

            // No open/close tags
            String removed = MyStringUtils.removeBESTTags(line);
            if (containBESTTags(removed)) {
                System.out.println(f.getName() + ":" + (i + 1) + "   (No open/close tags)");
            }
        }

    }

    public static void checkWholeBESTCorpusStructure(File root) throws IOException {
        Collection<File> files = FileUtils.listFiles(root, new String[]{"txt"}, true);
        for (File f : files) {
            checkBESTCorpusStructure(f);
        }
    }

    public static void replaceWholeCorpus(String targetRegex, String replace, File corpusRoot) throws IOException {
        Iterator<File> files = FileUtils.iterateFiles(corpusRoot, new String[]{"txt"}, true);
        while (files.hasNext()) {
            File f = files.next();
            String content = FileUtils.readFileToString(f);
            content = content.replaceAll(targetRegex, replace);

            FileUtils.writeStringToFile(f, content);
        }

    }

    public static void serializeNGrams(Map<WordSequence, Integer> ngrams, File dest) throws FileNotFoundException, IOException {
        System.out.println("Serializing N-grams of size " + ngrams.size() + " to " + dest.getAbsolutePath());
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dest));
        oos.writeObject(ngrams);
        oos.close();
        System.out.println("Serialization done.");

    }

    public static Map<WordSequence, Integer> deserializeNGrams(File file) throws IOException, FileNotFoundException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Map<WordSequence, Integer> ngrams = (Map<WordSequence, Integer>) ois.readObject();
        return ngrams;
    }

    /**@return a set of x-gram words where x > 1 which
     * if concatenated, can form a meaningful unigram.
     * The returned set of words should not be in the dictionary used
    for tokenization.*/
    public static Map<WordSequence, Integer> getAmbiguousWords(Map<WordSequence, Integer> grams) {
        Map<WordSequence, Integer> ambGrams = new HashMap<WordSequence, Integer>();
        for (Entry<WordSequence, Integer> e : grams.entrySet()) {
            WordSequence ws = e.getKey();

            if (ws.length() >= 2) {


                String[] words = ws.getSequence();
                StringBuilder buf = new StringBuilder();
                for (String w : words) {
                    buf.append(w);
                }
                String merged = buf.toString();

                WordSequence mergedSeq = new WordSequence(merged);
                Integer mergedFreq = grams.get(mergedSeq);

                if (mergedFreq != null) {
                    assert mergedFreq >= 1;
                    ambGrams.put(mergedSeq, mergedFreq);
                }
            }
        }
        return ambGrams;

    }
    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    public static void gatherCorpusWordsToFiles(File corpusRoot) throws IOException {
        AndFilter<String> fil = new AndFilter<String>(new WordAtLeastLengthFilter(2), new ThaiCharFilter());
        writeCorpusWords(gatherCorpusWords(corpusRoot, new AndFilter<String>(new WordAtLeastLengthFilter(5), RegexFilter.NE_TAG_FILTER)), new File("data/BEST_NE.txt"));
        writeCorpusWords(gatherCorpusWords(corpusRoot, new AndFilter<String>(fil, RegexFilter.AB_TAG_FILTER)), new File("data/BEST_AB.txt"));
        writeCorpusWords(gatherCorpusWords(corpusRoot, new AndFilter<String>(fil, RegexFilter.POEM_TAG_FILTER)), new File("data/BEST_POEM.txt"));
        writeCorpusWords(gatherCorpusWords(corpusRoot, new AndFilter<String>(fil, RegexFilter.NO_TAG_FILTER)), new File("data/BEST_other.txt"));
    }
    /////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {
        File corpusRoot = new File("/media/SHARE/QA_project_resources/Best_Corpus/train");
        File serializedNGrams = new File("data/4_grams_full.obj");


////        // Gather 4-gram words
////        // Gather only grams with word length <= 40
//        Map<WordSequence, Integer> container = gatherNGrams(corpusRoot, 4, 15, new MaxWordLengthFilter(40));
//
//        System.out.println("N-grams count before filter: "+container.size());
//        // Filter only grams with frequency >= 4 , retain all 1-gram
//        container = WordGramCounter.filter(container
//                ,new OrFilter<Map.Entry<WordSequence, Integer>>( new GramFilterAdapter(new GramNumEqualFilter(1)),  new MinFreqGramFilter(3) )
////                , new AllWordFilter(true)
//                );
//        System.out.println("N-grams count after filter: "+container.size());
//        // Serialize
//        serializeNGrams(container, serializedNGrams);

        // Print
//        Map<WordSequence, Integer> container = getAmbiguousWords(deserializeNGrams(serializedNGrams));
//        List<Map.Entry<WordSequence, Integer>> entryList = WordGramCounter.sortByFreq(container.entrySet(), false);
//        StringBuilder buf = new StringBuilder();
//        for (Map.Entry<WordSequence, Integer> e : entryList) {
//
//            int freq = e.getValue();
//            buf.append(e.getKey() + " : " + freq).append("\n");
//        }
//
//        FileUtils.writeStringToFile(new File("/home/nook/Desktop/ambiguous.txt"), buf.toString());


        Set<String> words = gatherCorpusWords(new File("/media/SHARE/QA_project_resources/Best_Corpus/train"),RegexFilter.NO_TAG_FILTER);
        List<String> l = new Vector<String>(words);
        Collections.sort(l , new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o1.length() - o2.length();
            }

        });
        FileUtils.writeLines(new File("data/BEST_other.txt"), l);
    }
}
