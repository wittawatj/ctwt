package com.wittawat.wordseg.sourcable;

import com.wittawat.wordseg.Actions;
import com.wittawat.wordseg.feature.FeatureUnit;
import java.io.File;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;
import weka.classifiers.Sourcable;
import weka.core.Attribute;
import weka.core.Instance;

/**
 *
 * @author Wittawat Jitkrittum
 */
public class SourcableHelper {

    static Pattern classifyPat = Pattern.compile("public\\s{1,10}static\\s{1,10}double\\s{1,10}classify");
    public static File DEFAULT_SOURCABLE_MODEL_FOLDER = new File("/home/nook/NetBeansProjects/StackedGen/src/com/wittawat/wordseg/sourcable");

    public static String implementsSourcableModel(String code, String className) {
        return implementsSourcableModel("package com.wittawat.wordseg.sourcable;\n", code, className);

    }

    /**Modify the code so that it implments <code>SourcableModel</code>.
    @param header the package and import information.
     */
    public static String implementsSourcableModel(String header, String code, String className) {
        code = " public " + code;
        Pattern classPat = Pattern.compile("class\\s+" + Pattern.quote(className));
        // Find class definition
        Matcher m = classPat.matcher(code);
        if (m.find()) {
            int end = m.end();
            code = code.substring(0, end) + " implements " + SourcableModel.class.getName() + " " + code.substring(end, code.length());
        }

        // Find classify method
        m = classifyPat.matcher(code);
        if (m.find()) {
            code = code.substring(0, m.start()) + "public double classifyInstance(Object[] i) throws Exception{\n" +
                    "return classify(i);\n" +
                    "}\n" + code.substring(m.start(), code.length()) + "\n";
        }

        code = header + "\n\n" + code;
        return code;

    }

    /**
     * This is intended to be used with <code>SourcableModel</code>.
     * @return a raw-valued instance generated from the input instance. */
    public static Object[] convertToRawValues(FeatureUnit[] fus, Instance instance) {
        double[] doubleV = instance.toDoubleArray();
        Object[] newV = new Object[doubleV.length];
        for (int i = 0; i < doubleV.length; ++i) {
            Attribute attrI = instance.attribute(i);
            if (attrI.isNumeric()) {
                newV[i] = doubleV[i];
            } else if (attrI.isNominal()) {
                newV[i] = instance.toString(attrI);
            } else {
                throw new RuntimeException("Unsupported attribute type found: " + attrI.toString());
            }
        }
        return newV;

    }

    public static String modelToSourcableModel(Classifier model, String className) throws Exception {
        if (model instanceof Sourcable) {
            Sourcable sourcable = (Sourcable) model;
            return sourcable.toSource(className);

        }
        return null;
    }

    public static String modelToSourcableModel(File modelFile, String className) throws Exception {

        Classifier model = Actions.loadModel(modelFile);
        return modelToSourcableModel(model, className);

    }

    /**Auto-name the class name to be the name of the model without .model.*/
    public static String modelToSourcableModel(File modelFile) throws Exception {

        Classifier model = Actions.loadModel(modelFile);
        String fileName = modelFile.getName();
        String modelName = fileName.substring(0, fileName.lastIndexOf(".model"));
        return modelToSourcableModel(model, modelName);

    }

    /**Write the source file to the default folder in the project .*/
    public static void modelToSourceFile(File modelFile) throws Exception {
        String fileName = modelFile.getName();
        String modelName = fileName.substring(0, fileName.lastIndexOf(".model"));
        File dest = FileSystemView.getFileSystemView().createFileObject(DEFAULT_SOURCABLE_MODEL_FOLDER, modelName + ".java");
        String code = modelToSourcableModel(modelFile);
        if (code != null) {
            // null when the model is not Sourcable
            code = implementsSourcableModel(code, modelName);
            FileUtils.writeStringToFile(dest, code);
        }
    }
    ///////////////////////////////////////

    public static void main(String[] args) throws Exception {

        File modelFolder = new File("model");
        Iterator<File> modelFiles = FileUtils.iterateFiles(modelFolder, new String[]{"model"}, false);
        while (modelFiles.hasNext()) {
            File f = modelFiles.next();

            modelToSourceFile(f);
        }
//        modelToSourceFile(new File("/home/nook/NetBeansProjects/StackedGen/model/article.model"));

    }
}
