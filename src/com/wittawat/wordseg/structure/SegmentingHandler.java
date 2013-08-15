
package com.wittawat.wordseg.structure;

import com.wittawat.wordseg.BasicWordTokenizer;
import com.wittawat.wordseg.data.TextSource;

/**
 * A result of performing word segmentation
 * using a <code>WordTokenizer</code> on a
 * <code>TextSource</code>.
 * What to do with the result depends on the
 * concrete implementation.
 * 
 * @author Wittawat Jitkrittum
 */
public interface SegmentingHandler {

    void segmented(BasicWordTokenizer tokenizer, TextSource source, String result);

}
