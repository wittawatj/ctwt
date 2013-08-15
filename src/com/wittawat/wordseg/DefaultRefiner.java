package com.wittawat.wordseg;

import com.aliasi.lm.CharSeqCounter;
import com.wittawat.wordseg.feature.SeparateRatioFU;
import com.wittawat.wordseg.utils.Pair;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Default implementation of <code>ResultRefiner</code>.
 * Modify many obvious errors often occurred.
 * A list of modifications are as follows:
 *
 * (do later)......
 * ...
 * @author Wittawat Jitkrittum
 */
public class DefaultRefiner implements ResultRefiner {

    private final SeparateRatioFU sepRatioFU;
//    private final RightCondProbFU rProbFU;
//    private final LeftCondProbFU lProbFU;
    /**If the probability at any character index is
    >= this value, it is treated as a point that
    should be separated. 
     */
    private CharSeqCounter charCounter;
    private double cutProbThreshold = 0.75;

    public DefaultRefiner(CharSeqCounter counter) {
        sepRatioFU = new SeparateRatioFU("sepRatio",
                2, 2,
                counter);
        // Make the sepRaio as probabilities
        sepRatioFU.setNormalized(true);
        this.charCounter = counter;
//        rProbFU = new RightCondProbFU("rProb", counter, 4, '|');
//        lProbFU = new LeftCondProbFU("lProb", counter, 4, '|');
    }

    public DefaultRefiner() throws FileNotFoundException, IOException {
        this(Data.getCharGramsTrie());
    }

    /**@return true if the prob is within the considering range.*/
    private boolean isInConsiderRange(double prob) {
        assert prob <= 1;
        assert prob >= 0;
        return Math.abs(prob - 0.5) <= 0.1;
    }

    private void checkSeparationRatio(Pair<Integer, Double> pairI) {
        double probI = pairI.getValue2();
        if (isInConsiderRange(probI)) {
            final int strI = pairI.getValue1();

            // Consider separation ratio
            double sepRatio = sepRatioFU.getAttributeValue(strI);
            assert sepRatio >= 0;

            if (sepRatio >= cutProbThreshold) {
                // A Cut is confirmed by sepRatio
                if (probI < sepRatio) {

                    pairI.setValue2(sepRatio);
                }
            } else {
                // No cut from sepRatio
                if (probI >= sepRatio) {
                    pairI.setValue2(sepRatio);
                }
            }
        }
    }

    private TokResult checkLoneWords(TokResult tokResult) {
        final String content = tokResult.getContent();
        final List<Pair<Integer, Double>> indexes = tokResult.getProbIndexes();
        final int length = indexes.size();
        // Check for words which have length of 1

        final int STRETCH_CHARS = 2;
        final int CONSIDER_LENGTH = STRETCH_CHARS + 1;
        if (length >= 1) {

            Pair<Integer, Double> curPair = null;
            int curStrI = 0;

            for (int i = 0; i < length; ++i) {
                Pair<Integer, Double> nextPair = indexes.get(i);
                int nextStrI = nextPair.getValue1();
                assert nextStrI > curStrI;
                if (nextStrI == curStrI + 1) {
                    char loneWord = content.charAt(curStrI);
                    if (Character.isLetter(loneWord)) {
                        // Stretch left
                        String left = "";
                        final int endLeft = Math.max(0, curStrI - STRETCH_CHARS);
                        for (int j = curStrI; j >= endLeft; --j) {
                            left = content.charAt(j) + left;
                        }
                        if (left.length() == CONSIDER_LENGTH) {
                            // Stretch right
                            String right = "";
                            final int endRight = Math.min(content.length() - 1, curStrI + STRETCH_CHARS);
                            for (int j = curStrI; j <= endRight; ++j) {
                                right += content.charAt(j);
                            }
                            assert left.charAt(left.length() - 1) == right.charAt(0);

                            if (right.length() == CONSIDER_LENGTH) {
                                long leftCount = charCounter.count(left.toCharArray(), 0, CONSIDER_LENGTH);
                                long rightCount = charCounter.count(right.toCharArray(), 0, CONSIDER_LENGTH);
                                if (leftCount > rightCount) {
                                    if (curPair != null) {
                                        curPair.setValue2(0.0D);
                                    }
                                } else if (rightCount > leftCount) {
                                    nextPair.setValue2(0.0D);
                                }
                            }
                        }

                    }
                }
            }
        }
        return tokResult;

    }

    public TokResult refine(TokResult tokResult) {

        final String content = tokResult.getContent();
        final List<Pair<Integer, Double>> indexes = tokResult.getProbIndexes();
        final int length = indexes.size();

//        sepRatioFU.setCharSource(content);
//        for (int i = 0; i < length; ++i) {
//            Pair<Integer, Double> pairI = indexes.get(i);
//            checkSeparationRatio(pairI);
//
//        }

        tokResult = checkLoneWords(tokResult);

        return tokResult;
    }
}
