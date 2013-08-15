//package com.wittawat.wordseg.feature;
//
//import com.aliasi.lm.CharSeqCounter;
//
///**
// * The counter part <code>FUGroup</code>
// * for <code>LeftCondProbFU</code>.
// * @author Wittawat Jitkrittum
// */
//public class LeftCondProbFUGroup extends AbstractFUGroup {
//
//    private final CharSeqCounter counter;
//    private final char expectNext;
//    /**An array of 2-dim arrays of dependLeft and dependRight indexes.*/
//    private final int[][] dependencies;
//    private FeatureUnit[] featureUnits;
//
//    public LeftCondProbFUGroup(CharSeqCounter counter, char expectNext, int[][] dependencies) {
//        this.counter = counter;
//        this.expectNext = expectNext;
//        this.dependencies = dependencies;
//    }
//
//    public LeftCondProbFUGroup(CharSeqCounter counter, int[]... dependencies) {
//        this(counter, '|', dependencies);
//    }
//
//    public FeatureUnit[] getFeatureUnits() {
//
//        if (featureUnits == null) {
//        }
//    }
//
//    ///////////////////////////////////////////
//    class CachedLeftCondProbFU extends AbstractNumericalFeatureUnit {
//
//        private final int dependFrom;
//        private final int dependTo;
//
//        public double getAttributeValue(int currentIndex) {
//
//            LeftCondProbFU.getLCondProb(currentIndex, dependFrom, dependTo, charSource, expectNext, counter);
//        }
//    }
//}
