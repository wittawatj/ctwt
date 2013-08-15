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
 * Feature is the distance (number of characters) from the
 * current index, to the first index of the previous word
 * in the list.
 * 
 * @author Wittawat Jitkrittum
 */
public class PreviousWordPosFU extends AbstractNumericalFeatureUnit {

    private Dictionary<String> dict;
    private int[] startIndexes;

    public PreviousWordPosFU(String attributeName, Dictionary<String> dict) {
        super(attributeName);
        this.dict = dict;
    }

    @Override
    public void setCharSource(String content) {
        super.setCharSource(content);
        ExactDictionaryChunker chunker = new ExactDictionaryChunker(dict, CharacterTokenizerFactory.INSTANCE, false, false);
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
            if (shouldBe == 0) {
                return -1.0;
            } else {
                int before = -1;
                if (shouldBe == startIndexes.length) {
                    before = startIndexes[startIndexes.length - 1];
                } else {
                    assert shouldBe > 0;
                    assert shouldBe < startIndexes.length;
                    before = startIndexes[shouldBe - 1];
                }
                assert before >= 0;
                double diff = currentIndex - before;
                assert diff >= 0;
                return diff;
            }
        }
    }
}
