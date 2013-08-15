package com.wittawat.wordseg.data;

import java.util.Iterator;

/**A word iterator. The term word does not
 * need to correspond to the term word in the
 * linguistic sense.
 * @author nook*/
public interface WordIterator extends Iterator<String> {

    /**Close the underlying string source.*/
    void close();
}
