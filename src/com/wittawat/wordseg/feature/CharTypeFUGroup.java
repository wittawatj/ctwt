//package com.wittawat.wordseg.feature;
//
//import weka.core.Attribute;
//import weka.core.FastVector;
//
///**
// *
// * @author Wittawat Jitkrittum
// */
//public class CharTypeFUGroup extends AbstractFUGroup {
//
//    /**Relative indexes to consider. 0 for ci.
//    -1 for ci-1, 3 for ci+3.*/
//    private int[] relativeIndexes;
////    private Attribute[] attributes;
//    private FastVector possibleValues;
//
//    public CharTypeFUGroup(int... relativeIndexes) {
//        if (relativeIndexes == null || relativeIndexes.length == 0) {
//            throw new RuntimeException("relativeIndexes must not be blank.");
//        }
//        this.relativeIndexes = relativeIndexes;
//    }
//
//    public FeatureUnit[] getFeatureUnits() {
//    }
//
//    public FastVector getPossibleValues() {
//        if (possibleValues == null) {
//            possibleValues = CharTypeFU.createPossibleValues();
//        }
//        return possibleValues;
//    }
//
//    ///////////////////////////////////////////////////////
//    class CharTypeFUFeatureUnit extends AbstractFeatureUnit {
//
//        private final int relativeIndex;
//        private Attribute attribute;
//
//        public CharTypeFUFeatureUnit(int relativeIndex) {
//            super("type_c_i" + relativeIndex);
//            this.relativeIndex = relativeIndex;
//        }
//
//        public Attribute getAttribute() {
//            if(attribute == null){
//                attribute = new Attribute(getAttributeName(), getPossibleValues());
//            }
//            return attribute;
//        }
//
//        @Override
//        public void setCharSource(String content) {
//            CharTypeFUGroup.this.setCharSource(content);
//        }
//
//        public double getAttributeValue(int currentIndex) {
//
//        }
//
//        public Object getRawAttributeValue(int currentIndex) {
//
//        }
//    }
////    public double[] getAttributeValues(int currentIndex) {
////        double[] values = new double[relativeIndexes.length];
////        for (int i = 0; i < values.length; ++i) {
////            final int relIndex = relativeIndexes[i];
////            String type = CharTypeFU.getTypeOfCharAt(currentIndex, relIndex, this.charSource);
////            int vIndex = possibleValues.indexOf(type);
////            assert vIndex >= 0;
////            values[i] = vIndex;
////        }
////        return values;
////
////    }
////
////    public Attribute[] getAttributes() {
////        if (attributes == null) {
////            attributes = new Attribute[relativeIndexes.length];
////
////            possibleValues = CharTypeFU.createPossibleValues();
////            for (int i = 0; i < attributes.length; ++i) {
////                final int relIndex = relativeIndexes[i];
////                String attrName = "type_c_i" + relIndex;
////                attributes[i] = new Attribute(attrName, possibleValues);
////            }
////        }
////        return attributes;
////    }
//}
