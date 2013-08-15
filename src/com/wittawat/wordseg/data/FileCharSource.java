package com.wittawat.wordseg.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

/**A <code>CharSource</code> from a text file.*/
public class FileCharSource implements CharSource {

    private File textFile;

    public FileCharSource(File textFile) {
        super();
        this.textFile = textFile;
    }

    @Override
    public Iterator<Character> iterator() {

        try {
            return new CharIterator(this.textFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
