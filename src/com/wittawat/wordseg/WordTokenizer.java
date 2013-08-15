package com.wittawat.wordseg;

import java.util.Iterator;
import java.util.List;

/**
 * General interface for a word tokenizer.
 *
 * @author Wittawat Jitkrittum
 */
public interface WordTokenizer extends BasicWordTokenizer {

    Iterator<String> iterateTokens();

    List<String> listTokens();
}
