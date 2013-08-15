package com.wittawat.wordseg;

//package com.wittawat.wordseg.dict;
//
//import com.wittawat.wordseg.WordTokenizer;
//import com.wittawat.wordseg.utils.Trie;
//import java.io.File;
//import java.io.IOException;
//import java.util.Iterator;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import org.apache.commons.io.FileUtils;
//
///**
// * A <code>DictTokenizer</code> which uses a set of rules
// * to decide a token when Trie cannot find the next one.
// *
// * - If spaces are next, group them into one token.
// * - If digits (Thai or arabic) are next, group them into one token.
// * - Otherwise, return the next 1 character, or characters until
// * the next token can be found in the Trie (depending on the option).
// * @author Wittawat Jitkrittum
// */
//public abstract class DictRuleTokenizer extends DictTokenizer {
//
//    private static final Pattern SPACES_PATTERN = Pattern.compile("\\s+");
//    private static final Pattern DIGITS_PATTERN = Pattern.compile("\\d+");
//    private static final String THAI_DIGITS_PATTERN_STR = "[\u0E50-\u0E59]+";
//    private static final Pattern THAI_DIGITS_PATTERN = Pattern.compile(THAI_DIGITS_PATTERN_STR);
//    /**The tokenizer returns the next one character as the next token
//    when no match is found.*/
//    public static final int UNMATCHED_MODE_ONE_CHAR = 23;
//    /**The tokenizer returns some characters formed to be the next token
//    when no match is found.*/
//    public static final int UNMATCHED_MODE_UNTIL_NEXT_MATCHED = 834;
//    public static final int MATCHING_MODE_LONGEST = 1209;
//    public static final int MATCHING_MODE_SHORTEST = 5239;
//    private int unmatchedMode = UNMATCHED_MODE_UNTIL_NEXT_MATCHED;
//    private int matchingMode = MATCHING_MODE_LONGEST;
//
//    public DictRuleTokenizer(Iterator<String> words, Trie trie) {
//        super(words, trie);
//    }
//
//    public DictRuleTokenizer(Iterator<String> words) {
//        super(words);
//    }
//
//    protected int unmatched(String content) {
//        String firstChar = content.substring(0, 1);
//        Matcher m = null;
//        if (firstChar.matches("\\s")) {
//            m = SPACES_PATTERN.matcher(content);
//
//        } else if (firstChar.matches("\\d")) {
//            m = DIGITS_PATTERN.matcher(content);
//        } else if (firstChar.matches(THAI_DIGITS_PATTERN_STR)) {
//            m = THAI_DIGITS_PATTERN.matcher(content);
//        } else {
//            if (unmatchedMode == UNMATCHED_MODE_ONE_CHAR) {
//                return 1;
//            } else if (unmatchedMode == UNMATCHED_MODE_UNTIL_NEXT_MATCHED) {
//                int nCharsTillNextMatch = getNumCharsUntilNextMatch(content);
//                return nCharsTillNextMatch;
//            } else {
//                throw new RuntimeException("Unknown unmatchedMode.");
//            }
//        }
//        int numChars = getNumCharsFromUnMatched(m);
//        return numChars;
//
//    }
//
//    private int getNumCharsFromUnMatched(Matcher m) {
//        if (m.find()) {
//            assert m.start() == 0;
//            int numChars = m.end();
//            assert numChars >= 1;
//            return numChars;
//        }
//        assert false;
//        return 0;
//    }
//
//    private int getNumCharsUntilNextMatch(String content) {
//        int numChars = 0;
//        while (this.nextToken(content) == 0) {
//            ++numChars;
//            content = content.substring(1);
//        }
//        return numChars;
//    }
//
//    @Override
//    protected List<Integer> getEndTokenIndexes(String content) {
//    }
//
//    public int getUnmatchedMode() {
//        return unmatchedMode;
//    }
//
//    public void setUnmatchedMode(int unmatchedMode) {
//        if (unmatchedMode != UNMATCHED_MODE_ONE_CHAR && unmatchedMode != UNMATCHED_MODE_UNTIL_NEXT_MATCHED) {
//            throw new IllegalArgumentException("unmatched mode must come from constants defined in " + DictRuleTokenizer.class.getName());
//        }
//        this.unmatchedMode = unmatchedMode;
//    }
//
//    public int getMatchingMode() {
//        return matchingMode;
//    }
//
//    public void setMatchingMode(int matchingMode) {
//        if (matchingMode != MATCHING_MODE_LONGEST && matchingMode != MATCHING_MODE_SHORTEST) {
//            throw new IllegalArgumentException("matchign mode must com from constants defined in " + DictRuleTokenizer.class.getName());
//        }
//        this.matchingMode = matchingMode;
//    }
//
//    /////////////////////////////////////////////////////
//    public static void main(String[] args) throws IOException {
//        File dict = new File("data/BEST_other.txt");
//        File contentFile = new File("/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt");
//
//        Iterator<String> wordList = (Iterator<String>) FileUtils.readLines(dict).iterator();
//        WordTokenizer tokenizer =
//                new LongestMatchTokenizer(wordList) //                new ShortestMatchTokenizer(wordList);
//                ;
//        System.out.println("Finished loading dict ..");
//        String content = FileUtils.readFileToString(contentFile);
//        System.out.println(tokenizer.tokenize(content));
//
//    }
//}
