package com.wittawat.wordseg.feature;

import java.io.File;
import java.util.Map;

import com.wittawat.wordseg.word.WordStatsGen;

/**
 * Generate a feature based on the proportion of the words in a dictionary that
 * ends with a sequence of characters.
 * 
 * @author nook
 * */
public class CharSuffixFU extends AbstractNumericalFeatureUnit {

    /**
     * The position using as the last character in the suffix relative to the
     * current position. Default = -1.
     */
    private int relativeLastSuffixChar = -1;
    private int suffixLength;
    private static final File suffixProportionFile = new File(
            "data/BEST_suffix_map.txt");
    private Map<String, Float> suffixProportionMap;

    public CharSuffixFU(String attributeName, int suffixLength, int relativeLastSuffixChar, Map<String, Float> suffixProportionMap) {
        super(attributeName);
        this.relativeLastSuffixChar = relativeLastSuffixChar;
        this.suffixLength = suffixLength;
        this.suffixProportionMap = suffixProportionMap;
    }

    public CharSuffixFU(String attributeName, int suffixLength, int relativeLastSuffixChar) {
        this(attributeName, suffixLength, relativeLastSuffixChar, WordStatsGen.loadWordFloatMap(suffixProportionFile));
    }

    public CharSuffixFU(String attributeName, int suffixLength) {
        this(attributeName, suffixLength, 0);
    }

    public CharSuffixFU(String attributeName, int suffixLength, Map<String, Float> suffixProportionMap) {
        this(attributeName, suffixLength, 0, suffixProportionMap);
    }

    @Override
    public double getAttributeValue(int currentIndex) {
        StringBuilder buf = new StringBuilder();


        int length = charSource.length();
        for (int i = -suffixLength + 1; i <= 0; ++i) {
            int effectiveIndex = i + currentIndex + relativeLastSuffixChar;
            if (effectiveIndex >= 0 && effectiveIndex < length) {
                char charI = charSource.charAt(effectiveIndex);

                buf.append(charI);
            } else {
                return 0.0;
            }

        }
        String suffix = buf.toString();
        if (suffix.equals("")) {
            return 0;
        }
        assert suffixProportionMap != null;
        assert suffixProportionMap.size() > 0;
        Float proportion = suffixProportionMap.get(suffix);
        return proportion == null ? 0 : proportion;

    }
}
