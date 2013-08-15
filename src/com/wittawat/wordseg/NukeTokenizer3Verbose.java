package com.wittawat.wordseg;

import com.aliasi.chunk.Chunker;

import com.wittawat.tcc.TCCTokenizer;
import com.wittawat.wordseg.data.ContentContainer;
import com.wittawat.wordseg.data.ContentContainerImpl;
import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.utils.Pair;
import com.wittawat.wordseg.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * Same as <code>NukeTokenizer3</code> but also records
 * indexes which are not the end of words.
 *
 * Mainly for debugging purpose.
 * 
 * @author Wittawat Jitkrittum
 */
public class NukeTokenizer3Verbose extends NukeTokenizer3 {

    private final int iClassIndex = getNotEndClassIndex();

    public NukeTokenizer3Verbose(String content, Classifier model, FVGenerator fvGenerator, Chunker chunker) {
        super(content, model, fvGenerator, chunker);
    }

    public NukeTokenizer3Verbose(String content, Classifier model, FVGenerator fvGenerator) throws IOException {
        super(content, model, fvGenerator);
    }

    public NukeTokenizer3Verbose(String content, Classifier model) throws IOException {
        super(content, model);
    }

    @Override
    protected void processChunkShift(int from, int to, int eClassIndex, List<Pair<Integer, Double>> endTokenIndexes,
            String source, int endIndexShift, boolean allowConsiderStartBoundary, boolean allowConsiderEndBoundary) {
        from = considerMatchedBoundaries && allowConsiderStartBoundary ? Math.max(from - 1, 0) : from;

        String subContent = source.substring(from, to);
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

            ContentContainer contentContainer = new ContentContainerImpl(source, charIndexes);
            evaluator.setSource(contentContainer);
            Map<Integer, double[]> classProbs = evaluator.getClassProbabilites();

            List<Integer> indexes = new ArrayList<Integer>(classProbs.keySet());
            Collections.sort(indexes);

          
            for (Integer i : indexes) {
                double[] probs = classProbs.get(i);
                int maxIndex = Utils.max(probs);
                assert maxIndex >= 0;
                double prob = -1;
                if (maxIndex == eClassIndex) {
                    prob = probs[eClassIndex];

                } else if (maxIndex == iClassIndex) {
                    prob = 1 - probs[iClassIndex];

                }
                Pair<Integer, Double> pair = new Pair<Integer, Double>(i + 1 + endIndexShift, prob);
                endTokenIndexes.add(pair);

            }
        }
        if (notConsiderEndBoundary) {
            assert lastTCCIndex != null;
            endTokenIndexes.add(new Pair<Integer, Double>(lastTCCIndex + from + endIndexShift, 1.0));
        }
    }

    public static void main(String[] args) throws Exception {
        Classifier model = Actions.loadModel(
                "stacking_full");
        System.out.println("Finished loading model.");
        String content = FileUtils.readFileToString(new File(
                "/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt"));
        NukeTokenizer tok = new NukeTokenizer3Verbose(content, model);

        System.out.println("Start tokenizing");


        //////////// prob tok //////////////
        String probTok = tok.tokenizeWithProb();
        FileUtils.writeStringToFile(new File("/home/nook/Desktop/tok3verbose_prob.txt"), probTok);

    }
}
