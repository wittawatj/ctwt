package com.wittawat.wordseg;

import java.io.Console;
import java.io.File;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import weka.classifiers.Classifier;

/**
 * The main class to be used by users.
 * 
 * 
 * @author Wittawat Jitkrittum
 */
public class Main {

    private static String getAgreement() {
        return //                " \n-- Thai Word Tokenizer Based on Stacked Generalization Technique -- \n\n" +
                " \n-- Classification-based Thai Word Tokenizer -- \n\n" +
                "=== Agreement ===\n" +
                "This software is a work developed by Wittawat Jitkrittum and Thitima Nuchpithak from " +
                "Sirindhorn International Institute of Technology under the project named \"Classification-based" +
                " Thai Word Tokenizer\", which has been supported " +
                "by the National Electronics and Computer Technology Center (NECTEC) and Software " +
                "Industry Promotion Agency (SIPA), in order to encourage pupils and students to " +
                "learn and practice their skills in developing software. Therefore, the " +
                "intellectual property of this software shall belong to the developer " +
                "and the developer gives NECTEC and SIPA a permission to distribute " +
                "this software as an \"as is ”\" and non-modified software for a temporary " +
                "and non-exclusive use without remuneration to anyone for his or her own " +
                "purpose or academic purpose, which are not commercial purposes. In this " +
                "connection, NECTEC and SIPA shall not be responsible to the user for taking " +
                "care, maintaining, training or developing the efficiency of this software. " +
                "Moreover, NECTEC and SIPA shall not be liable for any error, software efficiency " +
                "and damages in connection with or arising out of the use of the software.\"\n\n\n";
    }

    private static String getAbout() {
        return "=== About ===\n" +
                "Developers: Wittawat Jitkrittum (wittawatj@gmail.com)\n" +
                "            Thitima Nuchpithak (rhinore@gmail.com)\n" +
                "            SIIT, TU\n" +
                "\n" +
                "- We thank Mr. Nattapong Tongtep from KINDML Lab, SIIT for kindly providing us " +
                "a list of Thai person titles.\n" +
                "- We thank Dr. Thanaruk Theeramunkong for his kind advice on the techniques used.\n" +
                "- We thank LSR Lab, NECTEC for allowing us to use their computational resources.\n" +
                "\n" +
                "There is still a long way for this program to be a perfect tokenizer. Please do not " +
                "hesitate to contact us if you have any question/comment/advice. Thank you.\n";
    }

    private static String getHelp() {
        return ">> (h|help) -- output this help.\n" +
                ">> <input>:tok -- tokenize the input. \n" +
                ">> <input>:tok:<out_file> -- tokenize the input, out to the out_file.\n" +
                ">> <input_file>:tokfile -- tokenize the content in the file denoted by input_file. Output to the console.\n" +
                ">> <input_file>:tokfile:<out_file> -- tokenize the content in the file denoted by input_file. Output to the outfile.\n" +
                ">> agreement -- view license agreement\n" +
                ">> about -- display general information of this software\n" +
                ">> set dict (true|false) -- use/not use dictionary in the first step to help the tokenizer\n" +
                ">> (q|quit|exit) -- exit the program\n";
    }

    public static void main(String[] args) throws Exception {
        Console con = System.console();
        if (con == null) {
            System.out.println("The system must support console to run the program.");
            System.exit(1);
        }
        // Load model
        System.out.println("Loading model ...");
        Classifier model = Data.getDefaultModel();


        System.out.println("Finished loading model.");
        System.out.println(getAgreement());

        boolean isUseDict = true;

        // Dummy statement to eliminate all lazy loading
        System.out.println("\n" + new NukeTokenizer3("โปรแกรมตัดคำไทยด้วยเทคนิคการจำแนกประเภท", model, isUseDict).tokenize() + "\n");

        System.out.println(getHelp());

       
        final String SET_DICT_PAT_STR = "\\s*set\\s+dict\\s+(true|false)\\s*";
        final Pattern SET_DICT_PAT = Pattern.compile(SET_DICT_PAT_STR);
        while (true) {
            System.out.print(">> ");
            String line = con.readLine();
            if (line != null && !line.trim().equals("")) {

                line = line.trim();
                try {
                    if (line.equals("h") || line.equals("help")) {
                        System.out.println(getHelp());
                    } else if (line.equals("about")) {
                        System.out.println(getAbout());
                    } else if (line.equals("agreement")) {
                        System.out.println(getAgreement());
                    } else if (SET_DICT_PAT.matcher(line).find()) {
                        Matcher m = SET_DICT_PAT.matcher(line);
                        m.find();
                        String v = m.group(1);
                        isUseDict = v.equals("true");
                        System.out.println("Dictionary will " + (isUseDict ? "" : "not ") + "be used.");
                    } else if (line.matches("q|quit|exit")) {
                        System.out.println("Bye");
                        System.exit(0);
                    } else if (line.contains(":tokfile:")) {
                        String[] splits = line.split(":tokfile:");
                        String in = splits[0];
                        String out = splits[1];
                        String content = FileUtils.readFileToString(new File(in));
                        long start = new Date().getTime();

                        NukeTokenizer tokenizer = new NukeTokenizer3(content, model, isUseDict);

                        String tokenized = tokenizer.tokenize();
                        long end = new Date().getTime();
                        System.out.println("Time to tokenize: " + (end - start) + " ms.");
                        FileUtils.writeStringToFile(new File(out), tokenized);
                    } else if (line.contains(":tokfile")) {
                        String[] splits = line.split(":tokfile");
                        String in = splits[0];

                        String content = FileUtils.readFileToString(new File(in));
                        long start = new Date().getTime();
                        NukeTokenizer tokenizer = new NukeTokenizer3(content, model, isUseDict);
                        String tokenized = tokenizer.tokenize();
                        long end = new Date().getTime();

                        System.out.println(tokenized);
                        System.out.println("Time to tokenize: " + (end - start) + " ms.");
                    } else if (line.contains(":tok:")) {
                        String[] splits = line.split(":tok:");
                        String inText = splits[0];
                        String out = splits[1];

                        long start = new Date().getTime();
                        NukeTokenizer tokenizer = new NukeTokenizer3(inText, model, isUseDict);
                        String tokenized = tokenizer.tokenize();
                        long end = new Date().getTime();
                        System.out.println("Time to tokenize: " + (end - start) + " ms.");
                        FileUtils.writeStringToFile(new File(out), tokenized);
                    } else if (line.contains(":tok")) {
                        String[] splits = line.split(":tok");
                        String inText = splits[0];

                        long start = new Date().getTime();
                        NukeTokenizer tokenizer = new NukeTokenizer3(inText, model, isUseDict);
                        String tokenized = tokenizer.tokenize();
                        long end = new Date().getTime();

                        System.out.println(tokenized);
                        System.out.println("Time to tokenize: " + (end - start) + " ms.");
                    } else {
                        System.out.println("Unknown command");
                    }
                } catch (Exception e) {
                    System.out.println("Error. See the exception.");
                    e.printStackTrace();
                }

            }
        }

    }
}
