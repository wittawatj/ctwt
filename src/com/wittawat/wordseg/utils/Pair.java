
package com.wittawat.wordseg.utils;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class Pair<E,T> {
    private E value1;
    private T value2;

    public Pair(E value1, T value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public E getValue1() {
        return value1;
    }

    public T getValue2() {
        return value2;
    }

    public void setValue1(E value1) {
        this.value1 = value1;
    }

    public void setValue2(T value2) {
        this.value2 = value2;
    }
    

}
