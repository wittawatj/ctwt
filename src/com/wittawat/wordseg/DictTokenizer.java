package com.wittawat.wordseg;

import com.wittawat.wordseg.AbstractWordTokenizer;
import com.wittawat.wordseg.utils.HashTrie;
import com.wittawat.wordseg.utils.MyStringUtils;
import com.wittawat.wordseg.utils.Pair;
import com.wittawat.wordseg.utils.Trie;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A dictionary based word tokenizer. A <code>Trie</code> is used
 * internally to store the list of all words.
 * 
 * @author Wittawat Jitkrittum
 */
public abstract class DictTokenizer extends AbstractWordTokenizer{

    protected Trie trie;

    /**Initialize the tokenizer with the given words.*/
    public DictTokenizer(String content, Iterator<String> words) {

        this(content, words, new HashTrie());
    }

    public DictTokenizer(String content, Iterator<String> words, Trie trie) {
        super(content);
        while (words.hasNext()) {
            String w = words.next();
            // remove new lines
            w = MyStringUtils.removeAllNewLines(w);
            trie.put(w);
        }
        this.trie = trie;
    }

    @Override
    public Iterator<String> iterateTokens() {
        List<Pair<Integer, Double>> indexes = getEndTokenIndexes(content);
        return new TokenizeIterator(content, indexes);
    }

    @Override
    public List<String> listTokens() {
        List<String> list = new ArrayList<String>();
        Iterator<String> tokenIt = iterateTokens();
        while (tokenIt.hasNext()) {
            String token = tokenIt.next();
            list.add(token);
        }
        return list;
    }

    @Override
    public String tokenize() {
        Iterator<String> tokenIt = iterateTokens();
        StringBuilder buf = new StringBuilder();
        while (tokenIt.hasNext()) {
            String tok = tokenIt.next();
            buf.append(tok).append(delimiter);
        }
        return buf.toString();

    }


    ////////////////////////////////////////
   
}
