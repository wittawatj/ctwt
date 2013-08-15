package com.wittawat.wordseg.action;

import com.wittawat.wordseg.Actions;

/**
 * CL tool to merge many ARFF files into one.
 * @author Wittawat Jitkrittum
 */
public abstract class MergeArffs {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: program newDataSetName destARFF arff1 arff2 [arff3 [arff4 [...]]]");
            System.exit(1);
        }

        String dataSetName = args[0];
        String dest = args[1];
        int sourceCount = args.length - 2;
        String[] sources = new String[sourceCount];
        for (int i = 2; i < args.length; ++i) {
            sources[i-2] = args[i];
        }

        Actions.mergeAndWrite(dataSetName, dest, sources);
    }
}
