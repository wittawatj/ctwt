package com.wittawat.wordseg.feature;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 * Similar to <code>CharTypeFU</code> except that
 * each character in final consonant type has its
 * own type. The reason for this is that there are
 * too many final consonant characters .
 *
 * @author Wittawat Jitkrittum
 */
public class CharTypeFConExFU extends CharTypeFU {

    public CharTypeFConExFU(String attributeName) {
        super(attributeName);
    }

    public CharTypeFConExFU(String attributeName, int relativePosition) {
        super(attributeName, relativePosition);
    }

    @Override
    public Attribute getAttribute() {
        if (attribute == null) {
            POSSIBLE_VALUES = new FastVector(T_ARRAY.length);
            for (String type : T_ARRAY) {
                if (!type.equals(T_FINAL_CONSONANT)) {
                    // Exclude final consonant type
                    POSSIBLE_VALUES.addElement(type);
                }
            }
            // Add each final consonant as one own type
            final int fConCount = FINAL_CONSONANTS.length();
            for (int i = 0; i < fConCount; ++i) {
                char ch = FINAL_CONSONANTS.charAt(i);
                String type = String.valueOf(ch);
                POSSIBLE_VALUES.addElement(type);
            }
            attribute = new Attribute(getAttributeName(), POSSIBLE_VALUES);
        }
        return attribute;
    }

    @Override
    public double getAttributeValue(int currentIndex) {
        int effectiveIndex = currentIndex + relativePosition;
        String type = getTypeOfCharAt(currentIndex, relativePosition, charSource);

        if (type.equals(T_FINAL_CONSONANT)) {
            char ch = charSource.charAt(effectiveIndex);
            assert String.valueOf(ch).matches("["+FINAL_CONSONANTS+"]");
            type  = String.valueOf(ch);
            assert POSSIBLE_VALUES.contains(type);
        }
        return POSSIBLE_VALUES.indexOf(type);

    }
}
