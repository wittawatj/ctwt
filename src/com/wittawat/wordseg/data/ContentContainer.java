package com.wittawat.wordseg.data;

/**
 * A string content container and
 * the indexes of content characters.
 * 
 * @author Wittawat Jitkrittum
 */
public interface ContentContainer {

    String getContent();

    /**@return the marked locations of
    the content. Can be null to denote that
    all positions are used.*/
    int[] getIndexes();
}
