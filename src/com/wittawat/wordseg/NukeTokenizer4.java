package com.wittawat.wordseg;

import com.aliasi.chunk.Chunker;
import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.utils.Pair;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * The whole process is the same as <code>NuleTokenizer3</code>.
 * That is, Longest matching using dict -> TCC on the rest ->
 * use model to analyze at TCC reported indexes.
 *
 * The difference is that indexes not supposed to be tokenized
 * are also reported (with low probabilities).
 * Also, the specified <code>ResultRefiner</code> is applied
 * before returning the tokenized result.
 *
 * @author Wittawat Jitkrittum
 */
public class NukeTokenizer4 extends NukeTokenizer3Verbose {

    private ResultRefiner refiner = new GoThroughRefiner();

    public NukeTokenizer4(String content, Classifier model) throws IOException {
        super(content, model);
    }

    public NukeTokenizer4(String content, Classifier model, FVGenerator fvGenerator) throws IOException {
        super(content, model, fvGenerator);
    }

    public NukeTokenizer4(String content, Classifier model, FVGenerator fvGenerator, Chunker chunker) {
        super(content, model, fvGenerator, chunker);
    }

    public ResultRefiner getRefiner() {
        return refiner;
    }

    public void setRefiner(ResultRefiner refiner) {
        this.refiner = refiner;
    }

    @Override
    protected List<Pair<Integer, Double>> getEndTokenIndexes(String content) {
        List<Pair<Integer, Double>> indexes = super.getEndTokenIndexes(content);
        TokResult result = new TokResult(indexes, content);
        return refiner.refine(result).getProbIndexes();
    }

    public static void main(String[] args) throws Exception {
        Classifier model = Actions.loadModel("default");
        String content = FileUtils.readFileToString(new File(
                //"/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt")
                "/home/nuke/Desktop/seg.txt"
                ));
        NukeTokenizer4 tok = new NukeTokenizer4(content, model);
        tok.setRefiner(new EndOfLineRefiner());
        tok.setUseDictMatchOnFirstStep(false);
        
        System.out.println("Start tokenizing");
        String tokenized =
                tok.tokenize();

        tok.close();

        FileUtils.writeStringToFile(new File("/home/nuke/Desktop/tok4.txt"), tokenized);


    }
}
