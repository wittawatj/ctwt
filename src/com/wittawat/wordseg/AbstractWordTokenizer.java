package com.wittawat.wordseg;

import com.wittawat.wordseg.utils.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Wittawat Jitkrittum
 */
public abstract class AbstractWordTokenizer implements WordTokenizer {

    public static WordTokenizer getDefaultWordTokenizer(String content) throws Exception {
        NukeTokenizer3 tok = new NukeTokenizer3(content, Data.getDefaultModel());
        return tok;
    }
    protected String delimiter = "|";
    protected String content;
    private double cutProbThreshold = 0.5;

    public AbstractWordTokenizer(String content) {
        this.content = content;
    }

    public Iterator<String> iterateTokens() {
        return iterateTokens(content);
    }

    protected Iterator<String> iterateTokens(String content) {
        List<Pair<Integer, Double>> indexes = getEndTokenIndexes(content);
        return new TokenizeIterator(content, indexes);
    }

    public String getSourceContent() {
        return content;
    }

    public List<String> listTokens() {

        Iterator<String> tokenIt = iterateTokens();
        List<String> list = new ArrayList<String>();
        while (tokenIt.hasNext()) {
            String token = tokenIt.next();
            list.add(token);
        }
        return list;
    }

    protected String getString(Iterator<String> tokenIt) {
        StringBuilder buf = new StringBuilder();
        while (tokenIt.hasNext()) {
            String tok = tokenIt.next();
            buf.append(tok).append(delimiter);
        }
        return buf.toString();
    }

    public String tokenize() {
        Iterator<String> tokenIt = iterateTokens();
        return getString(tokenIt);

    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**@return a list of end token indexes (sorted) for the content. For example,
    for  "abc|de|f|g|", this would return [3, 5, 6, 7]. Associated with each integer
    index is its probability of tokenization.*/
    protected abstract List<Pair<Integer, Double>> getEndTokenIndexes(String content);
    ////////////////////////////////////////////////

    protected class TokenizeIterator implements Iterator<String> {

        private String content;
        private Iterator<Pair<Integer, Double>> endTokenIndexes;
        private int curCharIndex = 0;
        private String nextToken;

        public TokenizeIterator(String content, List<Pair<Integer, Double>> endTokenIndexes) {
            if (content == null) {
                throw new IllegalArgumentException("content must not be null.");
            }
            this.content = content;
            this.endTokenIndexes = new ArrayList<Pair<Integer, Double>>(endTokenIndexes).iterator();
            this.nextToken = findNextToken();
        }

        private String findNextToken() {
            while (endTokenIndexes.hasNext() && !content.equals("")) {
                Pair<Integer, Double> next = endTokenIndexes.next();
                if (next.getValue2() >= cutProbThreshold) {
                    int nextEndIndex = next.getValue1();

                    String token = content.substring(curCharIndex, nextEndIndex);
                    curCharIndex = nextEndIndex;
                    return token;
                }

            }
            return null;

        }

        public boolean hasNext() {
            return nextToken != null;
        }

        public String next() {
            String toReturn = nextToken;
            nextToken = findNextToken();
            return toReturn;
        }

        public void remove() {
        }
    }
}
