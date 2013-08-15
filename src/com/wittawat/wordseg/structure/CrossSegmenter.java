package com.wittawat.wordseg.structure;

import com.wittawat.wordseg.BasicWordTokenizer;
import com.wittawat.wordseg.data.TextSource;
import java.util.ArrayList;
import java.util.List;

/**
 * A cross checker which can run a list of word tokenizers
 * against a list of text sources. For each, pair of
 * a tokenizer and a text source, specified action(s) are performed.
 *
 * @author Wittawat Jitkrittum
 */
public class CrossSegmenter {

    private List<BasicWordTokenizer> tokenizers;
    private List<TextSource> textSources;
    private List<SegmentingHandler> segmentingHandlers;

    public CrossSegmenter(List<BasicWordTokenizer> tokenizers, List<TextSource> textSources) {
        if (tokenizers == null || tokenizers.size() == 0) {
            throw new IllegalArgumentException("tokenizers cannot be blank.");
        }
        if (textSources == null || textSources.size() == 0) {
            throw new IllegalArgumentException("textSources cannot be blank.");
        }
        this.tokenizers = tokenizers;
        this.textSources = textSources;
    }

    /**Run the list of word tokenizers
     * against the list of text sources.*/
    public void crossSegment() {
        for(TextSource source : textSources){
            String text = source.getText();
            for(BasicWordTokenizer tok : tokenizers){
                throw new UnsupportedOperationException();

            }
        }
    }

    private List<SegmentingHandler> getSegmentingHandlers() {
        if (segmentingHandlers == null) {
            segmentingHandlers = new ArrayList<SegmentingHandler>();
        }
        return segmentingHandlers;
    }

    public void addSegmentingHandler(SegmentingHandler handler) {
        getSegmentingHandlers().add(handler);
    }

    public void removeSegmentingHandler(SegmentingHandler handler) {
        getSegmentingHandlers().remove(handler);
    }
}
