package com.wittawat.wordseg.word;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Filter accepting only words which contain at least
 * one Thai character.
 * 
 * @author Wittawat Jitkrittum
 */
public class ThaiCharFilter implements Filter<String> {

    private static Pattern PAT_THAI_CHAR = Pattern.compile("[\u0E01-\u0E5B]");

    public boolean accepts(String item) {
        Matcher m = PAT_THAI_CHAR.matcher(item);
        return m.find();

    }
    
}
