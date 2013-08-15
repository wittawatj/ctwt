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
 * The distance to the last end boundary of words in the dict
 * @author Wittawat Jitkrittum
 */
public class PrevEndBoundFU extends AbstractNumericalFeatureUnit {

    private Dictionary<String> dict;
    private int[] endIndexes;

    public PrevEndBoundFU(String attributeName, Dictionary<String> dict) {
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
            if (shouldBe == 0) {
                return -1.0;
            } else {
                int before = -1;
                if (shouldBe == endIndexes.length) {
                    before = endIndexes[endIndexes.length - 1];
                } else {
                    assert shouldBe > 0;
                    assert shouldBe < endIndexes.length;
                    before = endIndexes[shouldBe - 1];
                }
                assert before >= 0;
                double diff = currentIndex - before;
                assert diff >= 0;
                return diff;
            }
        }
    }//
}
