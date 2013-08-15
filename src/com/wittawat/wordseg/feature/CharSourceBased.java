package com.wittawat.wordseg.feature;

/**
 * Mark up for classes which based on character source.
 * 
 * @author Wittawat Jitkrittum
 */
public interface CharSourceBased {

    /**@return the string character source from which this Feature
    unit will generate feature.*/
    String getCharSource();

    void setCharSource(String content);
}
