package com.wittawat.wordseg.data;

import com.wittawat.wordseg.utils.MyStringUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 * Similar to <code>DelimitedWordIterator</code>. This class
 * loads all contents instead of feeding line by line as in
 * <code>DelimitedWordIterator</code>
 * @author Wittawat Jitkrittum
 */
public class MemoryWordIterator implements WordIterator {

    public static final String DEFAULT_DELIMITER = "|";
    private String delimiter = Pattern.quote(DEFAULT_DELIMITER);
    private Iterator<String> words;

    public MemoryWordIterator(String content, String delimiter) {
        this.delimiter = delimiter;
        // Destroy all new line characters
        content = MyStringUtils.removeAllNewLines(content);

        String[] split = content.split(delimiter, -2);
        this.words = Arrays.asList(split).iterator();

    }
    public MemoryWordIterator(String content) {
        this(content, Pattern.quote(DEFAULT_DELIMITER));
    }

    public MemoryWordIterator(File textFile, String encoding) throws IOException {
        this(FileUtils.readFileToString(textFile, encoding));
    }

    public MemoryWordIterator(File textFile) throws IOException {
        this(FileUtils.readFileToString(textFile));
    }

    public void close() {
    }

    public boolean hasNext() {
        return words.hasNext();
    }

    public String next() {
        return words.next();
    }

    public void remove() {
        words.remove();
    }

    public String getDelimiter() {
        return delimiter;
    }

    /**Set the regex delimiter used to split words.*/
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
