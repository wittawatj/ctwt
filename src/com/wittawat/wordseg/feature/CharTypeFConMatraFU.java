package com.wittawat.wordseg.feature;

import java.util.regex.Pattern;
import weka.core.Attribute;
import weka.core.FastVector;

/**
 * Similar to <code>CharTypeFU</code> except
 * that final consonants are further divided
 * according to Thai's Matra.
 * http://www.panyathai.or.th/wiki/index.php/%E0%B8%A1%E0%B8%B2%E0%B8%95%E0%B8%A3%E0%B8%B2%E0%B8%95%E0%B8%B1%E0%B8%A7%E0%B8%AA%E0%B8%B0%E0%B8%81%E0%B8%94
 *
 * O_ANG (อ) is treated as a final consonant and has its own matra.
 * 
 * @author Wittawat Jitkrittum
 */
public class CharTypeFConMatraFU extends CharTypeFU {

    /**แม่กก*/
    public static final String CHARS_MATRA_KOK = "กขฃคฆ";
    public static final Pattern PAT_MATRA_KOK = Pattern.compile("[" + CHARS_MATRA_KOK + "]");
    public static final String T_FC_KOK = "ก";
    /**แม่กด*/
    public static final String CHARS_MATRA_KOT = "จดตถทธฏฎฑฒชซศษสฐ";
    public static final Pattern PAT_MATRA_KOT = Pattern.compile("[" + CHARS_MATRA_KOT + "]");
    public static final String T_FC_KOT = "ด";
    /**แม่กบ*/
    public static final String CHARS_MATRA_KOP = "บปพฟภ";
    public static final Pattern PAT_MATRA_KOP = Pattern.compile("[" + CHARS_MATRA_KOP + "]");
    public static final String T_FC_KOP = "บ";
    /**แม่กน*/
    public static final String CHARS_MATRA_KON = "ณนญรลฬ";
    public static final Pattern PAT_MATRA_KON = Pattern.compile("[" + CHARS_MATRA_KON + "]");
    public static final String T_FC_KON = "น";
    /**แม่กง*/
    public static final String CHARS_MATRA_KONG = "ง";
    public static final String T_FC_KONG = "ง";
    /**แม่กม*/
    public static final String CHARS_MATRA_KOM = "ม";
    public static final String T_FC_KOM = "ม";
    /**แม่เกย*/
    public static final String CHARS_MATRA_KOEY = "ย";
    public static final String T_FC_KOEY = "ย";
    /**แม่เกอว*/
    public static final String CHARS_MATRA_KOEW = "ว";
    public static final String T_FC_KOEW = "ว";
    /**อ*/
    public static final String CHARS_O_ANG = "อ";
    public static final String T_O_ANG = "อ";

    public CharTypeFConMatraFU(String attributeName) {
        super(attributeName);
    }

    public CharTypeFConMatraFU(String attributeName, int relativePosition) {
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
            // Add Matra types
            final String[] matraTypes = {T_FC_KOK, T_FC_KOT, T_FC_KOP, T_FC_KON,
                T_FC_KONG, T_FC_KOM, T_FC_KOEY, T_FC_KOEW, T_O_ANG};
            for (String matra : matraTypes) {
                POSSIBLE_VALUES.addElement(matra);
            }

            attribute = new Attribute(getAttributeName(), POSSIBLE_VALUES);
        }
        return attribute;
    }

    private String getMatraType(char ch) {
        final String chStr = String.valueOf(ch);
        if (PAT_MATRA_KOK.matcher(chStr).find()) {
            return  T_FC_KOK;

        } else if (PAT_MATRA_KOT.matcher(chStr).find()) {
            return T_FC_KOT;

        } else if (PAT_MATRA_KOP.matcher(chStr).find()) {
            return T_FC_KOP;

        }else if(PAT_MATRA_KON.matcher(chStr).find()){
            return T_FC_KON;

        }else if(CHARS_MATRA_KONG.equals(chStr)){
            return T_FC_KONG;

        }else if(CHARS_MATRA_KOM.equals(chStr)){
            return T_FC_KOM;

        }else if(CHARS_MATRA_KOEY.equals(chStr)){
            return T_FC_KOEY;

        }else if(CHARS_MATRA_KOEW.equals(chStr)){
            return T_FC_KOEW;

        }else if(CHARS_O_ANG.equals(chStr)){
            return T_O_ANG;
        }else{
            throw new RuntimeException("Impossible final consonant occurs: "+ch);
        }

    }

    @Override
    public double getAttributeValue(int currentIndex) {
        final int effectiveIndex = currentIndex + relativePosition;
        String type = getTypeOfCharAt(currentIndex, relativePosition, charSource);

        if (type.equals(T_FINAL_CONSONANT)) {
            char ch = charSource.charAt(effectiveIndex);
            assert String.valueOf(ch).matches("[" + FINAL_CONSONANTS + "]");
            type = getMatraType(ch);
            assert POSSIBLE_VALUES.contains(type);
        }
        int index = POSSIBLE_VALUES.indexOf(type);
        assert index >= 0;
        return index;

    }
}
