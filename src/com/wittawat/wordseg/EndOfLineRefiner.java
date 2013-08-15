package com.wittawat.wordseg;

import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.utils.Pair;
import com.wittawat.wordseg.utils.Utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Always put a tokenization mark before the end of line
 * @author Wittawat Jitkrittum
 */
public class EndOfLineRefiner implements ResultRefiner {

    public TokResult refine(TokResult tokResult) {
        String content = tokResult.getContent();
        final String newLine = MyStringUtils.detectNewLine(content);
        final int contentLength = content.length();
        List<Pair<Integer, Double>> indexes = tokResult.getProbIndexes();
        assert Utils.strictAscSorted(indexes);
        final PairValue1Comparator<Double> COMPARATOR = new PairValue1Comparator<Double>();
        for (int lastFound = 0; lastFound < contentLength;) {

            int newLineIndex = content.indexOf(newLine, lastFound);

            Pair<Integer, Double> searchPair = new Pair<Integer, Double>(newLineIndex, 1.0);
            int foundIndex = Collections.binarySearch(indexes,searchPair , COMPARATOR);
            if(foundIndex < 0){
                int shouldInsert = -foundIndex - 1;
                indexes.add(shouldInsert, searchPair);
            }
            lastFound = newLineIndex + newLine.length();

        }
        return tokResult;


    }
    ////////////////////////////////////////////

    static class PairValue1Comparator<E> implements Comparator<Pair<Integer, E>> {

        public int compare(Pair<Integer, E> o1, Pair<Integer, E> o2) {
            return o1.getValue1() - o2.getValue1();
        }
    }
}
