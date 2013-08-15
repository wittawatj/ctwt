package com.wittawat.wordseg.data;

import com.wittawat.wordseg.utils.MyStringUtils;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Pattern;

import org.apache.commons.collections.iterators.ObjectArrayIterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * A <code>WordIterator</code> which iterates words delimited by character(s),
 * usually by '|' (default delimiter).
 * 
 * @author nook
 * @deprecated 
 */
public class DelimitedWordIterator implements WordIterator {

    public static final String DEFAULT_DELIMITER = "|";

    private final LineIterator lineIterator;
    private String delimiter = Pattern.quote(DEFAULT_DELIMITER);
    private ObjectArrayIterator lineWordIterator = new ObjectArrayIterator();

    public DelimitedWordIterator(File textFile, String encoding)
            throws IOException {
        super();
        this.lineIterator = FileUtils.lineIterator(textFile, encoding);
        feedLine();
    }

    public DelimitedWordIterator(File textFile) throws IOException {
        this(textFile, "UTF-8");
    }

    /**content with delimiter separated words.*/
    public DelimitedWordIterator(Reader content, String delimiter){
        this.lineIterator = new LineIterator(content);
        this.delimiter = delimiter;
        feedLine();
    }
    public DelimitedWordIterator(Reader content){
        this(content, Pattern.quote(DEFAULT_DELIMITER));
    }
    public DelimitedWordIterator(String content, String delimiter){
        this(new StringReader(content), delimiter);
    }
    public DelimitedWordIterator(String content){
        this(content, Pattern.quote(DEFAULT_DELIMITER));
    }
    
    private void feedLine() {
        assert lineIterator != null;
        if (lineIterator.hasNext()) {
            String next = lineIterator.nextLine();
//            System.out.println("#"+next+"#");

            String append = "\n" ;
            String line = MyStringUtils.toDefaultEndOfLines(next) + append ;
//            System.out.println("!"+line+"!");
            
            if (line != null) {
                String[] words = line.split(delimiter);
                if (words != null) {
                    lineWordIterator = new ObjectArrayIterator(words);
                    return;
                }
            }
        }
        lineWordIterator = null;

    }

    @Override
    public boolean hasNext() {
        if (lineWordIterator == null) {
            return false;

        } else if (lineWordIterator.hasNext()) {
            return true;

        } else {
            // No more words to iterate in the current line.
            feedLine();
            // Recursion
            return hasNext();
        }

    }

    @Override
    public String next() {
        if (hasNext()) {
            return (String) lineWordIterator.next();

        }
        return null;
    }

    @Override
    public void remove() {
        
    }

    public String getDelimiter() {
        return delimiter;
    }

    /**Set the regex delimiter used to split words.*/
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public void close() {

        try {
            
        } finally {

            LineIterator.closeQuietly(lineIterator);
        }
    }

    @Override
    protected void finalize() throws Throwable {

        super.finalize();
        close();
    }

    // //////////////////////////////////////////////
    public static void main(String[] args) throws Exception {
        WordIterator wi = new DelimitedWordIterator(
//                new File(
//                "/media/SHARE/QA_project_resources/InterBEST_Corpus/release1/article/article_00005.txt")
                "ทด|สอบ|การ|แบ่|ง|คำ|ขอ|ง| |WordIterator|.."
                );
        int i = 0;
        while (wi.hasNext()) {
            ++i;
            System.out.print(wi.next() + "*");
            if (i % 12 == 0) {
                System.out.println();
            }
        }

    }
}
