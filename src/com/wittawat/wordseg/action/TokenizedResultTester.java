package com.wittawat.wordseg.action;

import com.wittawat.wordseg.Actions;
import com.wittawat.wordseg.NukeTokenizer;
import com.wittawat.wordseg.NukeTokenizer3;
import java.io.File;

import java.util.Iterator;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;

import weka.classifiers.Classifier;

/**
 * CL tool to automatically run batch many models
 * to perform word segmentation.
 *
 * @author Wittawat Jitkrittum
 */
public class TokenizedResultTester {

    static String BEST_EVALUATION_TOOL_URL = "http://www.hlt.nectec.or.th/Evaluation/ResultEval";

//    public static String upload(String tokenized) throws UnsupportedEncodingException, IOException {
//        HttpClient httpclient = new DefaultHttpClient();
//
//        HttpPost httppost = new HttpPost(BEST_EVALUATION_TOOL_URL);
//        httppost.setHeader("Referer", "http://www.hlt.nectec.or.th/Evaluation/");
//        StringBody tokContent = new StringBody(tokenized);
//
//        File tempFile = File.createTempFile("tempTok", ".txt");
//        FileBody tokFile = new FileBody(tempFile, "text/plain");
//        FileUtils.writeStringToFile(tempFile, tokenized);
//        MultipartEntity reqEntity = new MultipartEntity();
//
////        reqEntity.addPart("save", new StringBody("Submit"));
//        reqEntity.addPart("inputfile", tokContent);
//
//
////        reqEntity.addPart("inputfile", tokContent);
//
//
//        System.out.println("executing request " + httppost.getRequestLine());
//        httppost.setEntity(reqEntity);
//
//        HttpResponse response = httpclient.execute(httppost);
//        HttpEntity resEntity = response.getEntity();
//
//        System.out.println("----------------------------------------");
//        StatusLine statusLine = response.getStatusLine();
//
//        System.out.println(statusLine);
//        if (resEntity != null) {
//            System.out.println("Response content length: " + resEntity.getContentLength());
//            System.out.println("Chunked?: " + resEntity.isChunked());
//        }
//
//
//        /// Get content
//        InputStream is = resEntity.getContent();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder buf = new StringBuilder();
//        String line = null;
//        while ((line = reader.readLine()) != null) {
//            buf.append(line).append("\n");
//        }
////        if (resEntity != null) {
////            resEntity.consumeContent();
////        }
//        return buf.toString();
//    }

    public static void main(String[] args) throws Exception {

        args = new String[]{"/media/SHARE/QA_project_resources/Best_Corpus/test/TEST_100K.txt",
        "fu12_test/30000_results",
        "/home/nook/NetBeansProjects/StackedGen/model_fu12/expr2_30000_models"};

        if (args.length != 3) {
            System.out.println("USAGE: testFile resultFolder modelFolder");
            System.exit(1);
        }
        String testPath = args[0];
        String destFolder = args[1];
        String modelFolder = args[2];

        final String testContent = FileUtils.readFileToString(new File(testPath));
        Iterator<File> modelFiles = FileUtils.iterateFiles(new File(modelFolder), new String[]{"model"}, true);
        while(modelFiles.hasNext()){
            File modelFile = modelFiles.next();
            String modelName = modelFile.getName();
            Classifier model = Actions.loadModel(modelFile);
            System.out.println("Finished loading model: " + modelFile.getAbsolutePath());
            NukeTokenizer tok = new NukeTokenizer3(testContent, model);

            System.out.println("Start tokenizing");
            String tokenized =
                    tok.tokenize();
            tok.close();

            File dest = FileSystemView.getFileSystemView().createFileObject(new File(destFolder), modelName+"_result.txt");
            FileUtils.writeStringToFile(dest, tokenized);
            System.out.println("Finished testing: "+modelName);
        }

        
    }
}
