package com.wittawat.wordseg.structure;

import com.wittawat.wordseg.feature.FVGenerator;
import java.io.Serializable;
import weka.classifiers.Classifier;

/**
 * A container of a model and its feature set.
 * This determines a unique combination of one experiment
 * setting.
 *
 * @author Wittawat Jitkrittum
 */
public interface ModelSetting extends Serializable {

    Classifier getModel();

    FVGenerator getFVGenerator();
}
