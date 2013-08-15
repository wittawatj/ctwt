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
 * current index, to the last index of the next word
 * in the list.
 *
 * @author Wittawat Jitkrittum
 */
public class NextWordPosFU extends AbstractNumericalFeatureUnit {

    private Dictionary<String> dict;
    private int[] endIndexes;

    public NextWordPosFU(String attributeName, Dictionary<String> dict) {
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
        endIndexes = new int[chunkSet.size()];

        int i = 0;
        for (Chunk chunk : chunkSet) {
            Integer endCharIndex = chunk.end() - 1;
            endIndexes[i] = endCharIndex;
            ++i;
        }
        assert Utils.ascSorted(endIndexes);
    }

    public double getAttributeValue(int currentIndex) {
        int result = Arrays.binarySearch(endIndexes, currentIndex);
        if (result >= 0) {
            // Found
            return 0.0;
        } else {
            int shouldBe = -result - 1;
            if (shouldBe == endIndexes.length) {
                return -1.0;
            } else {
                int after = -1;
                if (shouldBe == 0) {
                    after = endIndexes[0];
                } else {
                    assert shouldBe > 0;
                    assert shouldBe < endIndexes.length;
                    after = endIndexes[shouldBe];
                }
                assert after >= 0;
                double diff = after - currentIndex;
                assert diff >= 0;
                return diff;
            }
        }
    }
}
