//
//package com.wittawat.wordseg.feature;
//
//import com.aliasi.dict.Dictionary;
//
///**
// * Attribute which uses as the value:
// *
// * 1. the distance from the current index to the previous
// * end of word boundary. 0 if current index itself is
// * the end of word in the dict.
// *
// * 2. the distance from the current index to the next
// * start of the word boundary. 1 if the current index + 1
// * is the start of the word in the dict.
// *
// *
// * @author Wittawat Jitkrittum
// */
//public class DictBoundaryFUGroup extends AbstractFUGroup{
//
//    private Dictionary<String> dict;
//
//    public FeatureUnit[] getFeatureUnits() {
//        return new FeatureUnit[]{getPrevBoundaryFU(), getNextBoundaryFU()};
//    }
//
//
//}
