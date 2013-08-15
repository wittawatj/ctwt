package com.wittawat.wordseg.utils;

import com.wittawat.wordseg.data.BESTCorpus;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import weka.classifiers.Classifier;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class MyStringUtils {

    public static final String TAG_TCC_IGNORE_START = "<TCCIGNORE>";
    public static final String TAG_TCC_IGNORE_END = "</TCCIGNORE>";
    public static final String DEFAULT_NEW_LINE = "\n";
    private static char[] thaiCharsInUnicode;
    private static char[] engCharsInUnicode;

    public static boolean ascSorted(char[] chars) {

        for (int i = 0; i < chars.length - 1; ++i) {
            if (chars[i] > chars[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**@return an array of valid English characters in English unicode range
     * (including often used symbols).*/
    public static char[] getEnglishCharsInUnicode() {
        if (engCharsInUnicode == null) {
            char start = '\u0021';
            char end = '\u007E';
            int length = end - start + 1;
            engCharsInUnicode = new char[length];
            int index =0 ;
            for(char ch = start; ch <= end; ++ch){
                engCharsInUnicode[index]  = ch;
                ++index;
            }
        }
        return engCharsInUnicode;

    }

    /**@return an array of valid Thai characters in Thai unicode range.*/
    public static char[] getThaiCharsInUnicode() {
        if (thaiCharsInUnicode == null) {
            char part1Start = '\u0E01';
            char part1End = '\u0E3A';
            int part1Length = part1End - part1Start + 1;
            char part2Start = '\u0E3F';
            char part2End = '\u0E5B';
            int part2Length = part2End - part2Start + 1;
            int rangeLength = part1Length + part2Length;
            char[] chars = new char[rangeLength];

            int index = 0;
            //Construct part 1
            for (char ch = part1Start; ch <= part1End; ++ch) {
                chars[index] = ch;
                ++index;
            }

            // Construct part 2
            for (char ch = part2Start; ch <= part2End; ++ch) {
                chars[index] = ch;
                ++index;
            }
            thaiCharsInUnicode = chars;

        }

        return thaiCharsInUnicode;

    }

    public static void printArray(char[] array) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < array.length; ++i) {
            if (i == 0) {
                buf.append("[");
                buf.append("'").append(array[i]).append("'");
            } else {
                buf.append(",");
                buf.append("'").append(array[i]).append("'");

            }

            if (i == array.length - 1) {
                buf.append("]");

            }

        }
        System.out.println(buf.toString());
    }

    /**@return true if the character is in Thai unicode range.*/
    public static boolean isInThaiUnicodeRange(char ch) {
        return ch >= '\u0E01' && ch <= '\u0E5B';
    }

    /**@return the indexes of new line characters. Also, append content with
    newlines removed to buf.*/
    public static Integer[] findNewLineIndexes(String newLine, String content, StringBuilder buf) {
        List<Integer> indexes = new ArrayList<Integer>();
        final int contentLength = content.length();
        final int newLineLength = newLine.length();
        int fromIndex = 0;
        while (fromIndex < contentLength) {
            int newLineIndex = content.indexOf(newLine, fromIndex);
            if (newLineIndex != -1) {
                String sub = content.substring(fromIndex, newLineIndex);
                buf.append(sub);
                indexes.add(newLineIndex);

                fromIndex = newLineIndex + newLineLength;
            } else {
                // No more new line
                String lastChunk = content.substring(fromIndex, contentLength);
                buf.append(lastChunk);
                break;
            }

        }
        Integer[] indexArray = indexes.toArray(new Integer[]{});
        return indexArray;

    }

    public static String detectNewLine(String content) {
        if (content.contains("\r\n")) {
            // Assume Windows format
            return "\r\n";
        } else if (content.contains("\n")) {
            // Assume Unix format
            return "\n";
        } else if (content.contains("\r")) {
            // Mac ??
            return "\r";
        }
        return "\r\n"; // to be safe (Windows is the most widely used + should be able to display in Unix, Mac)
    }

    public static String getDelimitedString(List<Integer> endIndexes, String content, String delimiter) {
        int start = 0;
        StringBuilder buf = new StringBuilder();
        for (int end : endIndexes) {
            String w = content.substring(start, end);
            buf.append(w).append(delimiter);
            start = end;
        }
        return buf.toString();
    }

    /**Convert all new line characters to the default*/
    public static String toDefaultEndOfLines(String text) {
        if (text == null) {
            return null;
        }
        return text.replace("\r\n", DEFAULT_NEW_LINE).replace("\r", DEFAULT_NEW_LINE);
    }

    public static String getClassifiersName(Classifier... classifiers) {

        if (classifiers == null || classifiers.length == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        buf.append("[").append(classifiers[0].getClass().getName());
        for (int i = 1; i < classifiers.length; ++i) {
            buf.append(" , ").append(classifiers[i].getClass().getName());
        }
        buf.append("]");
        return buf.toString();
    }

    public static String removeAllNewLines(String text) {

        return text.replaceAll("(\r|\n)", "");
    }

    /**@return (a1 | a2 | ... | an) where each a is an element of patterns
    in order.*/
    public static String getOrRegexs(String[] patterns) {
        if (patterns == null || patterns.length == 0) {
            return "";
        }
        if (patterns.length == 1) {
            if (patterns[0] == null || patterns[0].trim().equals("")) {
                return "";
            }
        }
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        buf.append(patterns[0]);
        for (int i = 1; i < patterns.length; ++i) {
            String p = patterns[i];
            buf.append("|").append(p);
        }
        buf.append(")");
        return buf.toString();
    }

    /**Replace all BEST corpus's tags with blank strings
    regardless of whethere they come in pairs.*/
    public static String cleanBESTTags(final String ori) {
        String text = ori;
        text = text.replace(BESTCorpus.TAG_AB_START, "");
        text = text.replace(BESTCorpus.TAG_AB_END, "");
        text = text.replace(BESTCorpus.TAG_NE_START, "");
        text = text.replace(BESTCorpus.TAG_NE_END, "");
        text = text.replace(BESTCorpus.TAG_POEM_START, "");
        text = text.replace(BESTCorpus.TAG_POEM_END, "");
        return text;

    }

    /**Replace all BEST corpus's tags <NE>, <AB>, <POEM> with blank strings. NOt replace them,
    if they do not come in pairs.*/
    public static String removeBESTTags(String text) {
//        if(text == null){
//            return null;
//        }
        String[] search = {"(?s)<AB>(.*?)</AB>", "(?s)<NE>(.*?)</NE>", "(?s)<POEM>(.*?)</POEM>"};
        String[] replace = {"$1", "$1", "$1"};
        return replaceRegexEach(text, search, replace);
    }

    public static String encloseIgnoreTCCTag(String text) {
        if (text == null) {
            return null;
        }
        return TAG_TCC_IGNORE_START + text + TAG_TCC_IGNORE_END;
    }

    /**Enclose BEST tags with <TCCIGNORE> so that TCCParser does not
    tokenize them.*/
    public static String protectBESTTagsForTCCParser(final String text) {
        if (text == null) {
            return null;
        }
        String processed = text;
        // Protect <POEM>
        processed = replaceRegex(processed,
                "(?s)(<POEM>.*?</POEM>)",
                encloseIgnoreTCCTag("$1"));

        // Protect <NE>, <AB>
        processed = protectABNETags(processed, 0);
        return processed;

    }

    /**Remove all tags which cause TCCParser to ignore.*/
    public static String unprotectBESTTags(final String text) {
        if (text == null) {
            return null;
        }
        return text.replace(TAG_TCC_IGNORE_START, "").replace(TAG_TCC_IGNORE_END, "");

    }

    private static String protectABNETags(final String text, int fromIndex) {
        int neIndex = text.indexOf(BESTCorpus.TAG_NE_START, fromIndex);
        int abIndex = text.indexOf(BESTCorpus.TAG_AB_START, fromIndex);
        int neEndIndex = text.indexOf(BESTCorpus.TAG_NE_END, fromIndex);
        int abEndIndex = text.indexOf(BESTCorpus.TAG_AB_END, fromIndex);
        assert abIndex >= neIndex || abEndIndex < neIndex; //Assert that no NE nested inside AB
        if ((abIndex != -1 && neIndex < abIndex && neIndex != -1) ||
                (abIndex == -1 && neEndIndex != -1)) {
            assert neEndIndex != -1; //Assert that there is an </NE> after <NE>
            String beforeNe = text.substring(0, neIndex);
            String nePart = text.substring(neIndex, neEndIndex + BESTCorpus.TAG_NE_END.length());
            String afterNe = text.substring(neEndIndex + BESTCorpus.TAG_NE_END.length(), text.length());

            // Recursion
            return beforeNe + encloseIgnoreTCCTag(nePart) + protectABNETags(afterNe, 0);
        } else if ((neIndex != -1 && abIndex < neIndex && abIndex != -1) ||
                (neIndex == -1 && abIndex != -1)) {
            assert abEndIndex != -1; //Assert that there is an </AB> after <AB>
            String beforeAb = text.substring(0, abIndex);
            String abPart = text.substring(abIndex, abEndIndex + BESTCorpus.TAG_AB_END.length());
            String afterAb = text.substring(abEndIndex + BESTCorpus.TAG_AB_END.length(), text.length());

            //Recursion
            return beforeAb + encloseIgnoreTCCTag(abPart) + protectABNETags(afterAb, 0);

        }
        return text;

    }

    public static String replaceRegex(String text, String regexSearch, String replace) {
        return replaceRegexEach(text, new String[]{regexSearch}, new String[]{replace});
    }

    /**@return a new string after each pattern in regexSearch is replaced with each replacement
    in replace array. The replacement process goes one by one in order. Users must ensure that
    output of one replace does not yield a new input for the next one. This may cause an
    unexpected result. regexSearch.length == replace.length*/
    public static String replaceRegexEach(String text, String[] regexSearch, String[] replace) {
        assert regexSearch.length == replace.length;
        if (regexSearch.length != replace.length) {
            throw new RuntimeException("regexSearch and replace must be parallet arrays (same length).");
        }
        String processedText = text;
        for (int i = 0; i < regexSearch.length; ++i) {
            String searchI = regexSearch[i];
            String replaceI = replace[i];
            Pattern pat = Pattern.compile(searchI);
            Matcher m = pat.matcher(processedText);
            StringBuffer buf = new StringBuffer();
            while (m.find()) {
                m.appendReplacement(buf, replaceI);
            }
            m.appendTail(buf);
            processedText = buf.toString();
        }
        return processedText;
    }

    public static String toString(String[] a) {
        StringBuilder buf = new StringBuilder();

        buf.append("[");
        if (a.length > 0) {
            buf.append("\"").append(a[0]).append("\"");
        }
        for (int i = 1; i < a.length; ++i) {
            String w = a[i];
            buf.append(", ").append("\"").append(w).append("\"");
        }
        buf.append("]");
        return buf.toString();
    }
    //////////////////////////////////////////////

    public static void main(String[] args) throws Exception {
        printArray(getThaiCharsInUnicode());
    }
}
