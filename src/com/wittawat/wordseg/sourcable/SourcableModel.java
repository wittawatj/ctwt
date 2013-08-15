package com.wittawat.wordseg.sourcable;

/**
 *
 * @author Wittawat Jitkrittum
 */
public interface SourcableModel {

    double classifyInstance(Object[] i) throws Exception;
}
