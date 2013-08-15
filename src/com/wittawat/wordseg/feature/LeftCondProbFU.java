package com.wittawat.wordseg.feature;

import com.aliasi.lm.CharSeqCounter;

/**
 * Conditional probability of a specified character given
 * the characters to the right identified by relative indexes
 * where 0 is the
 * current character, -1 for ci-1, 3 for ci+3,.. so on.
 * 
 * @author Wittawat Jitkrittum
 */
public class LeftCondProbFU extends AbstractNumericalFeatureUnit {

    private CharSeqCounter counter;
    private final int dependFrom;
    private final int dependTo;
    private final char expectNext;

    /**
     * Conditional probability of a specified character given
     * the left k characters. The character at the current index
     * is treated as the last character in those k characters.
     */
    public LeftCondProbFU(String attributeName, CharSeqCounter counter,
            int dependingChars, char nextExpect) {

        super(attributeName);
        this.counter = counter;
        assert dependingChars >= 1;
        this.dependFrom = 1 - dependingChars;
        this.dependTo = 0;
        this.expectNext = nextExpect;


    }

    public LeftCondProbFU(String attributeName, CharSeqCounter counter,
            int dependFrom, int dependTo, char expectNext) {
        super(attributeName);
        assert dependFrom < dependTo;
        assert dependTo - dependFrom + 1 <= 20;
        this.counter = counter;
        this.dependFrom = dependFrom;
        this.dependTo = dependTo;
        this.expectNext = expectNext;

    }

    public LeftCondProbFU(CharSeqCounter counter,
            int dependFrom, int dependTo, char expectNext) {
        this("lProb_i" + (dependFrom != 0 ? dependFrom : "") + "_i" + (dependTo != 0 ? dependTo : ""),
                counter,
                dependFrom,
                dependTo,
                expectNext);
    }

    public LeftCondProbFU(CharSeqCounter counter,
            int dependFrom, int dependTo) {
        this(counter, dependFrom, dependTo, '|');
    }

    public double getAttributeValue(int currentIndex) {
        return getLCondProb(currentIndex, dependFrom, dependTo, charSource, expectNext, counter);
    }

    public static double getLCondProb(int currentIndex, int dependFrom,
            int dependTo, String charSource, char expectNext, CharSeqCounter counter) {
        final int begin = currentIndex + dependFrom;
        final int end = currentIndex + dependTo;
        if (begin < 0 || end >= charSource.length()) {
            return 0.0;
        }
        final int dependingChars = dependTo - dependFrom + 1;
        final char[] all = new char[dependingChars + 1];
        final char[] cond = new char[dependingChars];

        int j = 0;

        for (int i = begin; i <= end; ++i) {
            char charI = charSource.charAt(i);
            all[j] = charI;
            cond[j] = charI;
            ++j;
        }
        all[all.length - 1] = expectNext;

        // FInd the prob
        long countAll = counter.count(all, 0, all.length);
//        System.out.printf("\"%s\" : %d\n", new String(all), countAll);
        if (countAll > 0) {
            long countCond = counter.extensionCount(cond, 0, cond.length);
            assert countCond > 0;

            double prob = (double) countAll / (double) countCond;
//            System.out.printf("LeftProb| \"%s\" : %f (countAll: %d, countCond: %d)\n", new String(all), prob, countAll, countCond);
            assert prob >= 0;
            assert prob <= 1;
            return prob;
        } else {
            return 0.0;
        }

    }
}
