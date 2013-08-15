package com.wittawat.wordseg;

import com.wittawat.wordseg.feature.FVGenerator;
import com.wittawat.wordseg.feature.ClassUnit;
import com.wittawat.tcc.TCCTokenizer;
import com.wittawat.wordseg.data.ContentContainer;
import com.wittawat.wordseg.data.ContentContainerImpl;
import com.wittawat.wordseg.feature.AbstractFVGenerator;
import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.utils.Pair;
import com.wittawat.wordseg.utils.Utils;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import weka.classifiers.Classifier;

/**
 * Intended to be used by end users. For each line,
 * -> TCC -> use model to predict each index marked by TCC.
 * 
 * @author Wittawat Jitkrittum
 */
public class NukeTokenizer extends AbstractWordTokenizer {

    protected Classifier model;
    protected FVGenerator fvGenerator;
    protected ModelBasedCharLevelEvaluator evaluator;

    public NukeTokenizer(String content, Classifier model, FVGenerator fvGenerator) {
        super(content);
        this.model = model;
        this.fvGenerator = fvGenerator;
        evaluator = new ModelBasedCharLevelEvaluator(model, fvGenerator);
    }

    public NukeTokenizer(String content, Classifier model) {

        this(content, model, AbstractFVGenerator.getDefaultFVGenerator());
    }

    @Override
    public Iterator<String> iterateTokens() {
        content = MyStringUtils.removeAllNewLines(content);
        return iterateTokens(content);
    }

    @Override
    public String tokenize() {
        String newLine = MyStringUtils.detectNewLine(content);
        LineIterator it = null;

        StringBuilder buf = new StringBuilder();
        try {
            it = new LineIterator(new StringReader(content));
            int i = 0;
            while (it.hasNext()) {
                String line = it.nextLine();
                if (!line.equals("")) {

//                    System.out.println("new line:");
                    List<Pair<Integer, Double>> indexes = getEndTokenIndexes(line);
                    String tokenized = getString(new TokenizeIterator(line, indexes));
//                    System.out.println(tokenized);
                    buf.append(tokenized);
                }
                buf.append(newLine);
//                System.out.printf("Line %d processed.\n", ++i);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        return buf.toString();
    }

    public String tokenizeWithProb() {
        String newLine = MyStringUtils.detectNewLine(content);
        LineIterator it = null;

        StringBuilder buf = new StringBuilder();
        try {
            it = new LineIterator(new StringReader(content));
            while (it.hasNext()) {
                String line = it.nextLine();
                if (!line.equals("")) {

                    int start = 0;
                    List<Pair<Integer, Double>> indexes = getEndTokenIndexes(line);
                    for (Pair<Integer, Double> p : indexes) {
                        int end = p.getValue1();
                        double prob = p.getValue2();
                        String w = line.substring(start, end);
                        buf.append(w);
                        if (prob >= 0.5) {
                            buf.append("<").append(prob).append(">");
                        }

                        start = end;
                    }

                }
                buf.append(newLine);
            }
        } finally {
            LineIterator.closeQuietly(it);
        }

        return buf.toString();
    }

    protected int getClassValueIndex(String value) {
        ClassUnit cu = fvGenerator.getClassUnit();
        String[] values = cu.getPossibleValues();
        for (int i = 0; i < values.length; ++i) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        throw new RuntimeException("ClassUnit must have one value for " + value + ".");
    }

    protected int getNotEndClassIndex() {
        return getClassValueIndex(ClassUnit.CLASS_I);
    }

    protected int getEndClassIndex() {
        return getClassValueIndex(ClassUnit.CLASS_E);
    }

    
    @Override
    protected List<Pair<Integer, Double>> getEndTokenIndexes(String content) {
        // Assume that the content has no new lines
        TCCTokenizer tccTok = new TCCTokenizer(content);
//        System.out.println("TCC: "+tccTok.tokenizeOrNull());
        Vector<Integer> tccEndIndexes = tccTok.getEndIndexesOrNull();
        tccEndIndexes.remove(tccEndIndexes.size() - 1);

        List<Pair<Integer, Double>> endTokenIndexes = new ArrayList<Pair<Integer, Double>>();
        if (tccEndIndexes.size() > 0) {

            final int eClassIndex = getEndClassIndex();
            if (tccEndIndexes != null) {
                int[] charIndexes = Utils.toArray(tccEndIndexes, -1);
                ContentContainer contentContainer = new ContentContainerImpl(content, charIndexes);
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

        }
        endTokenIndexes.add(new Pair<Integer, Double>(content.length(), 1.0));
        return endTokenIndexes;

    }

    public void close() {
        fvGenerator.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    ////////////////////////////////////////
    public static void main(String[] args) throws Exception {
        Classifier model = Actions.loadModel(
                "stacking_full" //                "article_fold_3_of_3"
                //                "stacking_NB"
                //                    "stacking_J48"
                );


        String content = FileUtils.readFileToString(new File("/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt"));
        NukeTokenizer tok = new NukeTokenizer(content, model);
        String tokenized =
                tok.tokenize();
//                tok.tokenizeWithProb(content);
        tok.close();
//        System.out.println(tokenized);
        FileUtils.writeStringToFile(new File("/home/nook/Desktop/tok1.txt"), tokenized);

    }
}
