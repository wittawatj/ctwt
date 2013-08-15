package com.wittawat.wordseg.word;

import com.wittawat.wordseg.utils.MyStringUtils;
import java.io.Serializable;
import java.util.Arrays;

/**
 * General purpose word sequence.
 * @author Wittawat Jitkrittum
 */
public class WordSequence implements Sequence<String>, Serializable {

    private String[] sequence;

    public WordSequence(String... sequence) {
        this.sequence = sequence;

    }

    public String[] getSequence() {
        return sequence.clone();
    }

    public int length() {
        return sequence.length;
    }

    public String get(int i) {
        return sequence[i];
    }

    public Sequence<String> add(Sequence<String> items) {
        int itemsLength = items.length();
        String[] newStrings = new String[sequence.length + itemsLength];

        for(int i=0;i<sequence.length;++i){
            newStrings[i] = sequence[i];
        }

        int j=0;
        for (int i = sequence.length; i < newStrings.length; ++i) {
            newStrings[i] = items.get(j);
            ++j;
        }
        return new WordSequence(newStrings);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WordSequence other = (WordSequence) obj;
        if (!Arrays.deepEquals(this.sequence, other.sequence)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.deepHashCode(this.sequence);
        return hash;
    }

    @Override
    public String toString() {
        return MyStringUtils.toString(sequence);
    }
}
