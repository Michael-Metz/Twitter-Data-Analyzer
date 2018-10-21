package nlp;

import nlp.tweetanalyzers.ApacheNLPTweetAnalyzer;
import nlp.tweetanalyzers.StanfordNLPTweetAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cons {

    private static String OS_SLASH;//unix = "/", windows = "\\"
    private static String APACHE_MODEL_ROOT_DIR;
    private static String STANFORD_MODEL_ROOT_DIR;
    /**
     * This program will go into
     *
     * @param args
     */
    public static void main(String args[]) {

        if (args.length != 4) {
            printUsage();
            System.exit(1);
        }
        File rootDirectory = new File(args[0]);
        OS_SLASH = args[1].equalsIgnoreCase("win") ? "\\" : "/";
        STANFORD_MODEL_ROOT_DIR = args[2];
        APACHE_MODEL_ROOT_DIR = args[3];


        TwitterPreProcessor preProcessor = new TwitterPreProcessor();
        StanfordNLPTweetAnalyzer stanford = StanfordNLPTweetAnalyzer.getInstance(STANFORD_MODEL_ROOT_DIR,true);
        ApacheNLPTweetAnalyzer apache = ApacheNLPTweetAnalyzer.getInstance(APACHE_MODEL_ROOT_DIR);

        File[] twitterExportDirectories = rootDirectory.listFiles();
        for (int i = 0; i < twitterExportDirectories.length; i++) {
            File dir = twitterExportDirectories[i];
            //pick up tweets.csv from dir
            String tweetsFileNamePath = dir.getAbsolutePath() + OS_SLASH + "tweets.csv";
            List<Tweet> tweets = null;
            try {
                tweets = IOTweetHelper.readTweetsFromCsvFile(tweetsFileNamePath);
                System.out.println("processing: " + tweetsFileNamePath);
            } catch (IOException e) {
                System.out.println("unable to read: " + tweetsFileNamePath);
                continue;
            }

            //will hold all the tweets that were agreed upon
            ArrayList<AnalyzedTweet> agreedAnalyzedTweets = new ArrayList<>(tweets.size());
            //will hold all the tweets not agreed on
            ArrayList<AnalyzedTweet> notAgreedAnalyzedTweets = new ArrayList<>(tweets.size());

            for (Tweet tweet : tweets) {
                SanitizedTweet sanitizedTweet = preProcessor.sanitizeTweet(tweet);
                if (sanitizedTweet.getSanitizedText().length() < 10)//to small text to analyze
                    continue;
                AnalyzedTweet apacheAnalyzedTweet = stanford.analyzeSanitizedTweetSentiment(sanitizedTweet);
                AnalyzedTweet stanfordAnalyzedTweet = apache.analyzeSanitizedTweetSentiment(sanitizedTweet);

                if (apacheAnalyzedTweet.getOverallSentiment() == stanfordAnalyzedTweet.getOverallSentiment())
                    agreedAnalyzedTweets.add(apacheAnalyzedTweet);
                else
                    notAgreedAnalyzedTweets.add(stanfordAnalyzedTweet);
            }
            System.out.println("       agreed: " + agreedAnalyzedTweets.size());
            System.out.println("   not agreed: " + notAgreedAnalyzedTweets.size());
            IOTweetHelper.writeAnalyzedTweetsToCSV(agreedAnalyzedTweets, "agreed" + i + ".csv");
            IOTweetHelper.writeAnalyzedTweetsToCSV(notAgreedAnalyzedTweets, "notagreed" + i + ".csv");
        }

    }

    private static void printUsage() {
        System.out.println("Usage: java PingServer root-dir os stanford-model-dir apache-model-dir");
        System.out.println("          root-dir - path to the root directory");
        System.out.println("                os - what operating system your using, either \'unix\' or \'win\'");
        System.out.println("  apache-model-dir - Path to dir of apache models");
        System.out.println("stanford-model-dir - path to dir of stanford models");
    }

    public static void printFolderDirectoryHierarchy(File[] files, int indent) {
        for (File file : files) {
            for (int i = 1; i < indent; i++)
                System.out.print(" ");
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                printFolderDirectoryHierarchy(file.listFiles(), indent + 4); // Calls same method again.
            } else {
                System.out.println("File: " + file.getName());
            }
        }
    }
}
