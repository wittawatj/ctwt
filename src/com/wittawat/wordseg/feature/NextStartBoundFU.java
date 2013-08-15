package com.wittawat.wordseg.feature;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.dict.Dictionary;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.tokenizer.CharacterTokenizerFactory;
import com.wittawat.wordseg.utils.Utils;
import java.util.Arrays;
import java.util.Collection;

/**
 * The distance to the next start boundary of words in the dict.
 * @author Wittawat Jitkrittum
 */
public class NextStartBoundFU extends AbstractNumericalFeatureUnit {

    private Dictionary<String> dict;
    private int[] startIndexes;

    public NextStartBoundFU(String attributeName, Dictionary<String> dict) {
        super(attributeName);
        this.dict = dict;
    }

    @Override
    public void setCharSource(String content) {
        super.setCharSource(content);
        ExactDictionaryChunker chunker = new ExactDictionaryChunker(dict,
                CharacterTokenizerFactory.INSTANCE, false, false);

        Chunking chunking = chunker.chunk(content);
        Collection<Chunk> chunkSet = chunking.chunkSet();
        startIndexes = new int[chunkSet.size()];

        int i = 0;
        for (Chunk chunk : chunking.chunkSet()) {
            Integer startCharIndex = chunk.start();
            startIndexes[i] = startCharIndex;
            ++i;
        }
        assert Utils.ascSorted(startIndexes);

    }

    public double getAttributeValue(int currentIndex) {
        int result = Arrays.binarySearch(startIndexes, currentIndex);
        if (result >= 0) {
            // Found
            return 0.0;
        } else {
            int shouldBe = -result - 1;
            if (shouldBe == startIndexes.length) {
                return -1.0;
            } else {
                int after = -1;
                if (shouldBe == 0) {
                    after = startIndexes[0];
                } else {
                    assert shouldBe > 0;
                    assert shouldBe < startIndexes.length;
                    after = startIndexes[shouldBe];
                }
                assert after >= 0;
                double diff = after - currentIndex;
                assert diff >= 0;
                return diff;
            }
        }
    }
}
