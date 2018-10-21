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

    public static void main(String args[]) throws FileNotFoundException {

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

    /**
     * Combines a sequence of csv files into one big csv file. uses the first line of the first csv as the head
     * The remaining files the first line is ignored.
     *
     * 1.csv, 2.csv, 3.csv, ... n.csv -> outFile
     *
     * @param rootPath - the path to the directory that contains all the csv files
     * @param startNumber - start # of the sequence, usually "1"
     * @param endNumber - end # of sequence
     * @param outFileName - the file you want to name the big file.
     */
    private static void combineCSVFilesWithSequenceName(String rootPath, int startNumber, int endNumber, String outFileName) throws FileNotFoundException {
        Scanner[] combineStreams = new Scanner[endNumber - startNumber];

        int nameIndex = startNumber;
        for(int i = 0; i < combineStreams.length; i++){
            String filename = rootPath + "\\" + nameIndex + ".csv";
            System.out.println("reading :" + filename);
            Scanner stream = new Scanner(new File(filename),ENCODING);

            if(i != 0)//only keep first line for line 1
                stream.nextLine();

            combineStreams[i] = stream;
            nameIndex++;
        }


        //write each stream to the output stream
        PrintWriter pw = new PrintWriter(outFileName);
        for(int i = 0; i < combineStreams.length; i++){
            System.out.println("writing :" + i);
            Scanner stream = combineStreams[i];
            while (stream.hasNextLine() )
                pw.println(stream.nextLine());
            stream.close();
            pw.flush();
        }
        pw.close();
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
