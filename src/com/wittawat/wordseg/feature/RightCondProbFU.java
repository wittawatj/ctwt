package com.wittawat.wordseg.feature;

import com.aliasi.lm.CharSeqCounter;

/**
 * Conditional probability of a specified character given
 * the characters to the left identified by relative indexes
 * where 0 is the
 * current character, -1 for ci-1, 3 for ci+3,.. so on.
 *
 * @author Wittawat Jitkrittum
 */
public class RightCondProbFU extends AbstractNumericalFeatureUnit {

    private CharSeqCounter counter;
    private final int dependFrom;
    private final int dependTo;
    private final char expectBefore;
    private final int dependingChars;

    /**
     * Conditional probability of a specified character given
     * the right k characters. The first character of those
     * k characters is the next character from the current index
     * i.e. ci+1.
     */
    public RightCondProbFU(String attributeName, CharSeqCounter counter,
            int dependingChars, char expectBefore) {
        super(attributeName);
        this.counter = counter;
        this.dependFrom = 1;
        this.dependTo = dependingChars;
        this.expectBefore = expectBefore;
        this.dependingChars = dependingChars;
    }

    public RightCondProbFU(String attributeName, CharSeqCounter counter,
            int dependFrom, int dependTo, char expectBefore) {
        super(attributeName);
        this.counter = counter;
        this.dependFrom = dependFrom;
        this.dependTo = dependTo;
        this.expectBefore = expectBefore;
        this.dependingChars = dependTo - dependFrom + 1;
    }

    public RightCondProbFU(CharSeqCounter counter,
            int dependFrom, int dependTo, char expectBefore) {
        this("rProb_i" + (dependFrom != 0 ? dependFrom : "") + "_i" + (dependTo != 0 ? dependTo : ""),
                counter,
                dependFrom,
                dependTo,
                expectBefore);
    }

    public RightCondProbFU(CharSeqCounter counter,
            int dependFrom, int dependTo) {
        this(counter, dependFrom, dependTo, '|');
    }

    public double getAttributeValue(int currentIndex) {
        return getRCondProb(currentIndex, dependFrom, dependTo, charSource, expectBefore, counter);
    }

    public static double getRCondProb(int currentIndex, int dependFrom,
            int dependTo, String charSource, char expectBefore, CharSeqCounter counter) {
        final int beginCond = currentIndex + dependFrom;
        final int endCond = currentIndex + dependTo;

        if (beginCond < 0 || endCond >= charSource.length()) {
            return 0.0;
        }
        final int dependingChars = dependTo - dependFrom + 1;
        char[] all = new char[dependingChars + 1];
        char[] cond = new char[dependingChars];

        int j = 0;
        for (int i = beginCond; i <= endCond; ++i) {
            char charI = charSource.charAt(i);
            cond[j] = charI;
            all[j + 1] = charI;
            ++j;
        }
        all[0] = expectBefore;

        // FInd the prob
        long countAll = counter.count(all, 0, all.length);
//        System.out.printf("\"%s\" : %d\n", new String(all), countAll);
        if (countAll > 0) {
            long countCond = counter.count(cond, 0, cond.length);
            assert countCond > 0;
            double prob = (double) countAll / (double) countCond;
            if (countAll == countCond + 1) {
                prob = 1.0;
            }
            assert prob <= 1;

//            System.out.printf("RightProb| \"%s\" : %f (countAll: %d, countCond: %d)\n", new String(all), prob, countAll, countCond);

            assert prob >= 0;
            return prob;
        } else {
            return 0.0;
        }

    }
}
