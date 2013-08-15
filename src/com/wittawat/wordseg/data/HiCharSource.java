package com.wittawat.wordseg.data;


/**
 * A higher level <code>CharSource</code> specifically designed for word
 * segmentation model training usage. In <code>HiCharSource</code> word delimiters
 * are not taken into account when calculating/accessing character indices.
 * That is, "ab|cd" 's index [2] is 'c' not '|'.
 * 
 * @author nook
 */
public interface HiCharSource  {

    /** @return true if the chatacter at i follows the delimiter. */
    boolean isWordStart(int i);

    /** @return true if the chatacter at i precedes the delimiter. */
    boolean isWordEnd(int i);


    /**@return the string content without any tag or delimiter*/
    String getPlainText();

    /**@return a plain text just list getPlainText() but with delimiter
     inserted at each delimited position.*/
    String getDelimitedPlainText();

    int length();

    Character charAt(int index);
}
