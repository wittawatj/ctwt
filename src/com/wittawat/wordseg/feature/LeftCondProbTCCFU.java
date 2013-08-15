//package com.wittawat.wordseg.feature;
//
//import com.aliasi.lm.CharSeqCounter;
//import com.wittawat.tcc.TCCTokenizer;
//import java.util.Vector;
//
///**
// * Similar to <code>LeftCondProbFU</code> but this
// * class considers TCCs.
// *
// * @author Wittawat Jitkrittum
// */
//@Deprecated
//public class LeftCondProbTCCFU extends AbstractNumericalFeatureUnit {
//
//    private CharSeqCounter counter;
//    /**In the unit of TCC (not char)*/
//    private final int dependFrom;
//    /**In the unit of TCC (not char)*/
//    private final int dependTo;
//    private final char expectNext;
//
//    private Vector<Integer> tccEndIndexes;
//    public LeftCondProbTCCFU(String attributeName, CharSeqCounter counter,
//            int dependFrom, int dependTo, char expectNext) {
//        super(attributeName);
//        assert dependFrom < dependTo;
//        assert dependTo - dependFrom + 1 <= 20;
//        this.counter = counter;
//        this.dependFrom = dependFrom;
//        this.dependTo = dependTo;
//        this.expectNext = expectNext;
//
//    }
//
//    @Override
//    public void setCharSource(String content) {
//        super.setCharSource(content);
//        if(!content.equals(charSource)){
//            TCCTokenizer tok = new TCCTokenizer(content); //expensive ?
//            tccEndIndexes = tok.getEndIndexesOrNull();
//        }
//    }
//
//     public double getAttributeValue(int currentIndex) {
//        return getLCondProb(currentIndex, dependFrom, dependTo, charSource, expectNext, counter, tccEndIndexes);
//    }
//
//    public static double getLCondProb(int currentIndex, int dependFrom,
//            int dependTo, String charSource, char expectNext,
//            CharSeqCounter counter, Vector<Integer> tccEndIndexes) {
//
//        // Only return a value if currentIndex is the end of a tccEndIndex
//
//        final int begin = currentIndex + dependFrom;
//        final int end = currentIndex + dependTo;
//        if (begin < 0 || end >= charSource.length()) {
//            return 0.0;
//        }
//        final int dependingChars = dependTo - dependFrom + 1;
//        final char[] all = new char[dependingChars + 1];
//        final char[] cond = new char[dependingChars];
//
//        int j = 0;s
//
//        for (int i = begin; i <= end; ++i) {
//            char charI = charSource.charAt(i);
//            all[j] = charI;
//            cond[j] = charI;
//            ++j;
//        }
//        all[all.length - 1] = expectNext;
//
//        // FInd the prob
//        long countAll = counter.count(all, 0, all.length);
////        System.out.printf("\"%s\" : %d\n", new String(all), countAll);
//        if (countAll > 0) {
//            long countCond = counter.extensionCount(cond, 0, cond.length);
//            assert countCond > 0;
//
//            double prob = (double) countAll / (double) countCond;
////            System.out.printf("LeftProb| \"%s\" : %f (countAll: %d, countCond: %d)\n", new String(all), prob, countAll, countCond);
//            assert prob >= 0;
//            assert prob <= 1;
//            return prob;
//        } else {
//            return 0.0;
//        }
//
//    }
//}
