package nlp;

import nlp.tweetanalyzers.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.*;

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
    private static TweetSentimentAnalyzer randomSentimentAnalyzer = new RandomizedTweetAnalyzer();

    public static void main(String[] args) {
        Scanner kb = new Scanner(System.in);
        String[] menu = {"Read Sanitized Tweets from CSV", "Read Serialized \'Analyzed Tweets List\' file"};
        CLIUtil.printBanner("Menu");
        int choice = -1;
        choice = CLIUtil.displayMenu(menu);
        List<SanitizedTweet> sanitizedTweets = null;
        List<AnalyzedTweet> analyzedTweets = null;

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

                    break;
                }
                break;

        }


        boolean menuTwoFlag = true;
        while (menuTwoFlag) {
            String[] menuTwo = {"Analyze tweets with stanford analyzer",
                    "Analyze tweets with apache analyzer",
                    "Analyze tweets with random analyzer",
                    "Serialize Analyzed Tweets to object file",
                                "Next Menu"};
//            CLIUtil.printBanner("Tweets Loaded", Integer.toString(analyzedTweets.hashCode()));
            choice = CLIUtil.displayMenu(menuTwo);

            switch (choice) {
                case 0:
                    stanfordNlpAnalyzer = StanfordNLPTweetAnalyzer.getInstance();
                    analyzedTweets = stanfordNlpAnalyzer.analyzeSanitizedTweetsSentiment(sanitizedTweets);
                    break;
                case 1:
                    apacheNlpAnalyzer = ApacheNLPTweetAnalyzer.getInstance();
                    analyzedTweets = apacheNlpAnalyzer.analyzeSanitizedTweetsSentiment(sanitizedTweets);
                    break;
                case 2:
                analyzedTweets = randomSentimentAnalyzer.analyzeSanitizedTweetsSentiment(sanitizedTweets);
                break;
                case 3:
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
                case 4:
                menuTwoFlag = false;
                    break;
            }
        }


        boolean menuThreeFlag = true;
        while (menuThreeFlag) {
            String[] menuThree = {"Determine date range of list", "Run stats", "Write analyzed tweets to csv file"};
            CLIUtil.printBanner("memory usage", MemoryUsageUTIL.getUsedMemoryInMiB());
            choice = CLIUtil.displayMenu(menuThree);

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
        int totalSentences = 0;

        Object analyzer = null;
        try {
            Class c = Class.forName(tweets.get(0).getAnalysisAuthorClassName());
            Method factoryMethod = c.getDeclaredMethod("getInstance");
            analyzer = factoryMethod.invoke(null, null);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        if(analyzer instanceof SentenceSentimentAnalyzer) {
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
        }
        if(analyzer instanceof OverallSentimentAnalyzer) {
            totalVeryNegative = 0;
            totalNegative = 0;
            totalNeutral = 0;
            totalPositive = 0;
            totalVeryPostive = 0;
            totalSentences = 0;
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
        }
    }

}
