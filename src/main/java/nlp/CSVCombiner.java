package nlp;

import java.io.*;
import java.util.Scanner;

/**
 * Quick and dirty program to combine multiple twitter csv files in ANSI/Cp1252 encoded csv files
 */
public class CSVCombiner {

    private static final String ENCODING = "Cp1252";

    private static boolean[] removeLineOne;
    private static String[] appendFileFirstLines;
    private static String[] appendFileNames;
    private static Scanner[] appendFileStreams;

    public static void main(String args[]){
        Scanner kb = new Scanner(System.in);
        System.out.println("Enter file path of start file");
        String f1 = kb.nextLine();
        System.out.println("Enter file path of append files, separated by spaces");
        appendFileNames = kb.nextLine().split(" ");
        removeLineOne = new boolean[appendFileNames.length];
        appendFileStreams = new Scanner[appendFileNames.length];
        appendFileFirstLines = new String[appendFileNames.length];

        for(int i = 0; i < appendFileNames.length; i++)
        {
            try {
                appendFileStreams[i] = new Scanner(new File(appendFileNames[i]),ENCODING);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            appendFileFirstLines[i] = appendFileStreams[i].nextLine();
            System.out.printf("first line of %s : %s\n",appendFileNames[i], appendFileFirstLines[i]);
            System.out.printf("do you want to remove line one from %s ? (y) or (n)\n", appendFileNames[i]);
            removeLineOne[i] = kb.nextLine().toLowerCase().charAt(0) == 'y' ? true : false;

        }

        System.out.println("Enter file path of output file");
        String outFile = kb.nextLine();
        try {
            combineCSVFiles(f1, appendFileNames, appendFileFirstLines, removeLineOne, appendFileStreams, outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void combineCSVFiles(String f1,
                                        String[] appendFileNames,
                                        String[] appendFileFirstLines,
                                        boolean[] removeLineOne,
                                        Scanner[] appendFileStreams,
                                        String outputFile) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter pw = new PrintWriter(outputFile,ENCODING);

        Scanner is1 = new Scanner(new File(f1), ENCODING);


        while (is1.hasNext())
            pw.println(is1.nextLine());
        is1.close();

        //write out each csv file and include line one or not.
        for(int i = 0; i < appendFileStreams.length; i++)
        {
            Scanner stream = appendFileStreams[i];
            if(!(removeLineOne[i]))
                pw.println(appendFileFirstLines[i]);
            while (stream.hasNextLine()){
                pw.println(stream.nextLine());
            }
            pw.flush();
            stream.close();
        }

        pw.close();
    }

}
