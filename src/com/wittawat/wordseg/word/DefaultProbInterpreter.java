package com.wittawat.wordseg.word;

/**
 * The most basic probability interpreter using
 * a predefined table of sequence counts.
 * No interpolation, back-off, or smoothing
 * is done.
 * 
 * @author Wittawat Jitkrittum
 */
public class DefaultProbInterpreter<T> implements ProbEstimator<T> {

    /**Sequence frequency table.*/
    private GramTable<T> gramTable;

    public DefaultProbInterpreter(GramTable<T> gramTable) {
        this.gramTable = gramTable;
    }

    public double prob(Sequence<T> seq) {
        Integer count = gramTable.get(seq);
        if (count == null) {
            return 0.0;
        } else {
            assert count > 0;
            int sum = gramTable.sumUnigramFreq();
            double prob = count / sum;
            return prob;
        }
    }

    public double probGivenLeft(Sequence<T> items, Sequence<T> givenSeq) {
        if (givenSeq == null || givenSeq.length() == 0) {
            return prob(items);
        }
        Sequence<T> all = givenSeq.add(items);
        return getCondProb(givenSeq, all);

    }

    public double probGivenRight(Sequence<T> items, Sequence<T> givenSeq) {

        if (givenSeq == null || givenSeq.length() == 0) {
            return prob(items);
        }

        Sequence<T> all = items.add(givenSeq);
        return getCondProb(givenSeq, all);
    }

    private double getCondProb(Sequence<T> givenSeq, Sequence<T> all) {

        Integer allCount = gramTable.get(all);
        if (allCount == 0) {
            return 0.0;
        } else {
            Integer givenCount = gramTable.get(givenSeq);
            assert givenCount > 0;
            double condProb = (double) allCount / (double) givenCount;
            return condProb;
        }
    }
}
