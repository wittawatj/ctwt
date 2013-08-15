package com.wittawat.wordseg.action;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.filechooser.FileSystemView;
import org.apache.commons.io.FileUtils;

/**
 * Tool to process BEST's returned results.
 * 
 * @author Wittawat Jitkrittum
 */
public class ProcessResult {

    static Pattern ONE_RESULT_PAT = Pattern.compile(
            "(.+?)\\Q.\\Emodel_result\\s+" +
            "BEST2009\\s*" +
            "Results:\\s*" +
            "F-measure:\\s*([\\d\\Q.\\E]+)\\s*%\\s*" +
            "Recall:\\s*([\\d\\Q.\\E]+)/([\\d\\Q.\\E]+)\\s*=\\s*([\\d\\Q.\\E]+)\\s*%\\s*" +
            "Precision:\\s*([\\d\\Q.\\E]+)/([\\d\\Q.\\E]+)\\s*=\\s*([\\d\\Q.\\E]+)\\s*%\\s*" +
            "BEST2010\\s*" +
            "Results:\\s*" +
            "F-measure:\\s*([\\d\\Q.\\E]+)\\s*%\\s*" +
            "Recall:\\s*([\\d\\Q.\\E]+)/([\\d\\Q.\\E]+)\\s*=\\s*([\\d\\Q.\\E]+)\\s*%\\s*" +
            "Precision:\\s*([\\d\\Q.\\E]+)/([\\d\\Q.\\E]+)\\s*=\\s*([\\d\\Q.\\E]+)\\s*%\\s*" +
            "[=]+", Pattern.DOTALL);
    public static void main(String[] args) throws Exception{
        File resultFile = new File("/home/nook/NetBeansProjects/StackedGen/fu12_test/30000_results/result_stats.txt");
        File html = FileSystemView.getFileSystemView().createFileObject(resultFile.getParentFile(),resultFile.getName()+".html");
        String content = FileUtils.readFileToString(resultFile);
        
        StringBuilder buf = new StringBuilder();
        buf.append("<table cellspacing=2 cellpadding=3 border=1>");
        buf.append("<tr><td></td><td colspan=3>BEST 2009</td><td colspan=3>BEST 2010</td></tr>\n");
        buf.append("<tr><td>Scheme</td><td>Precision</td><td>Recall</td><td>F-Measure</td>" +
                "<td>Precision</td><td>Recall</td><td>F-Measure</td></tr>\n");
        Matcher m = ONE_RESULT_PAT.matcher(content);
        while(m.find()){
//            System.out.println(m.group());
            String scheme = m.group(1);
            String fMeasure2009 = m.group(2);
            String numCorrect2009 = m.group(3);
            String numAll = m.group(4);
            String recall2009 = m.group(5);
            assert numCorrect2009.equals(m.group(6));
            String numProposed2009 = m.group(7);
            String precision2009 = m.group(8);

            String fMeasure2010 = m.group(9);
            String numCorrect2010 = m.group(10);
            assert numAll.equals(m.group(11));
            String recall2010 = m.group(12);
            assert numCorrect2010.equals(m.group(13));
            String numProposed2010 = m.group(14);
            String precision2010 = m.group(15);

            ///////// format /////
            buf.append("<tr>\n");
             buf.append("<td>");
            buf.append(scheme);
            buf.append("</td>\n");


            buf.append("<td>");
            buf.append(precision2009);
            buf.append("</td>\n");

            buf.append("<td>");
            buf.append(recall2009);
            buf.append("</td>\n");

            buf.append("<td>");
            buf.append(fMeasure2009);
            buf.append("</td>\n");

            buf.append("<td>");
            buf.append(precision2010);
            buf.append("</td>\n");

            buf.append("<td>");
            buf.append(recall2010);
            buf.append("</td>\n");

            buf.append("<td>");
            buf.append(fMeasure2010);
            buf.append("</td>\n");

            buf.append("</tr>\n");


        }
        buf.append("</table>");
        String resultTable = buf.toString();
        System.out.println(resultTable);
        FileUtils.writeStringToFile(html, resultTable);
    }
}
