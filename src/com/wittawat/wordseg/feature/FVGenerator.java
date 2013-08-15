package com.wittawat.wordseg.feature;

import com.wittawat.wordseg.*;
import com.wittawat.wordseg.data.BESTHiCharSource;
import weka.core.Instances;

/**
 * A feature vector generator. Each feature vector represents one character.
 * 
 * @author nook
 */
public interface FVGenerator {

    String DEFAULT_DELIMITER = "|";

    /**@return the header of the instance generated.
    The returned Instances object should not contain any
    instance. The purpose is to use it for meta data.*/
    Instances getDataSetHeader();

    ClassUnit getClassUnit();

    /**@return the index positions in character source 
     * from which instances will be generated.
     */
    int[] getInstanceIndexes();

    /**Set to null to generate at all character indexes.*/
    void setInstanceIndexes(int[] indexes);

    /**Needed to be called before produceUnlabeledDataSet(). */
    void setStringSource(String plainTextSource);

    WordSegDataSet produceUnlabeledDataSet();

    /**Needed to be called before produceLabeledDataSet(BESTHiCharSource textSource)*/
    void setHiCharSource(BESTHiCharSource hiCharSource);

    WordSegDataSet produceLabeledDataSet();

    /**Set the name to be set the the produced dataset.*/
    void setDataSetName(String dataSetName);

    String getDataSetName();

    void close();
}
