package com.wittawat.wordseg;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.wittawat.tcc.TCCTokenizer;
import com.wittawat.wordseg.feature.FeatureUnit;
import com.wittawat.wordseg.sourcable.*;
import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.utils.Pair;
import com.wittawat.wordseg.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import org.apache.commons.io.FileUtils;

/**
 * A fast implementation of NukeTokenizer's.
 * Not intended to be resuable, extensible.
 * Intended to be used as a final tokenizer.
 * 
 * @author Wittawat Jitkrittum
 */
public class FastTokenizer implements BasicWordTokenizer {

    private String sourceContent;
    private SourcableModel model;
    private FeatureUnit[] featureUnits;
    private Chunker chunker;
    private static final int END_CLASS_INDEX = 0;

    public FastTokenizer(String sourceContent, SourcableModel model,
            FeatureUnit[] featureUnits, Chunker chunker) {
        this.sourceContent = sourceContent;
        this.model = model;
        this.featureUnits = featureUnits;
        this.chunker = chunker;
    }

    public FastTokenizer(String sourceContent, SourcableModel model,
            FeatureUnit[] featureUnits) throws IOException {
        this(sourceContent, model, featureUnits, Data.getDefaultChunker());
    }

    public String getSourceContent() {
        return sourceContent;
    }

    private Object[] genRawFeatureVector(String source, int index) {
        Object[] vector = new Object[featureUnits.length];
        for (int i = 0; i < vector.length; ++i) {
            Object v = featureUnits[i].getRawAttributeValue(index);
            vector[i] = v;
        }
        return vector;

    }

    public Iterator<Integer> getCutIndexes(
            String source, int[] charIndexes) {


        List<Integer> cutIndexes = new LinkedList<Integer>();
        for (int i = 0; i < charIndexes.length; ++i) {
            int charIndex = charIndexes[i];
            Object[] vector = genRawFeatureVector(source, charIndex);
            int result = -1;
            try {
                result = (int) model.classifyInstance(vector);
            } catch (Exception ex) {
                ex.printStackTrace();
                result = END_CLASS_INDEX;
            }
            if (result == END_CLASS_INDEX) {
                cutIndexes.add(charIndex);
            }
        }

        return cutIndexes.iterator();
    }

    protected void processChunkShift(int from, int to,
            List<Pair<Integer, Double>> endTokenIndexes, String source, int endIndexShift) {

        String subContent = source.substring(from, to);

        TCCTokenizer tccTok = new TCCTokenizer(subContent);
        Vector<Integer> tccEndIndexes = tccTok.getEndIndexesOrNull();
        Integer lastTCCIndex = tccEndIndexes.remove(tccEndIndexes.size() - 1);
        if (tccEndIndexes.size() > 0) {
            int[] charIndexes = Utils.toArray(tccEndIndexes, from - 1);
            Iterator<Integer> classProbs = getCutIndexes(source, charIndexes);

            while (classProbs.hasNext()) {

                int i = classProbs.next();

                Pair<Integer, Double> newPair = new Pair<Integer, Double>(i + 1 + endIndexShift, 1.0);
                endTokenIndexes.add(newPair);

            }
        }
        endTokenIndexes.add(new Pair<Integer, Double>(lastTCCIndex + from + endIndexShift, 1.0));
    }

    protected List<Pair<Integer, Double>> getEndTokenIndexes(String content) {
        final String newLine = MyStringUtils.detectNewLine(content);

        final int newLineLength = newLine.length();
        final int contentLength = content.length();

        final StringBuilder buf = new StringBuilder();

        Integer[] newLineIndexes = MyStringUtils.findNewLineIndexes(newLine, content, buf);
        String oneLineContent = buf.toString();

        // Set the content to FeatureUnits
        for (FeatureUnit fu : featureUnits) {
            fu.setCharSource(oneLineContent);
        }

        Chunking chunking = chunker.chunk(content);
        Set<Chunk> chunkSet = chunking.chunkSet();

        List<Pair<Integer, Double>> endTokenIndexes = new LinkedList<Pair<Integer, Double>>();
        int i = 0;


        int lastChunkIndex = 0, lineShift = 0;
        for (Chunk chunk : chunkSet) {

            int startChunk = chunk.start();
            int endChunk = chunk.end();

            int lineI = -1;
            int portionStart = lastChunkIndex;

            while (i < newLineIndexes.length && (lineI = newLineIndexes[i]) < startChunk) {
                if (portionStart < lineI) {
                    processChunkShift(portionStart - lineShift,
                            lineI - lineShift,
                            endTokenIndexes, oneLineContent, lineShift);
                }
                portionStart = lineI + newLineLength;
                lineShift += newLineLength;
                ++i;
            }
            assert portionStart <= startChunk;
            if (portionStart < startChunk) {
                processChunkShift(portionStart - lineShift,
                        startChunk - lineShift,
                        endTokenIndexes, oneLineContent, lineShift);
            }

            endTokenIndexes.add(new Pair<Integer, Double>(endChunk, 1.0));
            lastChunkIndex = endChunk;

        }// end chunking for
        int lineI = -1;
        int portionStart = lastChunkIndex;

        while (i < newLineIndexes.length && (lineI = newLineIndexes[i]) < contentLength) {
//                if (portionStart < lineI) {
            processChunkShift(portionStart - lineShift,
                    lineI - lineShift,
                    endTokenIndexes, oneLineContent, lineShift);
//                }
            portionStart = lineI + newLineLength;
            lineShift += newLineLength;
            ++i;
        }
        if (portionStart < contentLength) {
            processChunkShift(portionStart - lineShift,
                    contentLength - lineShift,
                    endTokenIndexes, oneLineContent, lineShift);
        }
        return endTokenIndexes;

    }

    public String tokenize() {
        List<Pair<Integer, Double>> indexes = getEndTokenIndexes(sourceContent);
        StringBuilder buf = new StringBuilder();
        int last = 0;
        for (Pair<Integer, Double> p : indexes) {
            int start = p.getValue1();
            String word = sourceContent.substring(last, start);
            buf.append(word).append('|');
            last = start;
        }
        return buf.toString();
    }

    public void close() {
    }

    /////////////////////////////////////////////////////
    public static void main(String[] args) throws Exception {
        String content = FileUtils.readFileToString(new File(
                "/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt"));

//        BasicWordTokenizer tok = new FastTokenizer(content,
//                new article(), AbstractFVGenerator.getDefaultFeatureUnits());
//
//        System.out.println("Start tokenizing");
//        String tokenized =
//                tok.tokenize();
//
//        tok.close();
//
//        FileUtils.writeStringToFile(new File(
//                "/home/nook/Desktop/fasttok.txt"), tokenized);



    }
}
