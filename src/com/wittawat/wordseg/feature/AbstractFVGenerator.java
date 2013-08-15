package com.wittawat.wordseg.feature;

import com.aliasi.dict.Dictionary;
import com.aliasi.lm.CharSeqCounter;
import com.aliasi.lm.TrieCharSeqCounter;
import com.wittawat.wordseg.Data;
import com.wittawat.wordseg.utils.Utils;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import weka.core.FastVector;
import weka.core.Instances;

/**
 *
 * @author Wittawat Jitkrittum
 */
public abstract class AbstractFVGenerator implements FVGenerator {

    protected FeatureUnit[] featureUnits;
    protected ClassUnit classUnit = new ClassFU("class");
    protected int[] instanceIndexes;
    protected String dataSetName;

    /**featureUnits do NOT include the unit to generate class attribute.*/
    public AbstractFVGenerator(FeatureUnit[] featureUnits) {
        this.featureUnits = featureUnits;

    }

    public AbstractFVGenerator() {
        this(getDefaultFeatureUnits());
    }

    public int[] getInstanceIndexes() {
        return instanceIndexes;
    }

    public void setInstanceIndexes(int[] indexes) {
        if (indexes != null && indexes.length == 0) {
            throw new IllegalArgumentException("indexes should contain at least one index. ");
        }
        this.instanceIndexes = indexes;
    }

    /**Set the input character source to each feature unit.*/
    protected void setCharSourceToFeatureUnits(String source) {
        for (FeatureUnit fu : featureUnits) {
            fu.setCharSource(source);
        }
    }

    public void setInstanceIndexes(Set<Integer> indexes) {
        if (indexes != null && indexes.size() == 0) {
            throw new IllegalArgumentException("indexes should contain at least one index. ");
        }
        this.instanceIndexes = Utils.setToArray(indexes);
    }

    public String getDataSetName() {
        return dataSetName == null ? "unnamed_dataset_" + new Date().toString() : dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    protected FastVector getAttributes() {
        FastVector attributes = new FastVector();
        for (FeatureUnit fu : featureUnits) {
            attributes.addElement(fu.getAttribute());
        }

        // Class attribute
        attributes.addElement(classUnit.getClassAttribute());
        return attributes;
    }

    public Instances getDataSetHeader() {
        Instances header = new Instances(
                getClass().getName() + " generated_dataset_header",
                getAttributes(),
                0);
        return header;
    }

    public FeatureUnit[] getFeatureUnits() {
        return featureUnits;
    }

    public void setFeatureUnits(FeatureUnit[] featureUnits) {
        if (featureUnits == null) {
            throw new IllegalArgumentException("featureUnits must not be null.");
        }
        this.featureUnits = featureUnits;
    }

    public ClassUnit getClassUnit() {
        return classUnit;
    }

    public void setClassUnit(ClassUnit classUnit) {
        this.classUnit = classUnit;
    }

    public static FVGenerator getDefaultFVGenerator() {
        return new GuidedFVGenerator();
    }
    /////////////////

    /**This is the same as the first version of default 31 feature units.
    THe only exception is that all <code>CharTypeFUs</code> are
     * replaced with <code>CharTypeFConExFU</code>*/
    public static FeatureUnit[] getDefaultFeatureUnits2() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary();
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFConExFU("typefcon_c_i-6", -6),
                        new CharTypeFConExFU("typefcon_c_i-5", -5),
                        new CharTypeFConExFU("typefcon_c_i-4", -4),
                        new CharTypeFConExFU("typefcon_c_i-3", -3),
                        new CharTypeFConExFU("typefcon_c_i-2", -2),
                        new CharTypeFConExFU("typefcon_c_i-1", -1),
                        new CharTypeFConExFU("typefcon_c_i"),
                        new CharTypeFConExFU("typefcon_c_i+1", 1),
                        new CharTypeFConExFU("typefcon_c_i+2", 2), //
                        new CharTypeFConExFU("typefcon_c_i+3", 3), //
                        new CharTypeFConExFU("typefcon_c_i+4", 4), //
                        new CharTypeFConExFU("typefcon_c_i+5", 5), //
                        new CharTypeFConExFU("typefcon_c_i+6", 6), //
                        new NextSpaceFU("nextSpace"),//
                        new PreviousSpaceFU("prevSpace"),//
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharSuffixFU("suffix_i-1_i", 2),
                        new CharPrefixFU("prefix_i+1_i+2", 2), //
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //
                        new NextWordPosFU("nextPTitle", personTitles),
                        new PreviousWordPosFU("prevPTitle", personTitles),
                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new LeftCondProbFU("lProb_i-1_i", charTrie, 2, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+2", charTrie, 2, '|'),};
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    /**This is similar to the first version of default 31 feature units.
    THe only exception is that all <code>CharTypeFUs</code> are
     * replaced with <code>CharTypeFConMatraFU</code>. Also, <code>CharTypeFConMatraFU</code>
    is increased to 15 instead of 13.*/
    public static FeatureUnit[] getDefaultFeatureUnits3() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary();
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFConMatraFU("matra_c_i-7", -7),
                        new CharTypeFConMatraFU("matra_c_i-6", -6),
                        new CharTypeFConMatraFU("matra_c_i-5", -5),
                        new CharTypeFConMatraFU("matra_c_i-4", -4),
                        new CharTypeFConMatraFU("matra_c_i-3", -3),
                        new CharTypeFConMatraFU("matra_c_i-2", -2),
                        new CharTypeFConMatraFU("matra_c_i-1", -1),
                        new CharTypeFConMatraFU("matra_c_i"),
                        new CharTypeFConMatraFU("matra_c_i+1", 1),
                        new CharTypeFConMatraFU("matra_c_i+2", 2), //
                        new CharTypeFConMatraFU("matra_c_i+3", 3), //
                        new CharTypeFConMatraFU("matra_c_i+4", 4), //
                        new CharTypeFConMatraFU("matra_c_i+5", 5), //
                        new CharTypeFConMatraFU("matra_c_i+6", 6), //
                        new CharTypeFConMatraFU("matra_c_i+7", 7), //
                        new NextSpaceFU("nextSpace"),//
                        new PreviousSpaceFU("prevSpace"),//
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharSuffixFU("suffix_i-1_i", 2),
                        new CharPrefixFU("prefix_i+1_i+2", 2), //
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //
                        new NextWordPosFU("nextPTitle", personTitles),
                        new PreviousWordPosFU("prevPTitle", personTitles),
                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new LeftCondProbFU("lProb_i-1_i", charTrie, 2, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+2", charTrie, 2, '|'),};
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    /**This is similar to FeatureUnits1 but features suffix, prefix, leftConfProb, and
    right condProb which depend on only 2 characters are all removed. Also, <code>SeparateRatioFU</code>
    is added.*/
    public static FeatureUnit[] getDefaultFeatureUnits4() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary(); //thread-safe ?
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFU("type_c_i-6", -6),
                        new CharTypeFU("type_c_i-5", -5),
                        new CharTypeFU("type_c_i-4", -4),
                        new CharTypeFU("type_c_i-3", -3),
                        new CharTypeFU("type_c_i-2", -2),
                        new CharTypeFU("type_c_i-1", -1),
                        new CharTypeFU("type_c_i"),
                        new CharTypeFU("type_c_i+1", 1),
                        new CharTypeFU("type_c_i+2", 2), //
                        new CharTypeFU("type_c_i+3", 3), //
                        new CharTypeFU("type_c_i+4", 4), //
                        new CharTypeFU("type_c_i+5", 5), //
                        new CharTypeFU("type_c_i+6", 6), //

                        new NextSpaceFU("nextSpace"),//
                        new PreviousSpaceFU("prevSpace"),//

                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //

                        new NextWordPosFU("nextPTitle", personTitles),
                        new PreviousWordPosFU("prevPTitle", personTitles),
                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie)
                    };
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    /**Like FeatureUnits4 but add the features suffix, prefix, leftConfProb, and
    right condProb which depend on 2 characters back again. Equivalently, FeatureUnits1 + SepRatio*/
    public static FeatureUnit[] getDefaultFeatureUnits5() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary(); //thread-safe ?
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFU("type_c_i-6", -6),
                        new CharTypeFU("type_c_i-5", -5),
                        new CharTypeFU("type_c_i-4", -4),
                        new CharTypeFU("type_c_i-3", -3),
                        new CharTypeFU("type_c_i-2", -2),
                        new CharTypeFU("type_c_i-1", -1),
                        new CharTypeFU("type_c_i"),
                        new CharTypeFU("type_c_i+1", 1),
                        new CharTypeFU("type_c_i+2", 2), //
                        new CharTypeFU("type_c_i+3", 3), //
                        new CharTypeFU("type_c_i+4", 4), //
                        new CharTypeFU("type_c_i+5", 5), //
                        new CharTypeFU("type_c_i+6", 6), //
                        new NextSpaceFU("nextSpace"),//
                        new PreviousSpaceFU("prevSpace"),//
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharSuffixFU("suffix_i-1_i", 2),
                        new CharPrefixFU("prefix_i+1_i+2", 2), //
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //
                        new NextWordPosFU("nextPTitle", personTitles),
                        new PreviousWordPosFU("prevPTitle", personTitles),
                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new LeftCondProbFU("lProb_i-1_i", charTrie, 2, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+2", charTrie, 2, '|'),
                        new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie)
                    };
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    /**Minimal set of only 8 attributes. From feature selection using Wrapper on
    REPTree on Novel_30000 dataset (forward search).*/
    public static FeatureUnit[] getDefaultFeatureUnits6() {

        try {

            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFU("type_c_i-1", -1),
                        new CharTypeFU("type_c_i"),
                        new CharTypeFU("type_c_i+1", 1),
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie)
                    };
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**From feature selection using Wrapper on
    REPTree on Novel_30000 dataset (backward search). 23 attributes.*/
    public static FeatureUnit[] getDefaultFeatureUnits7() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary(); //thread-safe ?
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFU("type_c_i-6", -6),
                        new CharTypeFU("type_c_i-5", -5),
                        new CharTypeFU("type_c_i-3", -3),
                        new CharTypeFU("type_c_i-2", -2),
                        new CharTypeFU("type_c_i-1", -1),
                        new CharTypeFU("type_c_i"),
                        new CharTypeFU("type_c_i+1", 1),
                        new CharTypeFU("type_c_i+3", 3), //
                        new CharTypeFU("type_c_i+4", 4), //

                        new CharTypeFU("type_c_i+6", 6), //

                        new PreviousSpaceFU("prevSpace"),//
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //
                        new NextWordPosFU("nextPTitle", personTitles),
                        new PreviousWordPosFU("prevPTitle", personTitles),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie)
                    };
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    public static FeatureUnit[] getDefaultFeatureUnits1() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary(); //thread-safe ?
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFU("type_c_i-6", -6),
                        new CharTypeFU("type_c_i-5", -5),
                        new CharTypeFU("type_c_i-4", -4),
                        new CharTypeFU("type_c_i-3", -3),
                        new CharTypeFU("type_c_i-2", -2),
                        new CharTypeFU("type_c_i-1", -1),
                        new CharTypeFU("type_c_i"),
                        new CharTypeFU("type_c_i+1", 1),
                        new CharTypeFU("type_c_i+2", 2), //
                        new CharTypeFU("type_c_i+3", 3), //
                        new CharTypeFU("type_c_i+4", 4), //
                        new CharTypeFU("type_c_i+5", 5), //
                        new CharTypeFU("type_c_i+6", 6), //
                        new NextSpaceFU("nextSpace"),//
                        new PreviousSpaceFU("prevSpace"),//
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharSuffixFU("suffix_i-1_i", 2),
                        new CharPrefixFU("prefix_i+1_i+2", 2), //
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //
                        new NextWordPosFU("nextPTitle", personTitles),
                        new PreviousWordPosFU("prevPTitle", personTitles),
                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new LeftCondProbFU("lProb_i-1_i", charTrie, 2, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+2", charTrie, 2, '|'),};
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    /**Pure CharTypeFU attributes. From -5 to 5 totally 11 attributes.*/
    public static FeatureUnit[] getDefaultFeatureUnits8() {

        return new FeatureUnit[]{
                    new CharTypeFU("type_c_i-5", -5),
                    new CharTypeFU("type_c_i-4", -4),
                    new CharTypeFU("type_c_i-3", -3),
                    new CharTypeFU("type_c_i-2", -2),
                    new CharTypeFU("type_c_i-1", -1),
                    new CharTypeFU("type_c_i"),
                    new CharTypeFU("type_c_i+1", 1),
                    new CharTypeFU("type_c_i+2", 2), //
                    new CharTypeFU("type_c_i+3", 3), //
                    new CharTypeFU("type_c_i+4", 4), //
                    new CharTypeFU("type_c_i+5", 5), //
                };


    }

    /**From feature selection, Wrapper on J48, Backward best search, full dataset, no CV.
    Total 20 attributes.*/
    public static FeatureUnit[] getDefaultFeatureUnits9() {
        try {
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFU("type_c_i-4", -4),
                        new CharTypeFU("type_c_i-3", -3),
                        new CharTypeFU("type_c_i-2", -2),
                        new CharTypeFU("type_c_i"),
                        new CharTypeFU("type_c_i+1", 1),
                        new CharTypeFU("type_c_i+2", 2), //
                        new CharTypeFU("type_c_i+3", 3), //

                        new CharTypeFU("type_c_i+6", 6), //
                        new NextSpaceFU("nextSpace"),//

                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-1_i", 2),
                        new CharPrefixFU("prefix_i+1_i+2", 2), //
                        new CharPrefixFU("prefix_i+1_i+3", 3), //

                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new LeftCondProbFU("lProb_i-1_i", charTrie, 2, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+2", charTrie, 2, '|'),};
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**Like fu5 but significantly increase the combinations of start, end indexes
    for lcprob and rcprob. Also, use <code>ValueRelationFU</code> on all such
    combinations.*/
    public static FeatureUnit[] getDefaultFeatureUnits10() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary(); //thread-safe ?
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            FeatureUnit[] baseFus = new FeatureUnit[]{
                new CharTypeFU("type_c_i-6", -6),
                new CharTypeFU("type_c_i-5", -5),
                new CharTypeFU("type_c_i-4", -4),
                new CharTypeFU("type_c_i-3", -3),
                new CharTypeFU("type_c_i-2", -2),
                new CharTypeFU("type_c_i-1", -1),
                new CharTypeFU("type_c_i"),
                new CharTypeFU("type_c_i+1", 1),
                new CharTypeFU("type_c_i+2", 2), //
                new CharTypeFU("type_c_i+3", 3), //
                new CharTypeFU("type_c_i+4", 4), //
                new CharTypeFU("type_c_i+5", 5), //
                new CharTypeFU("type_c_i+6", 6), //
                new NextSpaceFU("nextSpace"),//
                new PreviousSpaceFU("prevSpace"),//
                new CharSuffixFU("suffix_i-3_i", 4),
                new CharSuffixFU("suffix_i-2_i", 3),
                new CharSuffixFU("suffix_i-1_i", 2),
                new CharPrefixFU("prefix_i+1_i+2", 2), //
                new CharPrefixFU("prefix_i+1_i+3", 3), //
                new CharPrefixFU("prefix_i+1_i+4", 4), //
                new NextWordPosFU("nextPTitle", personTitles),
                new PreviousWordPosFU("prevPTitle", personTitles),
                new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie),};
            final int condProbFrom = -4;
            final int condProbTo = 4;
            final int windowLength = condProbTo - condProbFrom + 1;
            final int perOneCharDepends = 8;
            final int condProbAttrs = windowLength * perOneCharDepends;
            final int valueRelAttrs = condProbAttrs / 2;

            FeatureUnit[] fus = new FeatureUnit[baseFus.length + condProbAttrs + valueRelAttrs];
            for (int i = 0; i < baseFus.length; ++i) {
                fus[i] = baseFus[i];
            }
            int fusIndex = baseFus.length;
            FeatureUnit[] valueRelFus = new FeatureUnit[valueRelAttrs];
            int valueRelFusIndex = 0;
            for (int relCur = condProbFrom; relCur <= condProbTo; ++relCur) {
                FeatureUnit[] condFus = {
                    new LeftCondProbFU(charTrie, -4 + relCur, 0 + relCur), // 0
                    new LeftCondProbFU(charTrie, -3 + relCur, 0 + relCur), // 1
                    new LeftCondProbFU(charTrie, -2 + relCur, 0 + relCur), // 2
                    new LeftCondProbFU(charTrie, -1 + relCur, 0 + relCur), // 3
                    new RightCondProbFU(charTrie, 1 + relCur, 5 + relCur), // 4
                    new RightCondProbFU(charTrie, 1 + relCur, 4 + relCur), // 5
                    new RightCondProbFU(charTrie, 1 + relCur, 3 + relCur), // 6
                    new RightCondProbFU(charTrie, 1 + relCur, 2 + relCur) // 7
                };
                for (FeatureUnit condFu : condFus) {
                    fus[fusIndex] = condFu;
                    ++fusIndex;
                }

                for (int i = 0; i < 4; ++i) {
                    ValueRelationFU vr = new ValueRelationFU(condFus[i], condFus[i + 4]);
                    valueRelFus[valueRelFusIndex] = vr;
                    ++valueRelFusIndex;
                }

            }
            assert fusIndex == baseFus.length + condProbAttrs;
            for (int i = 0; i < valueRelFus.length; ++i) {
                fus[fusIndex + i] = valueRelFus[i];
            }

            return fus;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    public static FeatureUnit[] getDefaultFeatureUnits11() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary();
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();
            Dictionary<String> neWords = Data.getNEDictionary();

            FeatureUnit[] baseFus = new FeatureUnit[]{
                new CharTypeFU("type_c_i-6", -6),
                new CharTypeFU("type_c_i-5", -5),
                new CharTypeFU("type_c_i-4", -4),
                new CharTypeFU("type_c_i-3", -3),
                new CharTypeFU("type_c_i-2", -2),
                new CharTypeFU("type_c_i-1", -1),
                new CharTypeFU("type_c_i"),
                new CharTypeFU("type_c_i+1", 1),
                new CharTypeFU("type_c_i+2", 2), //
                new CharTypeFU("type_c_i+3", 3), //
                new CharTypeFU("type_c_i+4", 4), //
                new CharTypeFU("type_c_i+5", 5), //
                new CharTypeFU("type_c_i+6", 6), //
                new NextSpaceFU("nextSpace"),//
                new PreviousSpaceFU("prevSpace"),//
                new CharSuffixFU("suffix_i-3_i", 4),
                new CharSuffixFU("suffix_i-2_i", 3),
                new CharSuffixFU("suffix_i-1_i", 2),
                new CharPrefixFU("prefix_i+1_i+2", 2), //
                new CharPrefixFU("prefix_i+1_i+3", 3), //
                new CharPrefixFU("prefix_i+1_i+4", 4), //
                new NextStartBoundFU("nextPTitle", personTitles),
                new PrevEndBoundFU("prevPTitle", personTitles),
                new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie),
                new NextStartBoundFU("nextNE", neWords),
                new PrevEndBoundFU("prevNE", neWords)
            };
            final int condProbFrom = -3;
            final int condProbTo = 3;
            final int windowLength = condProbTo - condProbFrom + 1;
            final int perOneCharDepends = 4;
            final int condProbAttrs = windowLength * perOneCharDepends;
            final int valueRelAttrs = condProbAttrs / 2;

            FeatureUnit[] fus = new FeatureUnit[baseFus.length + condProbAttrs + valueRelAttrs];
            for (int i = 0; i < baseFus.length; ++i) {
                fus[i] = baseFus[i];
            }
            int fusIndex = baseFus.length;
            FeatureUnit[] valueRelFus = new FeatureUnit[valueRelAttrs];
            int valueRelFusIndex = 0;
            for (int relCur = condProbFrom; relCur <= condProbTo; ++relCur) {
                FeatureUnit[] condFus = {
                    new LeftCondProbFU(charTrie, -4 + relCur, 0 + relCur),
                    new LeftCondProbFU(charTrie, -2 + relCur, 0 + relCur),
                    new RightCondProbFU(charTrie, 1 + relCur, 5 + relCur),
                    new RightCondProbFU(charTrie, 1 + relCur, 3 + relCur),};
                for (FeatureUnit condFu : condFus) {
                    fus[fusIndex] = condFu;
                    ++fusIndex;
                }

                final int half = condFus.length / 2;
                for (int i = 0; i < half; ++i) {
                    ValueDiffFU vr = new ValueDiffFU(condFus[i], condFus[i + half]);
                    valueRelFus[valueRelFusIndex] = vr;
                    ++valueRelFusIndex;
                }

            }
            assert fusIndex == baseFus.length + condProbAttrs;
            for (int i = 0; i < valueRelFus.length; ++i) {
                fus[fusIndex + i] = valueRelFus[i];
            }

            return fus;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /** The balance between information and speed. Last one before the BEST2010 event.*/
    public static FeatureUnit[] getDefaultFeatureUnits12() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary();
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();
            Dictionary<String> neWords = Data.getNEDictionary();

            FeatureUnit lMinus6 = new LeftCondProbFU("lProb_i-5_i", charTrie, 6, '|');
            FeatureUnit lMinus5 = new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|');
            FeatureUnit lMinus4 = new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|');
            FeatureUnit lMinus3 = new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|');

            FeatureUnit rPlus6 = new RightCondProbFU("rProb_i+1_i+6", charTrie, 6, '|');
            FeatureUnit rPlus5 = new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|');
            FeatureUnit rPlus4 = new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|');
            FeatureUnit rPlus3 = new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|');

            FeatureUnit diff6 = new ValueDiffFU(lMinus6, rPlus6);
            FeatureUnit diff5 = new ValueDiffFU(lMinus5, rPlus5);
            FeatureUnit diff4 = new ValueDiffFU(lMinus4, rPlus4);
            FeatureUnit diff3 = new ValueDiffFU(lMinus3, rPlus3);


            FeatureUnit[] fus = new FeatureUnit[]{
                new CharTypeFU("type_c_i-5", -5),
                new CharTypeFU("type_c_i-4", -4),
                new CharTypeFU("type_c_i-3", -3),
                new CharTypeFU("type_c_i-2", -2),
                new CharTypeFU("type_c_i-1", -1),
                new CharTypeFU("type_c_i"),
                new CharTypeFU("type_c_i+1", 1),
                new CharTypeFU("type_c_i+2", 2), //
                new CharTypeFU("type_c_i+3", 3), //
                new CharTypeFU("type_c_i+4", 4), //
                new CharTypeFU("type_c_i+5", 5), //

                new NextSpaceFU("nextSpace"),//
                new PreviousSpaceFU("prevSpace"),//

                new CharSuffixFU("suffix_i-3_i", 4),
                new CharSuffixFU("suffix_i-2_i", 3),
                new CharPrefixFU("prefix_i+1_i+3", 3), //
                new CharPrefixFU("prefix_i+1_i+4", 4), //

                new NextStartBoundFU("nextPTitle", personTitles),
                new PrevEndBoundFU("prevPTitle", personTitles),
                new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie),
                new SeparateRatioFU("sepRatio_i-2_i+3", 2, 3, charTrie),
                new NextStartBoundFU("nextNE", neWords),
                new PrevEndBoundFU("prevNE", neWords),
                lMinus6,
                lMinus5,
                lMinus4,
                lMinus3,
                rPlus6,
                rPlus5,
                rPlus4,
                rPlus3,
                diff6,
                diff5,
                diff4,
                diff3
            };

            return fus;
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**The same as featureUnits5 but add left and right entropies.*/
    public static FeatureUnit[] getDefaultFeatureUnits13() {
        try {
            Dictionary<String> personTitles = Data.getPersonTitleDictionary();
            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new CharTypeFU("type_c_i-6", -6),
                        new CharTypeFU("type_c_i-5", -5),
                        new CharTypeFU("type_c_i-4", -4),
                        new CharTypeFU("type_c_i-3", -3),
                        new CharTypeFU("type_c_i-2", -2),
                        new CharTypeFU("type_c_i-1", -1),
                        new CharTypeFU("type_c_i"),
                        new CharTypeFU("type_c_i+1", 1),
                        new CharTypeFU("type_c_i+2", 2), //
                        new CharTypeFU("type_c_i+3", 3), //
                        new CharTypeFU("type_c_i+4", 4), //
                        new CharTypeFU("type_c_i+5", 5), //
                        new CharTypeFU("type_c_i+6", 6), //
                        new NextSpaceFU("nextSpace"),//
                        new PreviousSpaceFU("prevSpace"),//
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharSuffixFU("suffix_i-1_i", 2),
                        new CharPrefixFU("prefix_i+1_i+2", 2), //
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //
                        new NextWordPosFU("nextPTitle", personTitles),
                        new PreviousWordPosFU("prevPTitle", personTitles),
                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new LeftCondProbFU("lProb_i-1_i", charTrie, 2, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+2", charTrie, 2, '|'),
                        new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie),
                        new LeftEntropyFU(charTrie, 1, 4),
                        new LeftEntropyFU(charTrie, 2, 5),
                        new RightEntropyFU(charTrie, -4, -1),
                        new RightEntropyFU(charTrie, -3, 0)
                    };
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;


    }

    /**Contains only language independent features. Only probability features, spaces.*/
    public static FeatureUnit[] getDefaultFeatureUnits14() {
        try {

            TrieCharSeqCounter charTrie = Data.getCharGramsTrie();

            return new FeatureUnit[]{
                        new NextSpaceFU("nextSpace"),//
                        new PreviousSpaceFU("prevSpace"),//
                        new CharSuffixFU("suffix_i-3_i", 4),
                        new CharSuffixFU("suffix_i-2_i", 3),
                        new CharSuffixFU("suffix_i-1_i", 2),
                        new CharPrefixFU("prefix_i+1_i+2", 2), //
                        new CharPrefixFU("prefix_i+1_i+3", 3), //
                        new CharPrefixFU("prefix_i+1_i+4", 4), //
                        new LeftCondProbFU("lProb_i-4_i", charTrie, 5, '|'),
                        new LeftCondProbFU("lProb_i-3_i", charTrie, 4, '|'),
                        new LeftCondProbFU("lProb_i-2_i", charTrie, 3, '|'),
                        new LeftCondProbFU("lProb_i-1_i", charTrie, 2, '|'),
                        new RightCondProbFU("rProb_i+1_i+5", charTrie, 5, '|'),
                        new RightCondProbFU("rProb_i+1_i+4", charTrie, 4, '|'),
                        new RightCondProbFU("rProb_i+1_i+3", charTrie, 3, '|'),
                        new RightCondProbFU("rProb_i+1_i+2", charTrie, 2, '|'),
                        new SeparateRatioFU("sepRatio_i-2_i+2", 2, 2, charTrie),
                        new LeftEntropyFU(charTrie, 1, 4),
                        new LeftEntropyFU(charTrie, 2, 5),
                        new RightEntropyFU(charTrie, -4, -1),
                        new RightEntropyFU(charTrie, -3, 0)
                    };
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    /**Feature set containing only statistics features*/
    public static FeatureUnit[] getStatsFeatureUnits(CharSeqCounter counter,
            char[] consideringChars,
            Map<String, Float> prefixMap,
            Map<String, Float> suffixMap) {

        return new FeatureUnit[]{
                    
                    new CharSuffixFU("suffix_i-2_i", 3, suffixMap),
                    new CharSuffixFU("suffix_i-1_i", 2, suffixMap),
                    
                    new CharPrefixFU("prefix_i+1_i+3", 3, prefixMap), //
                    new CharPrefixFU("prefix_i+1_i+2", 2, prefixMap), //
                    new LeftCondProbFU("lProb_i-3_i", counter, 4, '|'),
                    new LeftCondProbFU("lProb_i-2_i", counter, 3, '|'),
                    new LeftCondProbFU("lProb_i-1_i", counter, 2, '|'),
                    new RightCondProbFU("rProb_i+1_i+4", counter, 4, '|'),
                    new RightCondProbFU("rProb_i+1_i+3", counter, 3, '|'),
                    new RightCondProbFU("rProb_i+1_i+2", counter, 2, '|'),
                    new LeftEntropyFU(counter, 1, 2, consideringChars),
                    new LeftEntropyFU(counter, 1, 3, consideringChars),
                    new LeftEntropyFU(counter, 2, 3, consideringChars),
                    new LeftEntropyFU(counter, 2, 4, consideringChars),
                    new RightEntropyFU(counter, -2, -1, consideringChars),
                    new RightEntropyFU(counter, -3, -1, consideringChars),
                    new RightEntropyFU(counter, -1, 0, consideringChars),
                    new RightEntropyFU(counter, -2, 0, consideringChars)
                };

    }

    public static FeatureUnit[] getDefaultFeatureUnits() {
        return getDefaultFeatureUnits5();

    }
}

