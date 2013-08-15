package com.wittawat.wordseg.feature;

import com.aliasi.lm.CharSeqCounter;
import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.utils.Utils;
import java.util.Arrays;

/**
 * A <code>FeatureUnit</code> using right entropy described in
 * "Pattern-Based Features vs. Statistical-Based Features in Decision Trees for Word Segmentation"
 * as the feature value.
 *
 * RE(ci) = -sum_{all possible ci+dependTo+1} { P(ci+dependTo+1 | ci+dependFrom ... ci+dependTo)*log_2(P(ci+dependTo+1 | ci+dependFrom ... ci+dependTo))}
 *
 * where only characters in Thai unicode range are taken as all possible ci+dependTo+1 (by default)
 *
 * Note carefully that this class uses the opposite meaning compared
 * to <code>RightCondProbFU</code>.
 *
 * @author Wittawat Jitkrittum
 * @see <code>LeftEntropyFU</code>
 */
public class RightEntropyFU extends AbstractNumericalFeatureUnit {

    public static final double CHAR_NOT_EXISTS_DEFAULT = -1;
    private CharSeqCounter counter;
    private final int dependFrom;
    private final int dependTo;

    private final char[] consideringChars;

    /**@param consideringChars must be ascendingly sorted*/
    public RightEntropyFU(String attributeName, CharSeqCounter counter, int dependFrom, int dependTo, char[] consideringChars) {
        super(attributeName);
        this.counter = counter;
        this.dependFrom = dependFrom;
        this.dependTo = dependTo;
        this.consideringChars = consideringChars;
        Arrays.sort(consideringChars);
        assert MyStringUtils.ascSorted(consideringChars);
    }
    public RightEntropyFU(String attributeName, CharSeqCounter counter, int dependFrom, int dependTo) {
        this(attributeName, counter, dependFrom, dependTo, MyStringUtils.getThaiCharsInUnicode());
    }

    /**@param consideringChars must be ascendingly sorted*/
    public RightEntropyFU(CharSeqCounter counter, int dependFrom, int dependTo, char[] consideringChars) {
         this("re_i" + (dependFrom != 0 ? dependFrom : "") + "_i" + (dependTo != 0 ? dependTo : ""),
                counter, dependFrom, dependTo, consideringChars);
    }
    public RightEntropyFU(CharSeqCounter counter, int dependFrom, int dependTo) {
         this("re_i" + (dependFrom != 0 ? dependFrom : "") + "_i" + (dependTo != 0 ? dependTo : ""),
                counter, dependFrom, dependTo, MyStringUtils.getThaiCharsInUnicode());
    }

    public double getAttributeValue(int currentIndex) {

        final int begin = currentIndex + dependFrom;
        final int end = currentIndex + dependTo;
        if (begin < 0 || end >= charSource.length()) {
            return CHAR_NOT_EXISTS_DEFAULT;
        }
        final int dependingChars = dependTo - dependFrom + 1;
        assert dependingChars > 0;
        final char[] cond = new char[dependingChars];

        int j = 0;
        for (int i = begin; i <= end; ++i) {
            char charI = charSource.charAt(i);
            cond[j] = charI;
            ++j;
        }

        // Find all possible characters following 'cond'
        char[] follows = counter.charactersFollowing(cond, 0, cond.length);
        double sum = 0;
        for (int i = 0; i < follows.length; ++i) {
            char follow = follows[i];
            int searchResult = Arrays.binarySearch(consideringChars, follow);
            if (searchResult >= 0) {
                double prob = LeftCondProbFU.getLCondProb(currentIndex, dependFrom, dependTo, charSource, follow, counter);
                if (prob > 0) {
                    double logProb = Utils.log2(prob);
                    assert logProb <= 0;
                    double product = prob * logProb;
                    sum += product;
                }
            }
        }
        double rightEntropy = -sum;
        assert rightEntropy >= 0;
        return rightEntropy;
    }
}
