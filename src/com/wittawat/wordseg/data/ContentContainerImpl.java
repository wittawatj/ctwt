package com.wittawat.wordseg.data;

import com.wittawat.wordseg.utils.Utils;
import java.util.Set;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class ContentContainerImpl implements ContentContainer {

    private String content;
    private int[] indexes;

    public ContentContainerImpl(String content, int[] indexes) {
        this.content = content;
        this.indexes = indexes;
    }

    public ContentContainerImpl(String content, Set<Integer> indexes) {
        this(content, Utils.setToArray(indexes));
    }

    public String getContent() {
        return content;
    }

    public int[] getIndexes() {
        return indexes;
    }
}
