package com.wittawat.wordseg.data;

import com.wittawat.tcc.TCCTokenizer;
import com.wittawat.wordseg.utils.MyStringUtils;
import java.io.File;
import java.io.IOException;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;

/**
 * A kind of <code>DelimitedWordIterator</code> which iterates over
 * all TCCs (Thai Character Cluster) in a string content.
 * 
 * @author Wittawat Jitkrittum
 */
public class TCCIterator extends MemoryWordIterator {

    /**Construct a <code>TCCIterator</code> from an untokenized string
    content. It may be a good idea to protect BEST corpus's tag in the
    content first before passing the argument here.*/
    public TCCIterator(String content) throws RecognitionException {
        super(tokenizeTCC(MyStringUtils.toDefaultEndOfLines(content)));
    }

    public TCCIterator(File file) throws RecognitionException, IOException{
        this(FileUtils.readFileToString(file));
    }
    private static String tokenizeTCC(String content) throws RecognitionException {
        TCCTokenizer tok = new TCCTokenizer(content);
        tok.setDelimiter(MemoryWordIterator.DEFAULT_DELIMITER);
        String tokenized = tok.tokenize();
//        System.out.println(tokenized);
        return tokenized;

    }
    public static void main(String[] args) throws Exception{
        File textFile = new File("/media/SHARE/QA_project_resources/Best_Corpus/5 Million Words For BEST2010/encyclopedia/encyclopedia_00012.txt");
        String plain = MyStringUtils.removeBESTTags(MyStringUtils.toDefaultEndOfLines(FileUtils.readFileToString(textFile).replace("|", "")));
        plain = MyStringUtils.removeAllNewLines(plain);
        HiCharSource tccSource = new BESTHiCharSource(new TCCIterator(
                plain
                ));
        String tccPlain = tccSource.getPlainText();
        System.out.println("Original: "+plain);
        System.out.println("------------------------------------------");
        System.out.println("TCC Plain: "+tccPlain);
        System.out.println("------------------------------------------");
        System.out.println("Equals? : "+tccPlain.equals(plain));
        System.out.println("Original size: "+plain.length());
        System.out.println("TCC Plain size: "+tccPlain.length());

        int minSize = Math.min(plain.length(), tccPlain.length());

        for(int i=0;i<minSize;++i){
            if(tccPlain.charAt(i) != plain.charAt(i)){
                System.out.println("Diff at position: "+i);
                
                System.out.println("  Original i: "+Integer.toHexString(Character.codePointAt(plain, i)));
                System.out.println("  TCC Plain i: "+Integer.toHexString(Character.codePointAt(tccPlain, i)));
                break;
            }
        }
        
        int maxSize = Math.max(plain.length(), tccPlain.length());
        String longer = plain.length() >= tccPlain.length() ? plain : tccPlain;
        System.out.println("=== Rest diff: ");
        for(int i=minSize  ; i< maxSize ;++i){
            System.out.println("charAt index: "+i +" = \"" +longer.charAt(i)+"\"");
        }

        
    }
}
