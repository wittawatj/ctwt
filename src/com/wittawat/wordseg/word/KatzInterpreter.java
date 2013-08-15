package com.wittawat.wordseg.word;

import java.util.Map;
import org.apache.commons.lang.ArrayUtils;

/**
 * Class to get the most out of N-gram's frequencies.
 * Use Katz backoff WITHOUT normalizing constant.
 * So sum_{w_i} P(w_i | w_{i-2}, w_{i-1}) might not be 1.
 * 
 * @author Wittawat Jitkrittum
 */
public class KatzInterpreter {

    private Map<WordSequence, Integer> nGrams;
    private int unigramFreqSum = -1;
//    private double[][] conditionalWeights = {  //Don't use Jelinek-Mercer smoothing for now
//        {0.7, 0.3}, // a0*P(wi | wi-1) + a1*P(wi)
//        {0.6, 0.3, 0.1}, //a0*P(wi | wi-2, wi-1) + a1*P(wi | wi-1) + a2*P(wi)
//        {0.5, 0.3, 0.15, 0.05} //a0*P(wi | wi-3, wi-2, wi-1) + a1*P(wi | wi-1, wi-2) + a2*P(wi | wi-1) + a3*P(wi)
//    };

    public KatzInterpreter(Map<WordSequence, Integer> nGrams) {
        
        this.nGrams = nGrams;
    }

    /**@param n gram size to sum*/
    private int sumAllGramFreqs(int n) {
        int sum = 0;
        for (Map.Entry<WordSequence, Integer> e : nGrams.entrySet()) {
            if (e.getKey().length() == n) {
                int freq = e.getValue();
                assert freq > 0;
                sum += freq;
            }
        }
        return sum;
    }

    private int getUnigramFreqSum() {
        if (unigramFreqSum < 0) {
            unigramFreqSum = sumAllGramFreqs(1);
        }
        return unigramFreqSum;
    }

    public int getCount(String... grams) {
        WordSequence sequence = new WordSequence(grams);
        Integer count = nGrams.get(sequence);
        return count == null ? 0 : count;
    }

    public double probGivenLeft(String wi, String... given) {
        String[] all = (String[]) ArrayUtils.add(given, wi);
        int allCount = getCount(all);
        if (allCount == 0) {
            if (given.length > 1) {
                String[] leftmostRemoved = (String[]) ArrayUtils.remove(given, 0);
                assert leftmostRemoved.length == given.length - 1;
                // Recursion
                return probGivenLeft(wi, leftmostRemoved);
            } else {
                assert all.length == 2;
                return 0;
            }
        }

        int givenCount = getCount(given);
        assert givenCount >= allCount;
        double prob = (double) allCount / (double) givenCount;
        assert prob >= 0 && prob <= 1;
        return prob;
    }

    /**@return P(wi | wi-3, wi-2, wi-1) */
//    public double probGivenLeft(String wI, String wIMinus3, String wIMinus2, String wIMinus1) {
//        return probGivenLeft(wI, new String[]{wIMinus3, wIMinus2, wIMinus1});
//
//    }
//
//    public double probGivenLeft(String wI, String wIMinus2, String wIMinus1) {
//        return probGivenLeft(wI, new String[]{wIMinus2, wIMinus1});
//    }
//
//    public double probGivenLeft(String wI, String wIMinus1) {
//        return probGivenLeft(wI, new String[]{wIMinus1});
//    }
    public double prob(String w) {
        int countW = getCount(w);
        if (countW == 0) {
            return 0;
        }

        int countAllUnigrams = getUnigramFreqSum();
        assert countW <= countAllUnigrams;
        double prob = (double) countW / (double) countAllUnigrams;
        assert prob >= 0;
        assert prob <= 1;
        return prob;
    }

    /**@return the conditional probability given both sides. Specifically,
    return 0.33* P(wI | wiMinus) + 0.33*P(wI) + 0.33*P(wI | wIPlus)*/
    public double probGiven(String[] wIMinus, String wI, String[] wIPlus) {
        double oneThird = 1.0 / 3.0;
        double prob = oneThird * prob(wI) + oneThird * probGivenLeft(wI, wIMinus) + oneThird * probGivenRight(wI, wIPlus);
        assert prob >= 0;
        assert prob <= 1;
        return prob;

    }
//    public double probGivenRight(String wI, String wIPlus1, String wIPlus2, String wIPlus3) {
//        return probGivenRight(wI, new String[]{wIPlus1, wIPlus2, wIPlus3});
//    }
//
//    public double probGivenRight(String wI, String wIPlus1, String wIPlus2) {
//        return probGivenRight(wI, new String[]{wIPlus1, wIPlus2});
//    }
//
//    public double probGivenRight(String wI, String wIPlus1) {
//        return probGivenRight(wI, new String[]{wIPlus1});
//    }

    public double probGivenRight(String wi, String... given) {
        String[] all = (String[]) ArrayUtils.addAll(new String[]{wi}, given);
        int allCount = getCount(all);
        if (allCount == 0) {
            if (given.length > 1) {
                String[] rightmostRemoved = (String[]) ArrayUtils.remove(given, given.length - 1);
                assert rightmostRemoved.length == given.length - 1;
                // Recursion
                return probGivenRight(wi, rightmostRemoved);
            } else {
                assert all.length == 2;
                return 0;
            }

        }
        int givenCount = getCount(given);
        assert givenCount >= allCount;
        double prob = (double) allCount / (double) givenCount;
        assert prob >= 0 && prob <= 1;
        return prob;
    }
}
