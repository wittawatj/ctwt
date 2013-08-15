package com.wittawat.wordseg.once;

import com.wittawat.wordseg.utils.MyStringUtils;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;

/**
 * Generate a test file from BEST's corpus file.
 * @author Wittawat Jitkrittum
 */
public class GenTestFile {

    private File source;
    private boolean cleanDelimiters = true;
    private boolean cleanBESTTags = true;

    public GenTestFile(File source) {
        this.source = source;
    }

    public GenTestFile(String path) {
        this(new File(path));
    }

    public static void writeFolders(String sourceFolder, String destRoot) throws IOException {
        writeFolders(new File(sourceFolder), new File(destRoot));
    }

    public static void writeFolders(File sourceFolder, File destRoot) throws IOException {
        assert sourceFolder.isDirectory();
        assert destRoot.isDirectory();
        Iterator<File> files = FileUtils.iterateFiles(sourceFolder, new String[]{"txt"}, true);
        while (files.hasNext()) {
            File f = files.next();
            String fName = f.getName();
            File dest = FileSystemView.getFileSystemView().createFileObject(destRoot, fName);

            new GenTestFile(f).writePlainText(dest);
            System.out.println("Wrote to: " + dest.getAbsolutePath());
        }
    }

    public String getPlainText() throws IOException {
        String raw = FileUtils.readFileToString(source);
        if (cleanDelimiters) {
            raw = raw.replaceAll("\\Q|\\E", "");
        }
        if (cleanBESTTags) {
            raw = MyStringUtils.cleanBESTTags(raw);
        }
        return raw;
    }

    public void writePlainText(File dest) throws IOException {
        String content = getPlainText();
        FileUtils.writeStringToFile(dest, content);

    }

    public static void main(String[] args) throws Exception {
        String source = "/media/SHARE/QA_project_resources/Best_Corpus/train";
        String dest = "/media/SHARE/QA_project_resources/Best_Corpus/test/corpus_ans";
        writeFolders(source, dest);

    }
}
