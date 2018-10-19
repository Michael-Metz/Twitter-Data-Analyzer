package nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TwitterPreProcessor {

    //source https://stackoverflow.com/questions/2304632/regex-for-twitter-username
    public static final String USER_NAME_REGEX = "(^|[^@\\w])@(\\w{1,15})\\b";

    //refactored from source https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
//    public static final String WEB_URL_REGEX = "[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
    public static final String WEB_URL_REGEX = "(http(s)?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    //source http://erictarn.com/post/1060722347/the-best-twitter-hashtag-regular-expression
    public static final String HASH_TAG_REGEX = "\\B#\\w*[a-zA-Z]+\\w*";

    //source https://stackoverflow.com/questions/27951597/how-can-i-replace-all-duplicate-punctuation-with-single-punctuation-in-php
    // Hello!!!!!!???? Why Have'nt you responded to my text?????? -> Hello!? Why Have'nt you responded to my text?
    public static final String REPEATED_PUNCTUATION_REGEX = "((?<!:)[^\\p{L}\\p{N}])\\1+";

    //source https://stackoverflow.com/questions/27951597/how-can-i-replace-all-duplicate-punctuation-with-single-punctuation-in-php
    // I looooooooove baseballllllll -> I love baseball;
    public static final String REPEATED_CHARACTERS_REGEX = "";

    public static void main(String args[]){
        Scanner kb = new Scanner(System.in);
        System.out.println("enter the input filename");
        String inputFile = kb.nextLine();
        System.out.println("enter the output filename");
        String outputFile = kb.nextLine();
        List<Tweet> tweets = null;
        try {
            tweets = IOTweetHelper.readTweetsFromCsvFile(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pattern userNamePattern = Pattern.compile(USER_NAME_REGEX);
        Pattern webUrlPattern = Pattern.compile(WEB_URL_REGEX);
        Pattern hashTagPattern = Pattern.compile(HASH_TAG_REGEX);
        Pattern repeatedPunctuationPattern = Pattern.compile(REPEATED_PUNCTUATION_REGEX);
        Matcher matcher = null;
        int matchCount = 0;

        List<SanitizedTweet> sanitizedTweets = new ArrayList<>(tweets.size());
        SanitizedTweet sanitizedTweet = null;
        for (int i = 0; i < tweets.size(); i++) {
            if(i % 10000 == 0)
                System.out.printf("Sanitizing %d out of %d done: %.2f%%\n", i, tweets.size(), ((i*1.0)/tweets.size())*100);
            Tweet tweet = tweets.get(i);
            sanitizedTweet = new SanitizedTweet(tweet);
            String text = sanitizedTweet.getText();
            //handle web urls
            matcher = webUrlPattern.matcher(text);
            matchCount = 0;
            while (matcher.find())
                matchCount++;
            sanitizedTweet.setWebUrlCountFromText(matchCount);
            text = text.replaceAll(WEB_URL_REGEX, "");

            //handle user names
            matcher = userNamePattern.matcher(text);
            matchCount = 0;
            while (matcher.find())
                matchCount++;
            sanitizedTweet.setUserNamesCountFromText(matchCount);
            text = text.replaceAll(USER_NAME_REGEX, "");

            //handle hashtags
            matcher = hashTagPattern.matcher(text);
            matchCount = 0;
            while (matcher.find())
                matchCount++;
            sanitizedTweet.setHashTagsCountFromText(matchCount);
            text = text.replaceAll(HASH_TAG_REGEX, "");

            //handle repeated punctuation
            matcher = repeatedPunctuationPattern.matcher(text);
            matchCount = 0;
            while (matcher.find())
                matchCount++;
            sanitizedTweet.setRepeatedPunctuationCountFromText(matchCount);
            text = text.replaceAll(REPEATED_PUNCTUATION_REGEX, "$1");

            //removed tweets with less then 10 characters of sanitized text. Little text might screw up nlp analyzers
            if (10 < text.trim().length()) {
                sanitizedTweet.setSanitizedText(text);
                sanitizedTweets.add(sanitizedTweet);
            }
        }

        IOTweetHelper.writeSanitizedTweetsToCSV(sanitizedTweets, outputFile);
    }
}
