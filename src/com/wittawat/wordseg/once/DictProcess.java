

package com.wittawat.wordseg.once;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class DictProcess {

    public static void main(String[] args) throws Exception{
        File source = new File(
//                "/home/nook/Desktop/lexitron_wiki.txt"
                "data/BEST_AB.txt"
                );
        List<String> lines = FileUtils.readLines(source);
        Collections.sort(lines, new Comparator<String>() {

            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }

        });

        FileUtils.writeLines(new File(
//                "/home/nook/Desktop/lexitron_wiki_length_order.txt"
                "data/BEST_AB_sort.txt"
                ), lines);
    }
}
