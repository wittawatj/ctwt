package com.wittawat.wordseg;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.wittawat.tcc.TCCTokenizer;
import com.wittawat.wordseg.data.ContentContainer;
import com.wittawat.wordseg.data.ContentContainerImpl;
import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.feature.AbstractFVGenerator;
import com.wittawat.wordseg.utils.Pair;
import com.wittawat.wordseg.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * For each line, -> Longest matching based on dict ->
 * TCC on the rest text -> use model on each index marked
 * by TCC.
 * @author Wittawat Jitkrittum
 */
public class NukeTokenizer2 extends NukeTokenizer {

    protected Chunker chunker;

    public NukeTokenizer2(String content, Classifier model, FVGenerator fvGenerator, Chunker chunker) {
        super(content, model, fvGenerator);

        this.chunker = chunker;

    }

    public NukeTokenizer2(String content, Classifier model, FVGenerator fvGenerator) throws IOException {
        this(content, model, fvGenerator, Data.getDefaultChunker());
    }

    public NukeTokenizer2(String content, Classifier model) throws IOException {
        this(content, model, AbstractFVGenerator.getDefaultFVGenerator());
    }

    
    protected void processChunk(int from, int to, final int eClassIndex, List<Pair<Integer, Double>> endTokenIndexes, String source) {
        String subContent = source.substring(from, to);
        TCCTokenizer tccTok = new TCCTokenizer(subContent);
        Vector<Integer> tccEndIndexes = tccTok.getEndIndexesOrNull();
        Integer lastTCCIndex = tccEndIndexes.remove(tccEndIndexes.size() - 1);
        if (tccEndIndexes.size() > 0) {
            int[] charIndexes = Utils.toArray(tccEndIndexes, from - 1);

            ContentContainer contentContainer = new ContentContainerImpl(source, charIndexes);
            evaluator.setSource(contentContainer);
            Map<Integer, double[]> classProbs = evaluator.getClassProbabilites();

            List<Integer> indexes = new ArrayList<Integer>(classProbs.keySet());
            Collections.sort(indexes);

            for (Integer i : indexes) {
                double[] probs = classProbs.get(i);
                int maxIndex = Utils.max(probs);
                if (maxIndex == -1 || maxIndex == eClassIndex) {
                    double prob = probs[eClassIndex];
                    Pair<Integer, Double> pair = new Pair<Integer, Double>(i + 1, prob);
                    endTokenIndexes.add(pair);
                }

            }
        }
        endTokenIndexes.add(new Pair<Integer, Double>(lastTCCIndex + from, 1.0));

    }

    @Override
    protected List<Pair<Integer, Double>> getEndTokenIndexes(String content) {
        // Assume that the content has no new lines

        List<Pair<Integer, Double>> endTokenIndexes = new ArrayList<Pair<Integer, Double>>();

        Chunking chunking = chunker.chunk(content);
        final int eClassIndex = getEndClassIndex();

        Set<Chunk> chunkSet = chunking.chunkSet(); // Sortedset ? -> Yes
        if (chunkSet.size() > 0) {
            int lastChunkIndex = 0;
            for (Chunk chunk : chunkSet) {
                int start = chunk.start();
                int end = chunk.end();

//            System.out.println(chunk + " : " + content.substring(start, end));
                if (lastChunkIndex < start) {
                    processChunk(lastChunkIndex, start, eClassIndex, endTokenIndexes, content);

                    
                }
                endTokenIndexes.add(new Pair<Integer, Double>(end, 1.0));
                lastChunkIndex = end;
            }
            int contentLength = content.length();
            if (lastChunkIndex < contentLength) {
                processChunk(lastChunkIndex, contentLength, eClassIndex, endTokenIndexes, content);
            }
        } else {
            // NO chunk detected
            return super.getEndTokenIndexes(content);

        }
//        endTokenIndexes.add(new Pair<Integer, Double>(content.length(), 1.0));
        return endTokenIndexes;
    }

  
    ////////////////////////////////////////

    public static void main(String[] args) throws Exception {
        Classifier model = Actions.loadModel(
                "stacking_reptree"
//                "article_fold_3_of_3"
//                "stacking_NB"
//                "stacking_J48"
                );


        String content = FileUtils.readFileToString(new File(
                "/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt"
//                "/media/SHARE/QA_project_resources/Best_Corpus/test/BEST2010_100K.txt"
                ));
        NukeTokenizer tok = new NukeTokenizer2(content, model);
        String tokenized =
                tok.tokenize();

        tok.close();

        FileUtils.writeStringToFile(new File(
                "/home/nook/Desktop/TEST_100K_stacking_tok2.txt"
//                "/home/nook/Desktop/TEST_100K_round2_stacking_result_dict.txt"
                ), tokenized);

    }
}
