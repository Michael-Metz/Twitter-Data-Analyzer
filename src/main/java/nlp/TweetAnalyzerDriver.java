package nlp;

import nlp.tweetanalyzers.*;
import opennlp.tools.cmdline.CLI;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 */
public class TweetAnalyzerDriver {
    final static String[] HEADERS = {"User ID", "User name", "User screen name", "Date user created account", "Statuses posted",
            "Amount of followers", "Amount user is following", "User location", "User timezone",
            "User language", "Status ID", "Status language", "Date created", "Status location", "Is favorited",
            "Amount of favorites", "Is retweeted", "Amount of retweets", "Is retweeted by me", "Is a retweet",
            "Retweeted status ID", "Is possibly sensitive", "Is truncated", "Place", "Description", "Text"};

    private static TweetSentimentAnalyzer stanfordNlpAnalyzer = null;
    private static ApacheNLPTweetAnalyzer apacheNlpAnalyzer = null;
    private static TweetSentimentAnalyzer randomSentimentAnalyzer = null;

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        String[] menu = {"Read Sanitized Tweets from CSV", "Read Serialized \'Analyzed Tweets List\' file"};
        CLIUtil.printBanner("Menu");
        int choice = -1;
        choice = CLIUtil.displayMenu(menu);
        List<SanitizedTweet> sanitizedTweets = null;
        List<AnalyzedTweet> analyzedTweets = null;


        boolean skipMenuTwo = false;
        switch (choice) {
            case 0:
                //repeat until we read in a valid csv file
                while (true) {
                    System.out.println("Enter Path To CSV File");
                    String fp = kb.nextLine();
                    try {
                        sanitizedTweets = IOTweetHelper.readSanitizedTweetsFromCsvFile(fp);
                    } catch (IOException e) {
                        System.out.println("Could not read " + fp + " try again!");
                        continue;
                    }
                    break;
                }
                break;
            case 1:
                while (true) {
                    System.out.println("Enter Path To Serialized Object File");
                    String fp = kb.nextLine();
                    try {
                        analyzedTweets = IOTweetHelper.readSerializedAnalyzedTweets(fp);
                    } catch (IOException e) {
                        System.out.println("Could not read " + fp + " try again!");
                        continue;
                    } catch (ClassNotFoundException e) {
                        System.out.println("Could not read " + fp + " try again!");
                        continue;
                    }

                    skipMenuTwo = true;
                    break;
                }
                break;

        }


        boolean menuTwoFlag = true;
        while (menuTwoFlag && skipMenuTwo == false) {
            String[] menuTwo = {"Analyze tweets with stanford analyzer",
                    "Analyze tweets with apache analyzer",
                    "Analyze tweets with random analyzer",
                    "Next Menu"};

            boolean areTweetsAnalyzed = (analyzedTweets != null);
            String subTitle = String.format("Tweets: %d %s",
                    areTweetsAnalyzed ? analyzedTweets.size() : sanitizedTweets.size(),
                    areTweetsAnalyzed ? "Analyzed by " + analyzedTweets.get(0).getAnalysisAuthorClassName() : "Sanitized");
            CLIUtil.printBanner("Menu Two",subTitle);

            choice = CLIUtil.displayMenu(menuTwo);
            switch (choice) {
                case 0:
                    stanfordNlpAnalyzer = StanfordNLPTweetAnalyzer.getInstance(false);
                    analyzedTweets = stanfordNlpAnalyzer.analyzeSanitizedTweetsSentiment(sanitizedTweets);
                    break;
                case 1:
                    apacheNlpAnalyzer = ApacheNLPTweetAnalyzer.getInstance();
                    analyzedTweets = apacheNlpAnalyzer.analyzeSanitizedTweetsSentiment(sanitizedTweets);
                    break;
                case 2:
                    randomSentimentAnalyzer = RandomizedTweetAnalyzer.getInstance();
                    analyzedTweets = randomSentimentAnalyzer.analyzeSanitizedTweetsSentiment(sanitizedTweets);
                    break;
                case 3:
                    menuTwoFlag = false;
                    break;
            }
        }

        boolean menuThreeFlag = true;
        while (menuThreeFlag) {
            String[] menuThree = {
                    "Serialize AnalyzedTweets",
                    "Next Menu"};
            String subTitle = "save to file so you don't have to re-analyze tweets";
            CLIUtil.printBanner("Menu Three",subTitle);

            choice = CLIUtil.displayMenu(menuThree);
            switch (choice) {
                case 0:
                    while (true) {
                        System.out.println("Enter file name to serialize the analyzed tweets to");
                        String fp = kb.nextLine();
                        try {
                            IOTweetHelper.writeSerializedAnalyzedTweets(analyzedTweets, fp);
                        } catch (IOException e) {
                            System.out.println("Could not write " + fp + " try again!");
                            continue;
                        }
                        break;
                    }
                    break;
                case 1:
                    menuThreeFlag = false;
                    break;
            }
        }


        boolean menuFourFlag = true;
        while (menuFourFlag) {
            String[] menuFour = {"Determine date range of list", "Run stats", "Write analyzed tweets to csv file", "Output csv files separated csv files", "analyze tweets against apache"};
            CLIUtil.printBanner("memory usage", MemoryUsageUTIL.getUsedMemoryInMiB());
            choice = CLIUtil.displayMenu(menuFour);

            switch (choice) {
                case 0:
                    ZonedDateTime minDate = analyzedTweets.get(0).getDateCreated();
                    ZonedDateTime maxDate = analyzedTweets.get(0).getDateCreated();
                    for (Tweet analyzedTweet : analyzedTweets) {
                        ZonedDateTime d = analyzedTweet.getDateCreated();
                        if (d.isBefore(minDate))
                            minDate = d;
                        if (d.isAfter(maxDate))
                            maxDate = d;
                    }
                    System.out.printf("The Analyzed list contains %d tweets ranging from %s to %s\n",
                            analyzedTweets.size(),
                            minDate.toString(),
                            maxDate.toString());

                    break;
                case 1:
                    printSatistics(analyzedTweets);
                    break;
                case 2:
                    System.out.println("Enter the path for the file you want to save to");
                    String fp = kb.nextLine();
                    IOTweetHelper.writeAnalyzedTweetsToCSV(analyzedTweets, fp);
                    break;
                case 3:
                    System.out.println("The current loaded tweets we will output pospos.csv, posneu.csv, neu.csv, neuneg.csv, negneg.csv");
                    String[] fileNames = {"pospos.csv", "posneg.csv", "neu.csv", "neuneg.csv", "negneg.csv", "undf.csv"};
                    List<AnalyzedTweet>[] toFiles = (List<AnalyzedTweet>[]) new List[fileNames.length];
                    for (int i = 0; i < fileNames.length; i++) {
                        toFiles[i] = new ArrayList<>();
                    }
                    for (AnalyzedTweet analyzedTweet : analyzedTweets) {
                        switch (analyzedTweet.getOverallSentiment()) {
                            case AnalyzedTweet.VERY_POSITIVE_SENTIMENT:
                                toFiles[0].add(analyzedTweet);
                                break;
                            case AnalyzedTweet.POSITIVE_SENTIMENT:
                                toFiles[1].add(analyzedTweet);
                                break;
                            case AnalyzedTweet.NEUTRAL_SENTIMENT:
                                toFiles[2].add(analyzedTweet);
                                break;
                            case AnalyzedTweet.NEGATIVE_SENTIMENT:
                                toFiles[3].add(analyzedTweet);
                                break;
                            case AnalyzedTweet.VERY_NEGATIVE_SENTIMENT:
                                toFiles[4].add(analyzedTweet);
                                break;
                            case AnalyzedTweet.UNDEFINED_SENTIMENT:
                                toFiles[5].add(analyzedTweet);
                                break;
                        }
                    }
                    for (int i = 0; i < fileNames.length; i++) {
                        IOTweetHelper.writeAnalyzedTweetsToCSV(toFiles[i], fileNames[i]);
                    }
                    break;
                case 4:
                    apacheNlpAnalyzer = ApacheNLPTweetAnalyzer.getInstance();

                    List<AnalyzedTweet> apacheAnalyzed = apacheNlpAnalyzer.analyzeSanitizedTweetsSentiment(analyzedTweets);
                    List<AnalyzedTweet> postive = new ArrayList<>();
                    List<AnalyzedTweet> negative = new ArrayList<>();
                    List<AnalyzedTweet> nuetral = new ArrayList<>();
                    List<AnalyzedTweet> undefined = new ArrayList<>();
                    for (int i = 0; i < analyzedTweets.size(); i++) {
                        int apacheSent = apacheAnalyzed.get(i).getOverallSentiment();
                        int stanfordSent = analyzedTweets.get(i).getOverallSentiment();
                        apacheSent = apacheSent == 2 || apacheSent == 1 ? 1 : apacheSent;
                        apacheSent = apacheSent == -2 || apacheSent == -1 ? -1 : apacheSent;
                        if (apacheSent == stanfordSent) {
                            switch (apacheSent) {
                                case -1:
                                    negative.add(apacheAnalyzed.get(i));
                                    break;
                                case 0:
                                    nuetral.add(apacheAnalyzed.get(i));
                                    break;
                                case 1:
                                    postive.add(apacheAnalyzed.get(i));
                                    break;
                            }
                        }
                        IOTweetHelper.writeAnalyzedTweetsToCSV(negative, "apache-neg.csv");
                        IOTweetHelper.writeAnalyzedTweetsToCSV(nuetral, "apache-nue.csv");
                        IOTweetHelper.writeAnalyzedTweetsToCSV(postive, "apache-pos.csv");
                    }
                    break;

            }

        }

    }


    /**
     * Computes some statistics over the sentiment of analyzed tweets
     *
     * @param tweets
     */
    private static void printSatistics(List<AnalyzedTweet> tweets) {
        int totalVeryNegative = 0;
        int totalNegative = 0;
        int totalNeutral = 0;
        int totalPositive = 0;
        int totalVeryPostive = 0;
        int totalUndefined = 0;
        int totalSentences = 0;

        Object analyzer = null;
        try {
            Class c = Class.forName(tweets.get(0).getAnalysisAuthorClassName());
            Method factoryMethod = c.getDeclaredMethod("getInstance");
            analyzer = factoryMethod.invoke(null, null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if (analyzer instanceof SentenceSentimentAnalyzer) {
            for (AnalyzedTweet tweet : tweets) {
                int[] sentiments = tweet.getSentenceSentiments();
                String[] sentences = tweet.getSentences();
                totalSentences += sentiments.length;
                for (int i = 0; i < sentiments.length; i++) {
                    int sentiment = sentiments[i];
                    String sentence = sentences[i];
                    switch (sentiment) {
                        case AnalyzedTweet.VERY_NEGATIVE_SENTIMENT:
                            totalVeryNegative++;
                            break;
                        case AnalyzedTweet.NEGATIVE_SENTIMENT:
                            totalNegative++;
                            break;
                        case AnalyzedTweet.NEUTRAL_SENTIMENT:
                            totalNeutral++;
                            break;
                        case AnalyzedTweet.POSITIVE_SENTIMENT:
                            totalPositive++;
                            break;
                        case AnalyzedTweet.VERY_POSITIVE_SENTIMENT:
                            totalVeryPostive++;
                            break;
                        case AnalyzedTweet.UNDEFINED_SENTIMENT:
                            totalUndefined++;
                            break;
                        default:
                            System.out.println(sentiment + " no sentitment " + sentence);
                    }
                }
            }

            System.out.printf("There are %d tweets, which compose %d sentences\n", tweets.size(), totalSentences);
            System.out.println("Here are the sentence sentiment breakdown");
            System.out.printf("Very Negative - %.2f%s : %d\n", ((totalVeryNegative * 1.0) / totalSentences) * 100, "%", totalVeryNegative);
            System.out.printf("     Negative - %.2f%s : %d\n", ((totalNegative * 1.0) / totalSentences) * 100, "%", totalNegative);
            System.out.printf("      Neutral - %.2f%s : %d\n", ((totalNeutral * 1.0) / totalSentences) * 100, "%", totalNeutral);
            System.out.printf("     Positive - %.2f%s : %d\n", ((totalPositive * 1.0) / totalSentences) * 100, "%", totalPositive);
            System.out.printf("Very Positive - %.2f%s : %d\n", ((totalVeryPostive * 1.0) / totalSentences) * 100, "%", totalVeryPostive);
            System.out.printf("    Undefined - %.2f%s : %d\n", ((totalUndefined * 1.0) / totalSentences) * 100, "%", totalUndefined);
        }
        if (analyzer instanceof OverallSentimentAnalyzer) {
            totalVeryNegative = 0;
            totalNegative = 0;
            totalNeutral = 0;
            totalPositive = 0;
            totalVeryPostive = 0;
            totalUndefined = 0;

            for (AnalyzedTweet tweet : tweets) {

                int sentiment = tweet.getOverallSentiment();
                switch (sentiment) {
                    case AnalyzedTweet.VERY_NEGATIVE_SENTIMENT:
                        totalVeryNegative++;
                        break;
                    case AnalyzedTweet.NEGATIVE_SENTIMENT:
                        totalNegative++;
                        break;
                    case AnalyzedTweet.NEUTRAL_SENTIMENT:
                        totalNeutral++;
                        break;
                    case AnalyzedTweet.POSITIVE_SENTIMENT:
                        totalPositive++;
                        break;
                    case AnalyzedTweet.VERY_POSITIVE_SENTIMENT:
                        totalVeryPostive++;
                        break;
                    case AnalyzedTweet.UNDEFINED_SENTIMENT:
                        totalUndefined++;
                        break;
                    default:
                        System.out.println(sentiment + " no sentitment " + tweet.getText());
                }

            }
            System.out.println("\nHere is the overall sentiment breakdown");
            System.out.printf("Very Negative - %.2f%s : %d\n", ((totalVeryNegative * 1.0) / tweets.size()) * 100, "%", totalVeryNegative);
            System.out.printf("     Negative - %.2f%s : %d\n", ((totalNegative * 1.0) / tweets.size()) * 100, "%", totalNegative);
            System.out.printf("      Neutral - %.2f%s : %d\n", ((totalNeutral * 1.0) / tweets.size()) * 100, "%", totalNeutral);
            System.out.printf("     Positive - %.2f%s : %d\n", ((totalPositive * 1.0) / tweets.size()) * 100, "%", totalPositive);
            System.out.printf("Very Positive - %.2f%s : %d\n", ((totalVeryPostive * 1.0) / tweets.size()) * 100, "%", totalVeryPostive);
            System.out.printf("    Undefined - %.2f%s : %d\n", ((totalUndefined * 1.0) / tweets.size()) * 100, "%", totalUndefined);
        }
    }

}
