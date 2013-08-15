package com.wittawat.wordseg.utils;

import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Compilable;
import com.wittawat.tcc.TCCTokenizer;
import com.wittawat.wordseg.data.MemoryWordIterator;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Lingpipe's <code>TokenizerFactory</code> implementation
 * based on TCC.
 * 
 * @author Wittawat Jitkrittum
 */
public class TCCTokenizerFactory implements TokenizerFactory, Serializable, Compilable {

    public static final TCCTokenizerFactory INSTANCE = new TCCTokenizerFactory();

    public Tokenizer tokenizer(char[] content, int start, int length) {
        String str = new String(content, start, length);
        return new TCCLingPipeTokenizer(str);
    }

    public void compileTo(ObjectOutput oo) throws IOException {
        oo.writeObject(new Externalizer());
    }


    private static class TCCLingPipeTokenizer extends Tokenizer {

        private TCCTokenizer tccTokenizer;
        private Iterator<String> tokIt;

        public TCCLingPipeTokenizer(String content) {
            tccTokenizer = new TCCTokenizer(content);
            String tokenized = tccTokenizer.tokenizeOrNull();
            Iterator<String> it = new MemoryWordIterator(tokenized);
            tokIt = it;
        }

        @Override
        public String nextToken() {
            String tok = tokIt.hasNext() ? null : tokIt.next();
            return tok;
        }
    }

    private static class Externalizer extends AbstractExternalizable {

//        private static final long serialVersionUID = 1313238312180578595L;

        public Externalizer() {
            /* do nothing */
        }

        @Override
        public void writeExternal(ObjectOutput objOut) {
            /* do nothing */
        }

        @Override
        public Object read(ObjectInput objIn) {
            return INSTANCE;
        }
    }
}
