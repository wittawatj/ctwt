package com.wittawat.wordseg.feature;

import com.aliasi.lm.CharSeqCounter;
import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.utils.Utils;
import java.util.Arrays;

/**
 * A <code>FeatureUnit</code> using left entropy described in
 * "Pattern-Based Features vs. Statistical-Based Features in Decision Trees for Word Segmentation"
 * as the feature value.
 *
 * LE(ci) = -sum_{all possible ci+dependFrom-1} { P(ci+dependFrom-1 | ci+dependFrom ... ci+dependTo)*log_2(P(ci+dependFrom-1 | ci+dependFrom ... ci+dependTo))}
 *
 * where only characters in Thai unicode range are taken as all possible ci+dependFrom-1 (by default)
 *
 * Note carefully that this class uses the opposite meaning compared
 * to <code>LeftCondProbFU</code>.
 * 
 * @author Wittawat Jitkrittum
 * @see <code>RightEntropyFU</code>
 */
public class LeftEntropyFU extends AbstractNumericalFeatureUnit {

    public static final double CHAR_NOT_EXISTS_DEFAULT = -1;
    private CharSeqCounter counter;
    private final int dependFrom;
    private final int dependTo;
    private final char[] consideringChars;

    public LeftEntropyFU(String attributeName, CharSeqCounter counter, int dependFrom, int dependTo, char[] consideringChars) {
        super(attributeName);
        this.counter = counter;
        this.dependFrom = dependFrom;
        this.dependTo = dependTo;

        this.consideringChars = consideringChars;
        Arrays.sort(consideringChars);
        assert MyStringUtils.ascSorted(consideringChars);
    }

    public LeftEntropyFU(String attributeName, CharSeqCounter counter, int dependFrom, int dependTo) {
        this(attributeName, counter, dependFrom, dependTo, MyStringUtils.getThaiCharsInUnicode());
    }

    public LeftEntropyFU(CharSeqCounter counter, int dependFrom, int dependTo, char[] consideringChars) {
        this("le_i" + (dependFrom != 0 ? dependFrom : "") + "_i" + (dependTo != 0 ? dependTo : ""),
                counter, dependFrom, dependTo, consideringChars);
    }

    public LeftEntropyFU(CharSeqCounter counter, int dependFrom, int dependTo) {
        this("le_i" + (dependFrom != 0 ? dependFrom : "") + "_i" + (dependTo != 0 ? dependTo : ""),
                counter, dependFrom, dependTo, MyStringUtils.getThaiCharsInUnicode());
    }

    public double getAttributeValue(int currentIndex) {

        final int begin = currentIndex + dependFrom;
        final int end = currentIndex + dependTo;
        if (begin < 0 || end >= charSource.length()) {
            return CHAR_NOT_EXISTS_DEFAULT;
        }

        
        double sum = 0;
        for (int i = 0; i < consideringChars.length; ++i) {
            char ch = consideringChars[i];
            double prob = RightCondProbFU.getRCondProb(currentIndex, dependFrom, dependTo, charSource, ch, counter);
            if (prob > 0) {
                double logProb = Utils.log2(prob);
                assert logProb <= 0;
                double product = prob * logProb;
                sum += product;
            }
        }
        double leftEntropy = -sum;
        assert leftEntropy >= 0;
        return leftEntropy;

    }
}
