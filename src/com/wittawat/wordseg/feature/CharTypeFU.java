package com.wittawat.wordseg.feature;

import java.util.regex.Pattern;

import weka.core.Attribute;
import weka.core.FastVector;

/**
 * A <code>FeatureUnit</code> which generates features based on character type.
 * These are the first features in my NSC2010 proposal.
 * 
 * @author nook
 */
public class CharTypeFU extends AbstractFeatureUnit {

    public static final String //
            T_FINAL_CONSONANT = "c",
            T_NON_FINAL_CONSONANT = "n", //
            T_CONSONANT_NOT_BEGIN_WORDS = "r",
            T_VOWEL_NOT_BEGIN_WORDS = "v", //
            T_VOWEL_BEGIN_WORDS = "w",//
            T_TONE = "t", //
            T_SYMBOL = "s", //
            T_DIGIT = "d",//
            T_QUOTE = "q",//
            T_SPACE = "p", //
            T_FOREIGN_LETTER = "f",
            T_OTHER = "o",//
            T_ERROR = "e";
    static final String[] T_ARRAY = {T_FINAL_CONSONANT, T_NON_FINAL_CONSONANT,
        T_CONSONANT_NOT_BEGIN_WORDS, T_VOWEL_NOT_BEGIN_WORDS, T_VOWEL_BEGIN_WORDS, T_TONE, T_SYMBOL,
        T_DIGIT, T_QUOTE, T_SPACE, T_FOREIGN_LETTER, T_OTHER, T_ERROR};
    static final Pattern P_DIGIT = Pattern.compile("[0-9\u0E50-\u0E59]");
    static final String FINAL_CONSONANTS = "กขคฆงจชซญฐดตถทธนบปพฟภมยรลวศสอ";
    static final String NON_FINAL_CONSONANTS = "ฅฉผฝฌหฮฤฦ";
    static final String CONSONANT_NOT_BEGIN_WORDS = "ฏฎฑฒฬษณฃ";
    static final String VOWEL_NOT_BEGIN_WORDS = "ะิีึืุูัาำๅ็";
    static final String VOWEL_BEGIN_WORDS = "เแโใไ";
    static final String TONE = "่้๊๋";
    static final String SYMBOL = "์ๆฯ.,\u0E4D\u0E4E\u0E4F\u0E5A\u0E5B";
    static final String QUOTE = "\"'({[]})";
    static String[] TYPES;
//    static final String[] T_PATTERN_STRINGS = {
//        "[" + FINAL_CONSONANTS + "]", "[" + NON_FINAL_CONSONANTS + "]",
//        "[" + VOWEL_NOT_BEGIN_WORDS + "]", "[" + VOWEL_BEGIN_WORDS + "]", "[" + TONE + "]",
//        "[์ๆฯ" + Pattern.quote(".") + Pattern.quote(",") + "]", "[0-9๑-๙]",
//        "[]", "\\s", ".", ""};
    protected Attribute attribute;
    protected static FastVector POSSIBLE_VALUES;
    protected int relativePosition;

    static {
        // Initilize TYPES. TYPES contains the types of all THai characters
        final char lastChar = '\u0E4C';
        TYPES = new String[lastChar - 'ก' + 1];
        char ch = 'ก';

        for (int i = 0; ch <= lastChar; ch++, ++i) {
            String t = null;
            if (contains(FINAL_CONSONANTS, ch)) {
                t = T_FINAL_CONSONANT;
            } else if (contains(NON_FINAL_CONSONANTS, ch)) {
                t = T_NON_FINAL_CONSONANT;
            } else if (contains(CONSONANT_NOT_BEGIN_WORDS, ch)) {
                t = T_CONSONANT_NOT_BEGIN_WORDS;
            } else if (contains(VOWEL_NOT_BEGIN_WORDS, ch)) {
                t = T_VOWEL_NOT_BEGIN_WORDS;
            } else if (contains(VOWEL_BEGIN_WORDS, ch)) {
                t = T_VOWEL_BEGIN_WORDS;
            } else if (contains(TONE, ch)) {
                t = T_TONE;
            } else if (contains(SYMBOL, ch)) {
                t = T_SYMBOL;
            } else {
                if (!String.valueOf(ch).matches("[\u0E3A-\u0E3F]")) {
                    throw new RuntimeException("Unmatched type: " + ch);
                }
            }

            TYPES[i] = t;
        }
        // INitialize possible values;
        POSSIBLE_VALUES = new FastVector(T_ARRAY.length);
        for (String type : T_ARRAY) {
            POSSIBLE_VALUES.addElement(type);
        }
    }

    public CharTypeFU(String attributeName, int relativePosition) {
        super(attributeName);
        this.relativePosition = relativePosition;
        getAttribute();

    }

    public CharTypeFU(String attributeName) {
        this(attributeName, 0);

    }

    static String getTypeOfChar(Character ch) {
        String t = null;
        int index = ch - 'ก';
        if (index < TYPES.length && index >= 0 && TYPES[index] != null) {
            t = TYPES[index];
        } else if (contains(SYMBOL, ch)) {
            t = T_SYMBOL;

        } else if (contains(QUOTE, ch)) {
            t = T_QUOTE;
        } else if (Character.isWhitespace(ch)) {
            t = T_SPACE;
        } else if (P_DIGIT.matcher(String.valueOf(ch)).find()) {
            t = T_DIGIT;
        } else if (Character.isLetter(ch)) {
            t = T_FOREIGN_LETTER;
        } else {
            t = T_OTHER;
        }

        return t;
    }

    private static boolean contains(String str, char ch) {
        final int length = str.length();
        for (int i = 0; i < length; ++i) {
            if (str.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Attribute getAttribute() {
        if (attribute == null) {
            assert POSSIBLE_VALUES != null;
            attribute = new Attribute(getAttributeName(), POSSIBLE_VALUES);
        }
        return attribute;
    }

    static String getTypeOfCharAt(int currentIndex, int relativePosition, String charSource) {
        int effectiveIndex = currentIndex + relativePosition;

        if (effectiveIndex >= 0 && effectiveIndex < charSource.length()) {
            Character c = charSource.charAt(effectiveIndex);
            String type = getTypeOfChar(c);
            return type;
        }
        return T_ERROR;
    }

    @Override
    public double getAttributeValue(int currentIndex) {
        String type = getTypeOfCharAt(currentIndex, this.relativePosition, this.charSource);
        return POSSIBLE_VALUES.indexOf(type);

    }

    public Object getRawAttributeValue(int currentIndex) {
        double indexDouble = getAttributeValue(currentIndex);
        int index = (int) indexDouble;
        assert Math.abs(indexDouble - index) < 10e-5;
        Object raw = POSSIBLE_VALUES.elementAt(index);
        return raw;
    }

    public static void main(String[] args) {
        String thais = "";
        for (char c = 'ก'; c <= '\u0E5B'; ++c) {
            thais += c;
        }
        thais += "abcd{)(*^#}";
        CharTypeFU fu = new CharTypeFU(thais);
        fu.setCharSource(thais);
        for (int i = 0; i < thais.length(); ++i) {
            System.out.printf("ch: %c, type: %s\n", thais.charAt(i), fu.getRawAttributeValue(i));

        }

    }
}
