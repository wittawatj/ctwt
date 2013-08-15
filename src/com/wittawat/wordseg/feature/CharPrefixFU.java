package com.wittawat.wordseg.feature;

import java.io.File;
import java.util.Map;

import com.wittawat.wordseg.word.WordStatsGen;

/**
 * Generate a feature based on the proportion of the words in a dictionary that
 * begin with the specified sequence of characters.
 * 
 * @author nook
 * */
public class CharPrefixFU extends AbstractNumericalFeatureUnit {

    /**
     * The position to using as the first character in the prefix relative to
     * the current position. Default = 0.
     */
    private int relativeFirstPrefixChar;
    private int prefixLength;
    private static final File prefixProportionFile = new File(
            "data/BEST_prefix_map.txt");
    private Map<String, Float> prefixProportionMap;

    public CharPrefixFU(String attributeName, int prefixLength, int relativeFirstPrefixChar, Map<String, Float> prefixProportionMap) {
        super(attributeName);
        this.relativeFirstPrefixChar = relativeFirstPrefixChar;
        this.prefixLength = prefixLength;
        this.prefixProportionMap = prefixProportionMap;

    }

    public CharPrefixFU(String attributeName, int prefixLength, int relativeFirstPrefixChar) {
        this(attributeName, prefixLength, relativeFirstPrefixChar, WordStatsGen.loadWordFloatMap(prefixProportionFile));
    }

    public CharPrefixFU(String attributeName, int prefixLength) {

        this(attributeName, prefixLength, 1);
    }

    public CharPrefixFU(String attributeName, int prefixLength, Map<String, Float> prefixProportionMap) {
        this(attributeName, prefixLength, 1, prefixProportionMap);

    }

    @Override
    public double getAttributeValue(int currentIndex) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < prefixLength; ++i) {
            int effectiveIndex = i + currentIndex + relativeFirstPrefixChar;
            if (effectiveIndex >= 0 && effectiveIndex < charSource.length()) {
                char charI = charSource.charAt(effectiveIndex);
                buf.append(charI);
            } else {
                return 0.0;
            }

        }
        String prefix = buf.toString();
        if (prefix.equals("")) {
            return 0;
        }
        assert prefixProportionMap != null;
        assert prefixProportionMap.size() > 0;
        Float proportion = prefixProportionMap.get(prefix);
        return proportion == null ? 0 : proportion;
    }
}
