package nlp;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class IOTweetHelper {

    final static String[] RAW_TWITTER_DATA_HEADERS = {"User ID", "User name", "User screen name", "Date user created account", "Statuses posted",
            "Amount of followers", "Amount user is following", "User location", "User timezone",
            "User language", "Status ID", "Status language", "Date created", "Status location", "Is favorited",
            "Amount of favorites", "Is retweeted", "Amount of retweets", "Is retweeted by me", "Is a retweet",
            "Retweeted status ID", "Is possibly sensitive", "Is truncated", "Place", "Description", "Text"};

    final static String[] ADDITIONAL_SANITIZED_TWEET_HEADERS = {"Sanitized text",
            "User names count from text",
            "Web url count from text",
            "Hash tag count from text",
            "Repeated punctuation count from text"};

    final static String[] ADDITIONAL_ANALYZED_TWEET_HEADERS = {"Analysis author class name",
            "Overall sentiment",
            "Overall positive sentiment percent",
            "Overall negative sentiment percent"};
    final static String[] ADDITIONAL_TWEET_CONSOLIDATER_HEADERS = {"Sentiment consensus"};

    private transient static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);


    /**
     * generates an array of type string with ADDITIONAL_SANITIZED_TWEET_HEADERS appended to RAW_TWITTER_DATA_HEADERS
     *
     * @return
     */
    private static String[] generateSanitizedTweetHeaders() {
        int rawLen = RAW_TWITTER_DATA_HEADERS.length;
        int sanLen = ADDITIONAL_SANITIZED_TWEET_HEADERS.length;
        int overallLength = rawLen + sanLen;
        String[] array = new String[overallLength];

        for (int i = 0; i < rawLen; i++)
            array[i] = RAW_TWITTER_DATA_HEADERS[i];

        int i = rawLen;
        int j = 0;
        while (i < overallLength) {
            array[i] = ADDITIONAL_SANITIZED_TWEET_HEADERS[j];
            i++;
            j++;
        }
        return array;
    }

    /**
     * generates an array of type string with ADDITIONAL_ANALYZED_TWEET_HEADERS
     * appended to generateSanitizedTweetHeaders()
     *
     * @return
     */
    private static String[] generateAnalyzedTweetHeaders() {
        String[] parentHeaders = generateSanitizedTweetHeaders();

        int rawLen = parentHeaders.length;
        int sanLen = ADDITIONAL_ANALYZED_TWEET_HEADERS.length;
        int overallLength = rawLen + sanLen;
        String[] array = new String[overallLength];

        for (int i = 0; i < rawLen; i++)
            array[i] = parentHeaders[i];

        int i = rawLen;
        int j = 0;
        while (i < overallLength) {
            array[i] = ADDITIONAL_ANALYZED_TWEET_HEADERS[j];
            i++;
            j++;
        }
        return array;
    }
    private static String[] generateConsensusHeaders() {
        String[] parentHeaders = generateSanitizedTweetHeaders();
        String[] both = (String[]) ArrayUtils.addAll(parentHeaders, ADDITIONAL_TWEET_CONSOLIDATER_HEADERS);
        String[] both1 = (String[]) ArrayUtils.addAll(both, ADDITIONAL_ANALYZED_TWEET_HEADERS);
        String[] both2 = (String[]) ArrayUtils.addAll(both1, ADDITIONAL_ANALYZED_TWEET_HEADERS);
        return both2;
    }

    /**
     * Reads from a file and creates a list of Tweets.
     *
     * @param fileName - path to the csv file
     *
     * @return list of Tweets
     * @throws IOException - if we have trouble reading the file.
     */
    public static List<Tweet> readTweetsFromCsvFile(String fileName) throws IOException {
        Reader in = new FileReader(fileName);

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader(RAW_TWITTER_DATA_HEADERS).withFirstRecordAsHeader().parse(in);
        List<Tweet> tweets = new ArrayList<>();
        for(CSVRecord record : records)
        {
            Tweet tweet = generateTweetFromMap(record.toMap());
            tweets.add(tweet);
        }
        return tweets;
    }
    /**
     * Reads from a file and creates a list of Sanitized Tweets.
     *
     * @param fileName - path to the csv file
     *
     * @return list of Sanitized Tweets
     * @throws IOException - if we have trouble reading the file.
     */
    public static List<SanitizedTweet> readSanitizedTweetsFromCsvFile(String fileName) throws IOException {
        Reader in = new FileReader(fileName);
        String[] headers = generateSanitizedTweetHeaders();
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader(headers).withFirstRecordAsHeader().parse(in);
        List<SanitizedTweet> sanitizedTweets = new ArrayList<>();
        for(CSVRecord record : records)
        {
            SanitizedTweet tweet = generateSanitizedTweetFromMap(record.toMap());
            sanitizedTweets.add(tweet);
        }
        return sanitizedTweets;
    }

    /**
     * Reads a serialized file of lists of analyzed tweets
     *
     * @param filePath - to the serialized list
     * @return list of tweets
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws FileNotFoundException
     */
    public static List<AnalyzedTweet> readSerializedAnalyzedTweets(String filePath) throws IOException, ClassNotFoundException, FileNotFoundException{
        List<AnalyzedTweet> tweets;
        FileInputStream file;
        ObjectInputStream in;

        file = new FileInputStream(filePath);
        in = new ObjectInputStream(file);
        tweets = (List<AnalyzedTweet>) in.readObject();

        in.close();
        file.close();
        return tweets;
    }

    public static void writeSanitizedTweetsToCSV(List<SanitizedTweet> tweets, String filePath) {
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //print headers
        String[] headers = generateSanitizedTweetHeaders();
        int numCols = headers.length;

        for (int i = 0; i < numCols - 1; i++) {
            pw.print(headers[i] + ",");
        }
        pw.println(headers[numCols - 1]); //last column doesn't need a comma

        //print sanitized tweets
        for (int i = 0; i < tweets.size(); i++)
        {
            SanitizedTweet tweet = tweets.get(i);
            Map<String, String> map = mapTweetToCSVColumns(tweet);

            String entry = null;
            for (int j = 0; j < numCols - 1; j++)
            {
                entry = map.get(headers[j]);
                pw.print(StringEscapeUtils.escapeCsv(entry) + ",");
            }
            entry = map.get(headers[numCols - 1]);
            pw.println(StringEscapeUtils.escapeCsv(entry)); //last column doesn't need a comma
            if(i % 10000 == 0)
                System.out.printf("Writing %d out of %d done: %.2f%%\n", i, tweets.size(), ((i*1.0)/tweets.size())*100);
        }
        pw.close();
    }
    public static void writeTweetConsolidatorToCSV(TweetConsolidator tc, String filePath) {
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //print headers
        String[] headers = generateConsensusHeaders();
        int numCols = headers.length;

        for (int i = 0; i < numCols - 1; i++) {
            pw.print(headers[i] + ",");
        }
        pw.println(headers[numCols - 1]); //last column doesn't need a comma


        //print analyzed tweets
        for (int i = 0; i < tc.apache.size(); i++)
        {
            AnalyzedTweet apacheTweet = tc.apache.get(i);
            AnalyzedTweet stanfordTweet = tc.stanford.get(i);
            Map<String, String> apacheMap = mapTweetToCSVColumns(apacheTweet);
            Map<String, String> stanfordMap = mapTweetToCSVColumns(stanfordTweet);

            String entry = null;
            //print sanitized tweet information
            for (int j = 0; j <= 30; j++)
            {
                entry = apacheMap.get(headers[j]);
                pw.print(StringEscapeUtils.escapeCsv(entry) + ",");
            }

            //print tweet sentimentConsensus
            pw.print(tc.sentimentConsensus[i] + ",");

            //print apache analyzed info
            for(int j = 32; j <= 35; j++){
                entry = apacheMap.get(headers[j]);
                pw.print(StringEscapeUtils.escapeCsv(entry) + ",");
            }
            //print apache analyzed info
            for(int j = 36; j <= 38; j++){
                entry = stanfordMap.get(headers[j]);
                pw.print(StringEscapeUtils.escapeCsv(entry) + ",");
            }
            //print last column
            entry = stanfordMap.get(headers[numCols - 1]);
            pw.println(StringEscapeUtils.escapeCsv(entry)); //last column doesn't need a comma
        }
        pw.close();

    }
    public static void writeAnalyzedTweetsToCSV(List<AnalyzedTweet> analyzedTweets, String filePath) {
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //print headers
        String[] headers = generateAnalyzedTweetHeaders();
        int numCols = headers.length;

        for (int i = 0; i < numCols - 1; i++) {
            pw.print(headers[i] + ",");
        }
        pw.println(headers[numCols - 1]); //last column doesn't need a comma

        //print analyzed tweets
        for (int i = 0; i < analyzedTweets.size(); i++)
        {
            AnalyzedTweet tweet = analyzedTweets.get(i);
            Map<String, String> map = mapTweetToCSVColumns(tweet);

            String entry = null;
            for (int j = 0; j < numCols - 1; j++)
            {
                entry = map.get(headers[j]);
                pw.print(StringEscapeUtils.escapeCsv(entry) + ",");
            }
            entry = map.get(headers[numCols - 1]);
            pw.println(StringEscapeUtils.escapeCsv(entry)); //last column doesn't need a comma
        }
        pw.close();
    }

    /**
     * Writes a list of tweets to a serialized object file, that can be read in from the readSerializedTweets() method
     *
     * @param tweets
     * @param filePath
     * @throws IOException
     */
    public static void writeSerializedAnalyzedTweets(List<AnalyzedTweet> tweets, String filePath) throws IOException{
        FileOutputStream file;
        ObjectOutputStream objectOut;

        file = new FileOutputStream(filePath);
        objectOut = new ObjectOutputStream(file);

        objectOut.writeObject(tweets);
        objectOut.close();
        file.close();
    }

    /**
     * Maps a tweet object's instance variables to the name of a csv column
     * All you need to do is pass a tweet. this method will notice if it is a child of tweet and add those extra
     * information aswell.
     *
     * @param tweet
     * @return
     */
    private static Map<String, String> mapTweetToCSVColumns(Tweet tweet) {
        Map<String, String> map = new HashMap<String, String>(30);
        map.put("User ID", tweet.getUserId());
        map.put("User name", tweet.getUserName());
        map.put("User screen name", tweet.getUserScreenName());
        map.put("Date user created account", tweet.getDateUserCreatedAccount());
        map.put("Statuses posted", tweet.getStatusesPosted());
        map.put("Amount of followers", tweet.getAmountOfFollowers());
        map.put("Amount user is following", tweet.getAmountUserIsFollowing());
        map.put("User location", tweet.getUserLocation());
        map.put("User timezone", tweet.getUserTimezone());
        map.put("User language", tweet.getUserLanguage());
        map.put("Status ID", tweet.getStatusId());
        map.put("Status language", tweet.getStatusLanguage());
        map.put("Date created", tweet.getDateCreated().format(dateFormat));
        map.put("Status location", tweet.getStatusLocation());
        map.put("Is favorited", tweet.getIsFavorited());
        map.put("Amount of favorites", tweet.getAmountOfFavorites());
        map.put("Is retweeted", tweet.getIsRetweeted());
        map.put("Amount of retweets", tweet.getAmountOfRetweets());
        map.put("Is retweeted by me", tweet.getIsRetweetedByMe());
        map.put("Is a retweet", tweet.getIsARetweet());
        map.put("Retweeted status ID", tweet.getRetweetedStatusId());
        map.put("Is possibly sensitive", tweet.getIsPossiblySensitive());
        map.put("Is truncated", tweet.getIsTruncated());
        map.put("Place", tweet.getPlace());
        map.put("Description", tweet.getDescription());
        map.put("Text", tweet.getText());
        if (tweet instanceof SanitizedTweet) {
            SanitizedTweet sanitizedTweet = (SanitizedTweet) tweet;
            map.put(IOTweetHelper.ADDITIONAL_SANITIZED_TWEET_HEADERS[0], sanitizedTweet.getSanitizedText());
            map.put(IOTweetHelper.ADDITIONAL_SANITIZED_TWEET_HEADERS[1], Integer.toString(sanitizedTweet.getUserNamesCountFromText()));
            map.put(IOTweetHelper.ADDITIONAL_SANITIZED_TWEET_HEADERS[2], Integer.toString(sanitizedTweet.getWebUrlCountFromText()));
            map.put(IOTweetHelper.ADDITIONAL_SANITIZED_TWEET_HEADERS[3], Integer.toString(sanitizedTweet.getHashTagsCountFromText()));
            map.put(IOTweetHelper.ADDITIONAL_SANITIZED_TWEET_HEADERS[4], Integer.toString(sanitizedTweet.getRepeatedPunctuationCountFromText()));
        }
        if(tweet instanceof AnalyzedTweet){
            AnalyzedTweet analyzedTweet = (AnalyzedTweet) tweet;
            map.put(IOTweetHelper.ADDITIONAL_ANALYZED_TWEET_HEADERS[0], analyzedTweet.getAnalysisAuthorClassName());
            map.put(IOTweetHelper.ADDITIONAL_ANALYZED_TWEET_HEADERS[1], Integer.toString(analyzedTweet.getOverallSentiment()));
            map.put(IOTweetHelper.ADDITIONAL_ANALYZED_TWEET_HEADERS[2], Double.toString(analyzedTweet.getOverallPositiveSentimentPercent()));
            map.put(IOTweetHelper.ADDITIONAL_ANALYZED_TWEET_HEADERS[3], Double.toString(analyzedTweet.getOverallNegativeSentimentPercent()));
        }
        return map;
    }

    public static Tweet generateTweetFromMap(Map<String, String> map) {
        Tweet tweet = new Tweet();
        tweet.setUserId(map.get("User ID"));
        tweet.setUserName(map.get("User name"));
        tweet.setUserScreenName(map.get("User screen name"));
        tweet.setDateUserCreatedAccount(map.get("Date user created account"));
        tweet.setStatusesPosted(map.get("Statuses posted"));
        tweet.setAmountOfFollowers(map.get("Amount of followers"));
        tweet.setAmountUserIsFollowing(map.get("Amount user is following"));
        tweet.setUserLocation(map.get("User location"));
        tweet.setUserTimezone(map.get("User timezone"));
        tweet.setUserLanguage(map.get("User language"));
        tweet.setStatusId(map.get("Status ID"));
        tweet.setStatusLanguage(map.get("Status language"));
        String dateCreatedString = map.get("Date created");
        ZonedDateTime dateCreatedDateTime = ZonedDateTime.parse(dateCreatedString, dateFormat);
        tweet.setDateCreated(dateCreatedDateTime);
        tweet.setStatusLocation(map.get("Status location"));
        tweet.setIsFavorited(map.get("Is favorited"));
        tweet.setAmountOfFavorites(map.get("Amount of favorites"));
        tweet.setIsRetweeted(map.get("Is retweeted"));
        tweet.setAmountOfRetweets(map.get("Amount of retweets"));
        tweet.setIsRetweetedByMe(map.get("Is retweeted by me"));
        tweet.setIsARetweet(map.get("Is a retweet"));
        tweet.setRetweetedStatusId(map.get("Retweeted status ID"));
        tweet.setIsPossiblySensitive(map.get("Is possibly sensitive"));
        tweet.setIsTruncated(map.get("Is truncated"));
        tweet.setPlace(map.get("Place"));
        tweet.setDescription(map.get("Description"));
        tweet.setText(map.get("Text"));
        return tweet;
    }
    public static SanitizedTweet generateSanitizedTweetFromMap(Map<String, String> map) {
        Tweet tweet = generateTweetFromMap(map);
        SanitizedTweet sanitizedTweet = new SanitizedTweet(tweet);
        sanitizedTweet.setSanitizedText(map.get("Sanitized text"));

        sanitizedTweet.setUserNamesCountFromText(
                Integer.parseInt(map.get("User names count from text")));
        sanitizedTweet.setWebUrlCountFromText(
                Integer.parseInt(map.get("Web url count from text")));
        sanitizedTweet.setHashTagsCountFromText(
                Integer.parseInt(map.get("Hash tag count from text")));
        sanitizedTweet.setRepeatedPunctuationCountFromText(
                Integer.parseInt(map.get("Repeated punctuation count from text")));

        return sanitizedTweet;
    }
    public static SanitizedTweet generateAnalyzedTweetFromMap(Map<String, String> map) {
        SanitizedTweet sanitizedTweet = generateSanitizedTweetFromMap(map);
        AnalyzedTweet analyzedTweet = new AnalyzedTweet(sanitizedTweet);
        analyzedTweet.setAnalysisAuthorClassName(ADDITIONAL_ANALYZED_TWEET_HEADERS[0]);
        analyzedTweet.setOverallSentiment(Integer.parseInt(ADDITIONAL_ANALYZED_TWEET_HEADERS[1]));
        analyzedTweet.setOverallPositiveSentimentPercent(Double.parseDouble(ADDITIONAL_ANALYZED_TWEET_HEADERS[2]));
        analyzedTweet.setOverallNegativeSentimentPercent(Double.parseDouble(ADDITIONAL_ANALYZED_TWEET_HEADERS[3]));
        return analyzedTweet;
    }

}
