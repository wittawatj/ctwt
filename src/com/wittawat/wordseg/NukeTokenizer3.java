package com.wittawat.wordseg;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.wittawat.tcc.TCCTokenizer;
import com.wittawat.wordseg.data.ContentContainer;
import com.wittawat.wordseg.data.ContentContainerImpl;
import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.utils.Pair;
import com.wittawat.wordseg.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * Very similar to <code>NukeTokenizer2</code>
 * but process all content at once (not line by line).
 *
 * Longest matching based on dict ->
 * TCC on the rest text -> use model on each index marked
 * by TCC.
 *
 * @author Wittawat Jitkrittum
 */
public class NukeTokenizer3 extends NukeTokenizer2 {

    /**Set to true to let the model consider the boundaries of words matched
    by the dictionary.*/
    protected boolean considerMatchedBoundaries = true;
    private boolean useDictMatchOnFirstStep = true;

    public NukeTokenizer3(String content, Classifier model) throws IOException {
        super(content, model);
    }
    public NukeTokenizer3(String content, Classifier model, boolean useDictMatchOnFirstStep) throws IOException {
        this(content, model);
        this.useDictMatchOnFirstStep = useDictMatchOnFirstStep;
    }

    public NukeTokenizer3(String content, Classifier model, FVGenerator fvGenerator) throws IOException {
        super(content, model, fvGenerator);
    }

    public NukeTokenizer3(String content, Classifier model, FVGenerator fvGenerator, Chunker chunker) {
        super(content, model, fvGenerator, chunker);

    }

    public boolean isUseDictMatchOnFirstStep() {
        return useDictMatchOnFirstStep;
    }

    public void setUseDictMatchOnFirstStep(boolean useDictMatchOnFirstStep) {
        this.useDictMatchOnFirstStep = useDictMatchOnFirstStep;
    }

    /**Whether boundaries are checked also depend on the class global parameter "considerMatchedBoundaries".*/
    protected void processChunkShift(int from, int to, int eClassIndex,
            List<Pair<Integer, Double>> endTokenIndexes, String targetSource, int endIndexShift,
            boolean allowConsiderStartBoundary,
            boolean allowConsiderEndBoundary) {
        from = considerMatchedBoundaries && allowConsiderStartBoundary ? Math.max(from - 1, 0) : from;

        String subContent = targetSource.substring(from, to);
//        System.out.println(subContent);
        TCCTokenizer tccTok = new TCCTokenizer(subContent);
        Vector<Integer> tccEndIndexes = tccTok.getEndIndexesOrNull();
        Integer lastTCCIndex = null;
        boolean notConsiderEndBoundary = !(considerMatchedBoundaries && allowConsiderEndBoundary);
        if (notConsiderEndBoundary) {
            lastTCCIndex = tccEndIndexes.remove(tccEndIndexes.size() - 1);
        }
        if (tccEndIndexes.size() > 0) {
            int[] charIndexes = Utils.toArray(tccEndIndexes, from - 1);

            ContentContainer contentContainer = new ContentContainerImpl(targetSource, charIndexes);
            evaluator.setSource(contentContainer);
            Map<Integer, double[]> classProbs = evaluator.getClassProbabilites();

            List<Integer> indexes = new ArrayList<Integer>(classProbs.keySet());
            Collections.sort(indexes);

            for (Integer i : indexes) {
                double[] probs = classProbs.get(i);
                int maxIndex = Utils.max(probs);
                if (maxIndex == -1 || maxIndex == eClassIndex) {
                    double prob = probs[eClassIndex];
                    Pair<Integer, Double> pair = new Pair<Integer, Double>(i + 1 + endIndexShift, prob);
                    endTokenIndexes.add(pair);
                }

            }
        }
        if (notConsiderEndBoundary) {
            assert lastTCCIndex != null;
            endTokenIndexes.add(new Pair<Integer, Double>(lastTCCIndex + from + endIndexShift, 1.0));
        }
    }

    @Override
    protected List<Pair<Integer, Double>> getEndTokenIndexes(String content) {
        final String newLine = MyStringUtils.detectNewLine(content);
        final int eClassIndex = getEndClassIndex();
        final int newLineLength = newLine.length();
        final int contentLength = content.length();

        StringBuilder buf = new StringBuilder();

        Integer[] newLineIndexes = MyStringUtils.findNewLineIndexes(newLine, content, buf);
        String oneLineContent = buf.toString();

        Chunking chunking = chunker.chunk(content);

        Set<Chunk> chunkSet = chunking.chunkSet();

        List<Pair<Integer, Double>> endTokenIndexes = new ArrayList<Pair<Integer, Double>>();
        int i = 0;

//        if (chunkSet.size() > 0) {
        int lastChunkIndex = 0, lineShift = 0;
        if (useDictMatchOnFirstStep) {
            for (Chunk chunk : chunkSet) {

                int startChunk = chunk.start();
                int endChunk = chunk.end();

                int lineI = -1;
                int portionStart = lastChunkIndex;

                while (i < newLineIndexes.length && (lineI = newLineIndexes[i]) < startChunk) {
                    if (portionStart < lineI) {
                        processChunkShift(portionStart - lineShift,
                                lineI - lineShift,
                                eClassIndex,
                                endTokenIndexes, oneLineContent,
                                lineShift,
                                // Allow considering the startindex if portionStart == lastChunkIndex,
                                // it means the start portion is the end of last matched dictionary word.
                                portionStart == lastChunkIndex,
                                false);
                    }
                    portionStart = lineI + newLineLength;
                    lineShift += newLineLength;
                    ++i;
                }
                assert portionStart <= startChunk;
                if (portionStart < startChunk) {
                    processChunkShift(portionStart - lineShift,
                            startChunk - lineShift,
                            eClassIndex,
                            endTokenIndexes, oneLineContent,
                            
                            lineShift,
                            portionStart == lastChunkIndex, // Just in case there are two dictionary words (or more) in one line.
                            true);
                }
                if (!considerMatchedBoundaries) {
                    // If not consider the matched boundaries, that means we believe
                    // in the chunker's chunk words, so always add the end of word boundary indexes.
                    // If the boundary is considered, then the next loop will consider this endChunk index.
                    endTokenIndexes.add(new Pair<Integer, Double>(endChunk, 1.0));
                }
                lastChunkIndex = endChunk;

            }// end chunking for
        }

        int lineI = -1;
        int portionStart = lastChunkIndex;

        while (i < newLineIndexes.length && (lineI = newLineIndexes[i]) < contentLength) {
            if (portionStart < lineI) {
                processChunkShift(portionStart - lineShift,
                        lineI - lineShift,
                        eClassIndex,
                        endTokenIndexes, oneLineContent,
                        
                        lineShift,
                        portionStart == lastChunkIndex,
                        false);
            }
            portionStart = lineI + newLineLength;
            lineShift += newLineLength;
            ++i;
        }
        if (portionStart < contentLength) {
            processChunkShift(portionStart - lineShift,
                    contentLength - lineShift,
                    eClassIndex,
                    endTokenIndexes, oneLineContent,
                    
                    lineShift,
                    portionStart == lastChunkIndex,
                    false);
        }
        return endTokenIndexes;
//        } else {
//            // No chunks from dictionary
//            NukeTokenizer tok = new NukeTokenizer(content, model);
//            return tok.getEndTokenIndexes(content);
//        }
        }

    @Override
    public String tokenize() {

        List<Pair<Integer, Double>> indexes = getEndTokenIndexes(content);
        String tokenized = getString(new TokenizeIterator(content, indexes));
        return tokenized;
    }

    @Override
    public String tokenizeWithProb() {
        List<Pair<Integer, Double>> indexes = getEndTokenIndexes(content);
        int lastIndex = 0;
        StringBuilder buf = new StringBuilder();
        for (Pair<Integer, Double> pi : indexes) {
            int i = pi.getValue1();
            double prob = pi.getValue2();

            String word = content.substring(lastIndex, i);
            buf.append(word);
            buf.append("<");
            buf.append(prob);
            buf.append(">");
            lastIndex = i;
        }
        return buf.toString();
    }

    ////////////////////////////////////////
    public static void main(String[] args) throws Exception {
        long start = new Date().getTime();
        Classifier model = Actions.loadModel(
//                "default"
                "stacking_full"
//                "merged_1.4M"
                //                "stack"
                //                "j48"
                //                "article"
                //                "news"
                //                               "stacking_NB_full"
                //                "stacking_randomtree_full"
                //                "stacking_reptree"
                //                "article_fold_1_of_3"
                );
        System.out.println("Finished loading model.");
        String content = FileUtils.readFileToString(new File(
                "/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt" //
                //                "/media/SHARE/QA_project_resources/Best_Corpus/test/article_00161_test.txt"
                //                "/media/SHARE/QA_project_resources/Best_Corpus/test/small.txt"
                //                                "/media/SHARE/QA_project_resources/Best_Corpus/test/BEST2010_100K.txt"
                //                "/media/SHARE/QA_project_resources/Best_Corpus/test/corpus_test/wiki_00030.txt"
                //                "/media/SHARE/QA_project_resources/InterBEST_Corpus/testing/Royalnews.txt"
                //                                "/media/SHARE/QA_project_resources/InterBEST_Corpus/testing/News.txt"
                ));
        NukeTokenizer3 tok = new NukeTokenizer3(content, model);
//        tok.setUseDictMatchOnFirstStep(false);
        
        long end = new Date().getTime();
        System.out.println("Load time: " + (end - start) + " ms");
        System.out.println("Start tokenizing");
        String tokenized =
                tok.tokenize();

        tok.close();

        FileUtils.writeStringToFile(new File(
                //                                "/home/nook/Desktop/TEST_100K_stacking_tok3.txt"

                //                "/home/nook/Desktop/tok3_article_00161.txt"
                "/home/nook/Desktop/tok3.txt" //                "/home/nook/Desktop/tok3_wiki_00030.txt"
                //                                "/home/nook/Desktop/tok3_royalnews.txt"
                //                "/home/nook/Desktop/tok3_news.txt"
                ), tokenized);

        //////////// prob tok //////////////
//        String probTok = tok.tokenizeWithProb();
//        FileUtils.writeStringToFile(new File("/home/nook/Desktop/tok3_prob.txt"), probTok);
    }
}
