package com.wittawat.wordseg.feature;

/**
 *
 * @author Wittawat Jitkrittum
 */
public abstract class AbstractFUGroup implements FUGroup {

    protected String charSource;

    public String getCharSource() {
        return charSource;
    }

    public void setCharSource(String content) {
        this.charSource = content;

    }
}
