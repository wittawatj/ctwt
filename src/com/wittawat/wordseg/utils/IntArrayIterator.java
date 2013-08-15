package com.wittawat.wordseg.utils;

import java.util.Iterator;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class IntArrayIterator implements Iterator<Integer> {

    private int[] nums;
    private int curIndex = 0;

    public IntArrayIterator(int[] nums) {
        this.nums = nums;
    }

    public boolean hasNext() {
        return curIndex < nums.length;
    }

    public Integer next() {
        int toReturn = nums[curIndex];
        curIndex++;
        return toReturn;
    }

    public void remove() {
    }
}
