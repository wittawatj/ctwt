package com.wittawat.wordseg.data;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * A <code>WordSource</code> from files.
 * 
 * @author nook
 */
public class FileWordSource implements WordSource {

    private File file;

    public FileWordSource(File file) {
        super();
        this.file = file;
    }

    public FileWordSource(String path) {

        this(new File(path));
    }

    @Override
    public Iterator<String> iterator() {
        try {
            return new MemoryWordIterator(file);
        } catch (IOException e) {

            e.printStackTrace();

        }
        return null;
    }
}
