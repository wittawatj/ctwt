package com.wittawat.wordseg.feature;

/**
 * A <code>FeatureUnit</code> which generate a feature based on the number of
 * chracters from the current one until the next space character. Treat end of
 * character stream as a space also. Not count the current char if it is a space
 * also.
 * 
 * @author nook
 */
public class NextSpaceFU extends AbstractNumericalFeatureUnit {

    public NextSpaceFU(String attributeName) {
        super(attributeName);
    }

    public double getAttributeValue(int currentIndex) {

        int distance = 1;
        int i = currentIndex + 1;
        int length = charSource.length();
        for (; i >= 0 && i < length && !Character.isWhitespace(charSource.charAt(i)); ++i, ++distance) {
        }
        return distance;
    }
}
