package com.wittawat.wordseg.feature;

import com.aliasi.lm.CharSeqCounter;
import com.wittawat.wordseg.Data;
import java.io.File;
import org.apache.commons.io.FileUtils;

/**
 * Given the current context as ..., Ci-2, Ci-1, Ci, Ci+1, Ci+2, ...
 * this FU calculates the ratio of the frequency of
 * ..., Ci-2, Ci-1, Ci, |, Ci+1, Ci+2, ... to the frequency
 * of ..., Ci-2, Ci-1, Ci, *, Ci+1, Ci+2, ...  where * is
 * something that is not the delimiter.
 *
 * The frequency is obtained from the specified counter.
 * Left and right independece characters are counted from
 * Ci.
 * 
 * @author Wittawat Jitkrittum
 */
public class SeparateRatioFU extends AbstractNumericalFeatureUnit {

    public static final char DELIMITER = '|';
    /**The number of character to the left of the current characters
    to consider.*/
    private final int leftDependenceCount;
    /**The number of character to the right of the current characters
    to consider.*/
    private final int rightDependenceCount;
    private final CharSeqCounter counter;
    /**Offset from the current index.*/
    private final int relativePosition;
    /**Characters to be replaced at the same position as | */
    private static final char[] REPLACED_CHARS = {
        'ก', 'ข', 'ฃ', 'ค', 'ฅ', 'ฆ', 'ง', 'จ', 'ฉ', 'ช', 'ซ', 'ฌ', 'ญ', 'ฎ',
        'ฏ', 'ฐ', 'ฑ', 'ฒ', 'ณ', 'ด', 'ต', 'ถ', 'ท', 'ธ', 'น', 'บ', 'ป', 'ผ',
        'ฝ', 'พ', 'ฟ', 'ภ', 'ม', 'ย', 'ร', 'ฤ', 'ล', 'ฦ', 'ว', 'ศ', 'ษ', 'ส',
        'ห', 'ฬ', 'อ', 'ฮ', 'ฯ', 'ะ', 'ั', 'า', 'ำ', 'ิ', 'ี', 'ึ', 'ื', 'ุ',
        'ู', 'เ', 'แ', 'โ', 'ใ', 'ไ', 'ๅ', 'ๆ',
        '็', '่', '้', '๊', '๋', '์', '.', ',', ' ', '\'', '"'
    };
    /**Value to be returned when character range is invalid 
     * e.g. when index out of bound occurs.*/
    public static final double INVALID_CHAR_RANGE_VALUE = -1;
    /**Whether to normalize the ratio so that the result becomes probability (0 to 1).*/
    private boolean normalized = true;

    public SeparateRatioFU(String attributeName, int leftDependenceCount,
            int rightDependenceCount, CharSeqCounter counter, int relativePosition, boolean normalized) {
        super(attributeName);
        this.leftDependenceCount = leftDependenceCount;
        this.rightDependenceCount = rightDependenceCount;
        this.counter = counter;
        this.relativePosition = relativePosition;

    }

    public SeparateRatioFU(String attributeName,
            int leftDependenceCount, int rightDependenceCount,
            CharSeqCounter counter, int relativePosition) {
        this(attributeName, leftDependenceCount,
                rightDependenceCount, counter, relativePosition, true);
    }

    public SeparateRatioFU(String attributeName,
            int leftDependenceCount, int rightDependenceCount,
            CharSeqCounter counter) {

        this(attributeName, leftDependenceCount,
                rightDependenceCount, counter, 0);
    }

    public double getAttributeValue(int currentIndex) {
        final int effectiveIndex = currentIndex + relativePosition;
        final int start = effectiveIndex - leftDependenceCount;
        final int end = effectiveIndex + rightDependenceCount;
        final int length = charSource.length();
        if (start >= 0 && end < length) { //Valid character range
            final int rangeLength = end - start + 2; // +1 for |
            char[] charRange = new char[rangeLength];
            final int leftLength = leftDependenceCount + 1; // +1 for the current index
//            char[] leftRange = new char[leftLength]; //..., Ci-2, Ci-1, Ci

            for (int i = 0; i < leftLength; ++i) {
                char chI = charSource.charAt(i + start);
                charRange[i] = chI;
//                        leftRange[i] = chI;
                assert i != leftLength - 1 || i + start == effectiveIndex;
            }
            charRange[leftLength] = DELIMITER;
            for (int i = leftLength + 1; i < rangeLength; ++i) {
                charRange[i] = charSource.charAt(i + start - 1);
            }

            long countSep = counter.count(charRange, 0, charRange.length);
//            System.out.printf("substring: %10s , count: %d \n", new String(charRange), countSep);
            if (countSep <= 0) {
                return 0.0;
            }

            long freqSum = 0;
            // Find sum(freq(..., Ci-2, Ci-1, Ci, *, Ci+1, Ci+2, ...  ))
            char[] followings = REPLACED_CHARS;
//                    counter.charactersFollowing(leftRange, 0, leftLength);
            for (char follow : followings) {
                //Set the position after Ci to be the character that can follow.
                charRange[leftLength] = follow;
                long replacedCount = counter.count(charRange, 0, rangeLength);
                freqSum += replacedCount;
//                System.out.printf("substring replaced: %10s , count: %d \n", new String(charRange), replacedCount);
            }
            if(normalized){
                freqSum += countSep;
            }
            if (freqSum <= 0) {
                assert !normalized;
                return Double.MAX_VALUE;
            }

            double ratio = (double) countSep / (double) freqSum;
            assert ratio > 0;


//            System.out.printf("substring: %10s, ratio: %f , count: %d, sum other count: %d\n", charSource.substring(start, end + 1), ratio,
//                    countSep, freqSum);
            return ratio;
        } else {
            return INVALID_CHAR_RANGE_VALUE;
        }
    }

    public boolean isNormalized() {
        return normalized;
    }

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }

    public static void main(String[] args) throws Exception {
        String content = FileUtils.readFileToString(new File("/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt"));
        CharSeqCounter counter = Data.getCharGramsTrie();
        SeparateRatioFU fu = new SeparateRatioFU("sepratiofu", 2, 2, counter);
//        fu.setNormalized(false);
        System.out.println("Observed chars: " + counter.observedCharacters().length);
        fu.setCharSource(content);
        for (int i = 0; i < content.length(); ++i) {
            double ratio = fu.getAttributeValue(i);
        }


    }
}
