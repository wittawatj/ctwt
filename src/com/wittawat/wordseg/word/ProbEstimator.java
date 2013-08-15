package com.wittawat.wordseg.word;

/**
 * A probability predictor of a sequence.
 * Typical parameterized type T would be
 * Character and String.
 *
 * @author Wittawat Jitkrittum
 */
public interface ProbEstimator<T> {

    /**@return the probability of the sequence.*/
    double prob(Sequence<T> seq);

    /**@return the probability of the items given the givenSeq
    is found on the left of the item.*/
    double probGivenLeft(Sequence<T> item, Sequence<T> givenSeq);

    /**@return the probability of the items given that the
    givenSeq is found on the right of the item.*/
    double probGivenRight(Sequence<T> item, Sequence<T> givenSeq);
}
