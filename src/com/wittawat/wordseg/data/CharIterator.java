package com.wittawat.wordseg.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

/** A character iterator for a source of string. */
public class CharIterator implements Iterator<Character> {

    private BufferedReader reader;
    private String currentLine;
    private int lineIndex = -1;

    public CharIterator(File textFile) throws FileNotFoundException {
        this(new FileReader(textFile));

    }

    public CharIterator(Reader reader) {
        if (reader instanceof BufferedReader) {
            this.reader = (BufferedReader) reader;
        } else {
            this.reader = new BufferedReader(reader);
        }
        feedLine();
    }

    public CharIterator(String text) {
        this(new StringReader(text));
    }

    private void feedLine() {
        String l = null;
        try {
            l = reader.readLine();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            l = null;
        }
        currentLine = l == null ? null : l + "\n";
        lineIndex = currentLine == null ? -1 : 0;
    }

    @Override
    public boolean hasNext() {
        if (currentLine == null) {
            return false;
        }
        assert lineIndex != -1;
        if (lineIndex < currentLine.length()) {
            return true;
        } else {
            feedLine();
            // recursion
            return hasNext();
        }

    }

    @Override
    public Character next() {
        if (hasNext()) {
            Character toReturn = currentLine.charAt(lineIndex);
            ++lineIndex;
            return toReturn;
        }
        return null;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        super.finalize();
        reader.close();
    }

    public static void main(String[] args) throws Exception {
        File f = new File(
                "/media/SHARE/QA_project_resources/InterBEST_Corpus/release1/article/article_00001.txt");
        Iterator<Character> i = new CharIterator(f);
        while (i.hasNext()) {
            System.out.print(i.next());

        }
    }
}
