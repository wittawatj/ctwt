package com.wittawat.wordseg;

/**
 *
 * @author Wittawat Jitkrittum
 */
public interface BasicWordTokenizer {

    /**@return a tokenized content in the format
    depending on the implementation.*/
    String tokenize();

    /**@return the source content to be tokenized*/
    String getSourceContent();

    void close();
}
