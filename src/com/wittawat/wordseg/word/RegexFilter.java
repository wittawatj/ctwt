package com.wittawat.wordseg.word;

import com.wittawat.wordseg.data.BESTCorpus;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class RegexFilter implements Filter<String> {

    public static final Filter NE_TAG_FILTER = new RegexFilter("(?s)" + BESTCorpus.TAG_NE_START + "(.*?)" + BESTCorpus.TAG_NE_END);
    public static final Filter AB_TAG_FILTER = new RegexFilter("(?s)" + BESTCorpus.TAG_AB_START + "(.*?)" + BESTCorpus.TAG_AB_END);
    public static final Filter POEM_TAG_FILTER = new RegexFilter("(?s)" + BESTCorpus.TAG_POEM_START + "(.*?)" + BESTCorpus.TAG_POEM_END);
    public static final Filter NO_TAG_FILTER = new NotWordFilter(new OrFilter(NE_TAG_FILTER, AB_TAG_FILTER, POEM_TAG_FILTER));
    private String regex;

    public RegexFilter(String regex) {
        this.regex = regex;
    }

    public boolean accepts(String word) {
        return word.matches(regex);
    }
}
