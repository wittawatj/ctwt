package com.wittawat.wordseg.data;

import com.wittawat.wordseg.data.BESTHiCharSource.WordInfo;
import com.wittawat.wordseg.utils.MyStringUtils;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A <code>HiCharSource</code> designed to work with BEST corpus
 * (http://thailang.nectec.or.th/best/?q=node/4). Currently,
 * nested tags are ignored. LIke <NE>...<AB>...</AB></NE>, <AB>..
 * </AB> will be treated as if it is a normal word.
 * 
 * @author nook
 */
public class BESTHiCharSource implements HiCharSource, WordSource {

    public static final int T_NORMAL_WORD = 0, T_NE = 2, T_AB = 1, T_POEM = 3;
    public static final String PATTERN_NE_STR = Pattern.quote("<NE>") + "(.+?)" + Pattern.quote("</NE>");
    public static final Pattern PATTERN_NE = Pattern.compile(PATTERN_NE_STR);
    public static final String PATTERN_AB_STR = Pattern.quote("<AB>") + "(.+?)" + Pattern.quote("</AB>");
    public static final Pattern PATTERN_AB = Pattern.compile(PATTERN_AB_STR);
    public static final String PATTERN_POEM_STR = Pattern.quote("<POEM>") + "(.+?)" + Pattern.quote("</POEM>");
    public static final Pattern PATTERN_POEM = Pattern.compile(PATTERN_POEM_STR);
    public static final Object[][] PATTERN_MAP = {{PATTERN_POEM, T_POEM}, {PATTERN_NE, T_NE}, {PATTERN_AB, T_AB}};
    // ------------------------------------------------------
    /** Map the first index of words to the corresponding words. */
    private final TreeMap<Integer, WordInfo> indexedWords;
    private int length = 0;
    private String plainText;
    private String delimitedPlainText;

    public BESTHiCharSource(File f) {
        this(new FileWordSource(f).iterator());
    }

    /**Auto remove TCC ignore tags from words.*/
    public BESTHiCharSource(Iterator<String> words) {
        indexedWords = new TreeMap<Integer, WordInfo>();
//        should support <NE>...<AB>..</AB><NE>
        while (words.hasNext()) {
            String next = words.next();
            // Use this.length as the index for the construction.
            addOneWordPossiblyNotTrimed(next);
        }

    }

    private void addOneWordPossiblyNotTrimed(String next) {
        if (!next.equals("")) {
            next = MyStringUtils.unprotectBESTTags(next);
            next = MyStringUtils.toDefaultEndOfLines(next);

            /**It is possible that 'next' contains a new line
            character at start or at the end because BEST corpus does
            not have a delimiter after a new line. So, new line characters
            might be grouped with words.*/
            while (next.startsWith(MyStringUtils.DEFAULT_NEW_LINE)) {
                this.length += genWordInfo(this.length, MyStringUtils.DEFAULT_NEW_LINE);
                next = next.substring(MyStringUtils.DEFAULT_NEW_LINE.length());
            }
            assert !next.startsWith(MyStringUtils.DEFAULT_NEW_LINE);

            if (!next.equals("")) {
                // Find the next location of the new line
                int nextNewLineIndex = next.indexOf(MyStringUtils.DEFAULT_NEW_LINE);
                if (nextNewLineIndex == -1) {
                    this.length += genWordInfo(this.length, next);
                } else {
                    String wordContent = next.substring(0, nextNewLineIndex);
                    assert !wordContent.startsWith(MyStringUtils.DEFAULT_NEW_LINE);
                    assert !wordContent.endsWith(MyStringUtils.DEFAULT_NEW_LINE);
                    this.length += genWordInfo(this.length, wordContent);

                    // Add the rest of new lines if any
                    String newLines = next.substring(nextNewLineIndex);
                    while (!newLines.equals("")) {
                        this.length += genWordInfo(this.length, MyStringUtils.DEFAULT_NEW_LINE);
                        newLines = newLines.substring(MyStringUtils.DEFAULT_NEW_LINE.length());
                    }
                }

            }
        }
    }

    /**@return word length*/
    private int genWordInfo(int curIndex, String word) {
        WordInfo wi = new WordInfo(word);
        assert !indexedWords.containsKey(curIndex);
        indexedWords.put(curIndex, wi);
//                System.out.println("Index: "+index+" = "+wi);
        assert word.length() > 0;
        return wi.getWordLength();


    }


    public int length() {
        assert this.length >= 0;
        return this.length;
    }

    
    public Character charAt(int i) {
        if (!isValidRange(i)) {
            return null;
        }
        Entry<Integer, WordInfo> entry = indexedWords.floorEntry(i);
        WordInfo wi = entry.getValue();
        String word = wi.getWord();
        assert !word.equals("");
        int beginIndex = entry.getKey();

        assert beginIndex <= i;
        int localIndex = i - beginIndex;


        assert localIndex != word.length();
        assert localIndex < word.length();
        return word.charAt(localIndex);
    }

    @Override
    public boolean isWordEnd(int i) {
        return isWordStartEnd(i, false);
    }

    @Override
    public boolean isWordStart(int i) {
        return isWordStartEnd(i, true);
    }

    private boolean isWordStartEnd(int i, boolean begin) {
        if (!isValidRange(i)) {
            throw new RuntimeException("index i="+i+" is out of range");
        }
        Entry<Integer, WordInfo> entry = indexedWords.floorEntry(i);
        WordInfo wi = entry.getValue();
        String word = wi.getWord();
        int beginIndex = entry.getKey();
        assert beginIndex <= i;
        int localIndex = i - beginIndex;
        assert localIndex < word.length();
        final int targetIndex = begin ? 0 : word.length() - 1;
        return localIndex == targetIndex;
    }

    /**
     * @return true if the ith character is a part of a named entity enclosed in
     *         <NE>...</NE>
     */
    public boolean isPartOfNE(int i) {
        return isPartOfType(i, T_NE);
    }

    public boolean isBeginNE(int i) {
        boolean b = isBeginEndType(i, true, T_NE);
        assert !b || isPartOfNE(i);
        return b;
    }

    public boolean isEndNE(int i) {
        boolean b = isBeginEndType(i, false, T_NE);
        assert !b || isPartOfNE(i);
        return b;
    }

    /**
     * @return true if the ith character is a part of an abbreviation enclosed
     *         in <AB>...</AB>
     */
    public boolean isPartOfAB(int i) {
        return isPartOfType(i, T_AB);
    }

    public boolean isBeginAB(int i) {
        boolean b = isBeginEndType(i, true, T_AB);
        assert !b || isPartOfAB(i);
        return b;
    }

    public boolean isEndAB(int i) {
        boolean b = isBeginEndType(i, false, T_AB);
        assert !b || isPartOfAB(i);
        return b;
    }

    /**
     * @return true if the ith character is a part of a poem enclosed in
     *         <POEM>...</POEM>
     */
    public boolean isPartOfPOEM(int i) {
        return isPartOfType(i, T_POEM);
    }

    public boolean isBeginPOEM(int i) {
        boolean b = isBeginEndType(i, true, T_POEM);
        assert !b || isPartOfPOEM(i);
        return b;
    }

    public boolean isEndPOEM(int i) {
        boolean b = isBeginEndType(i, false, T_POEM);
        assert !b || isPartOfPOEM(i);
        return b;
    }

    public Set<Integer> getDelimiterIndices() {
        return this.indexedWords.keySet();
    }

    private boolean isPartOfType(int i, int type) {
        if (!isValidRange(i)) {
            throw new RuntimeException("index i is out of range");
        }
        Entry<Integer, WordInfo> entry = indexedWords.floorEntry(i);
        WordInfo wi = entry.getValue();
        return wi.getWordType() == type;
    }

    private boolean isBeginEndType(int i, boolean begin, int type) {
        if (!isValidRange(i)) {
            throw new RuntimeException("index i is out of range");
        }
        Entry<Integer, WordInfo> entry = indexedWords.floorEntry(i);
        WordInfo wi = entry.getValue();
        String word = wi.getWord();
        int beginIndex = entry.getKey();
        assert beginIndex <= i;
        int localIndex = i - beginIndex;
        assert localIndex < word.length();
        final int targetIndex = begin ? 0 : word.length() - 1;
        return localIndex == targetIndex && wi.getWordType() == type;

    }

    private boolean isValidRange(int i) {
        return i >= 0 && i < length();
    }

    public String getPlainText() {
        if (plainText == null) {
            StringBuilder buf = new StringBuilder();
            for (Entry<Integer, WordInfo> e : indexedWords.entrySet()) {
                WordInfo wordInfo = e.getValue();
                String word = wordInfo.getWord();
                buf.append(word);
            }
            plainText = buf.toString();

        }
        return plainText;
    }

    public String getDelimitedPlainText() {
        if (delimitedPlainText == null) {
            StringBuilder buf = new StringBuilder();
            for (Entry<Integer, WordInfo> e : indexedWords.entrySet()) {
                WordInfo wordInfo = e.getValue();
                String word = wordInfo.getWord();
                buf.append("|").append(word);
            }
            delimitedPlainText = buf.toString();
        }
        return delimitedPlainText;
    }

    public Iterator<String> iterator() {
        return new WordIterator(indexedWords.entrySet().iterator());
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (WordInfo wi : indexedWords.values()) {
            int t = wi.getWordType();
            String type = t == T_NE ? "NE" : t == T_AB ? "AB"
                    : t == T_POEM ? "POEM" : "NORMAL";
            buf.append("( " + wi.getWord() + " , " + type + " )\n");
        }
        return buf.toString();
    }

    public static boolean equals(BESTHiCharSource src1, BESTHiCharSource src2) {
        String plain1 = src1.getPlainText();
        String plain2 = src2.getPlainText();
        if (plain1.length() != plain2.length()) {
            return false;
        }
        if (src1.length() != src2.length()) {
            return false;
        }

        for (int i = 0; i < plain1.length(); ++i) {
            if (plain1.charAt(i) != plain2.charAt(i)) {
                return false;
            }
        }
        int length = src1.length();
        for (int i = 0; i < length; ++i) {
            if (!src1.charAt(i).equals(src1.charAt(i))) {
                return false;
            }
        }
        return true;
    }
// //////////////////////////////

    static class WordIterator implements Iterator<String> {
        private Iterator<Entry<Integer, WordInfo>> entries;

        public WordIterator(Iterator<Entry<Integer, WordInfo>> entries ) {
            this.entries =entries;
        }

        public boolean hasNext() {
            return entries.hasNext();
        }

        public String next() {
            Entry<Integer, WordInfo> e = entries.next();
            return e.getValue().getWord();
        }

        public void remove() {

        }
        
    }
    // //////////////////////////////

    public static class WordInfo {

        private String word;
        private int wordType;

        public WordInfo(String word) {
            super();

            boolean notNormal = false;
            /******* <NE>, <AB> can be nested *******/
            /**Examples: <NE><AB>น.พ.</AB> ชาตรี บานชื่น</NE> , ตําแหนง|รอง| |<AB>ผบ.</AB>|<NE><AB> ตร.</AB> </NE>|*/
            for (Object[] pair : PATTERN_MAP) {
                Pattern pat = (Pattern) pair[0];
                int type = (Integer) pair[1];
                String result = checkPattern(word, pat);

                if (result != null) {
                    // Destroy nested tags.
                    result = MyStringUtils.cleanBESTTags(result);
                    this.word = result;
                    this.wordType = type;
                    notNormal = true;
                    break;
                }
            }
            if (!notNormal) {

                // Assume THe word is normal if result = null in each previous
                // loop.
                // Destroy nested tags.
                word = MyStringUtils.cleanBESTTags(word);
                this.word = word;
                this.wordType = T_NORMAL_WORD;

                assert this.wordType != T_NORMAL_WORD || !word.matches(PATTERN_NE_STR);
                assert this.wordType != T_NORMAL_WORD || !word.matches(PATTERN_AB_STR);
                assert this.wordType != T_NORMAL_WORD || !word.matches(PATTERN_POEM_STR);
                assert this.word != null;
                assert !this.word.trim().contains(BESTCorpus.TAG_AB_START);
                assert !this.word.trim().contains(BESTCorpus.TAG_AB_END);
                assert !this.word.trim().contains(BESTCorpus.TAG_NE_START);
                assert !this.word.trim().contains(BESTCorpus.TAG_NE_END);
                assert !this.word.trim().contains(BESTCorpus.TAG_POEM_START);
                assert !this.word.trim().contains(BESTCorpus.TAG_POEM_END);

            }


        }

        private String checkPattern(String w, Pattern pattern) {

            Matcher m = pattern.matcher(w);
            boolean found = m.find();

            if (found) {

                // Assume the whole word is of that type
                // There is a little error in case of e.g. |abc<NE>def</NE>|
                // In that case, "abcdef" will be NE.
                return w;
                //                return m.group(1)
            } else {
                return null;
            }

        }

        public String getWord() {
            assert word != null;
            return word;
        }

        public int getWordLength() {
            return word.length();
        }

        public int getWordType() {
            assert this.wordType <= 3;
            assert this.wordType >= 0;
            return wordType;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final WordInfo other = (WordInfo) obj;
            if ((this.word == null) ? (other.word != null) : !this.word.equals(other.word)) {
                return false;
            }
            if (this.wordType != other.wordType) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + (this.word != null ? this.word.hashCode() : 0);
            hash = 17 * hash + this.wordType;
            return hash;
        }

        @Override
        public String toString() {
            return "(\"" + this.word + "\" , " + this.wordType + ")";
        }
    }

    public static void main(String[] args) throws Exception {
        HiCharSource charSource = new BESTHiCharSource(
                new File(
                "/media/SHARE/QA_project_resources/InterBEST_Corpus/release1/encyclopedia/encyclopedia_00010.txt"));
        System.out.println(charSource);
    }
}
