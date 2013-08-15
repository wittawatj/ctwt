package com.wittawat.wordseg.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * General purpose utility methods.
 *
 * @author Wittawat Jitkrittum
 */
public abstract  class Utils {

    public static final double NATURAL_LOG_OF_2 = Math.log(2);
    public static double log2(double v){
        return Math.log(v)/NATURAL_LOG_OF_2;
    }
    public static <E> int[] toIntArray(List<Pair<Integer, E>> l ){
        final int length = l.size();
        int[] arr = new int[length];
        for(int i=0;i<length;++i){
            arr[i] = l.get(i).getValue1();
        }
        return arr;
    }
    public static <E> boolean strictAscSorted(List<Pair<Integer, E>> l ){
        int last = l.size() -1;
        for(int i=0;i<last  ; ++i){
            if(l.get(i).getValue1() > l.get(i+1).getValue1()){
                return false;
            }
        }
        return true;
    }
    public static int max(double... a) {
        if (a == null || a.length == 0) {
            return -1;
        }
        if (a.length == 1) {
            return 0;
        }
        double max = a[0];
        int maxIndex = 0;
        for (int i = 1; i < a.length; ++i) {
            if (a[i] > max) {
                maxIndex = i;
                max = a[i];
            }
        }
        return maxIndex;

    }

    public static int[] toArray(List<Integer> list, int add) {
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; ++i) {
            arr[i] = list.get(i) + add;
        }
        return arr;

    }
    /**Sorted in ascending order.*/
    public static int[] setToArray(Set<Integer> set) {
        List<Integer> l = new ArrayList<Integer>(set);
        Collections.sort(l);
        int[] intArray = new int[l.size()];
        for (int i = 0; i < intArray.length; ++i) {
            intArray[i] = l.get(i);
        }

        return intArray;
    }

    public static boolean ascSorted(int[] arr) {
        int end = arr.length - 1;
        for (int i = 0; i < end; ++i) {
            if (arr[i] > arr[i + 1]) {
                return false;
            }

        }
        return true;
    }
    ////////////////////////////////////////

    public static void main(String[] args) throws Exception {
    }
}
