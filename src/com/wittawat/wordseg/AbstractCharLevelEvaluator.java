package com.wittawat.wordseg;

import com.wittawat.wordseg.data.ContentContainer;

/**
 *
 * @author Wittawat Jitkrittum
 */
public abstract class AbstractCharLevelEvaluator implements CharLevelEvaluator {

    protected ContentContainer source;

    public AbstractCharLevelEvaluator(ContentContainer source) {
        this.source = source;
    }

    public AbstractCharLevelEvaluator() {
    }

    
    public ContentContainer getSource() {
        return source;
    }

    public void setSource(ContentContainer content) {
        this.source = content;
    }
}
