package com.wittawat.wordseg.feature;

import com.wittawat.wordseg.*;

/**
 * A <code>FeatureUnit</code> which generate a feature based on the number of
 * chracters from the current one until the previous space character. Treat
 * start of character stream as a space also. Not count the current char if it
 * is a space also.
 * 
 * @author nook
 */
public class PreviousSpaceFU extends AbstractNumericalFeatureUnit {

    public PreviousSpaceFU(String attributeName) {
        super(attributeName);
    }

    public double getAttributeValue(int currentIndex) {

        int distance = 1;
        int i = currentIndex - 1;
        int length = charSource.length();
        for (; i >= 0 && i < length && !Character.isWhitespace(charSource.charAt(i)); --i, ++distance) {
        }
        return distance;
    }
}
