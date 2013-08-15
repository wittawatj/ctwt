package com.wittawat.wordseg.word;

import java.io.Serializable;

/**
 * A sequence of characters.
 * 
 * @author Wittawat Jitkrittum
 */
public class CharSequence implements Sequence<Character>, Serializable {

    private String string;

    public CharSequence(String string) {
        this.string = string;
    }

    public Character get(int i) {
        return string.charAt(i);
    }

    public int length() {
        return string.length();
    }

    public Sequence<Character> add(Sequence<Character> items) {
        StringBuilder buf = new StringBuilder();
        buf.append(this.string);

        int length = items.length();
        for (int i = 0; i < length; ++i) {
            Character ch = items.get(i);
            assert ch != null;
            buf.append(ch);
        }
        String all = buf.toString();
        return new CharSequence(all);
    }

    
}
