package nlp;

import java.util.HashMap;
import java.util.Map;

public class SanitizedTweet extends Tweet {
    private String sanitizedText;
    private int userNamesCountFromText;
    private int webUrlCountFromText;
    private int hashTagsCountFromText;
    private int repeatedPunctuationCountFromText;

    /**
     * Copy constructor
     * @param tweet
     */
    public SanitizedTweet(SanitizedTweet tweet){
        super(tweet);
        this.sanitizedText = tweet.sanitizedText;
        this.userNamesCountFromText = tweet.userNamesCountFromText;
        this.webUrlCountFromText = tweet.webUrlCountFromText;
        this.hashTagsCountFromText = tweet.hashTagsCountFromText;
        this.repeatedPunctuationCountFromText = tweet.repeatedPunctuationCountFromText;
    }
    /**
     * Copy constructor
     * @param tweet
     */
    public SanitizedTweet(Tweet tweet){
        super(tweet);
    }

    public SanitizedTweet() {

    }

    public String getSanitizedText() {
        return sanitizedText;
    }

    public void setSanitizedText(String sanitizedText) {
        this.sanitizedText = sanitizedText;
    }

    public int getUserNamesCountFromText() {
        return userNamesCountFromText;
    }

    public void setUserNamesCountFromText(int userNamesCountFromText) {
        this.userNamesCountFromText = userNamesCountFromText;
    }

    public int getWebUrlCountFromText() {
        return webUrlCountFromText;
    }

    public void setWebUrlCountFromText(int webUrlCountFromText) {
        this.webUrlCountFromText = webUrlCountFromText;
    }

    public int getHashTagsCountFromText() {
        return hashTagsCountFromText;
    }

    public void setHashTagsCountFromText(int hashTagsCountFromText) {
        this.hashTagsCountFromText = hashTagsCountFromText;
    }

    public int getRepeatedPunctuationCountFromText() {
        return repeatedPunctuationCountFromText;
    }

    public void setRepeatedPunctuationCountFromText(int repeatedPunctuationCountFromText) {
        this.repeatedPunctuationCountFromText = repeatedPunctuationCountFromText;
    }

    @Override
    public String toString() {
        return "SanitizedTweet{" + '\'' +
                ", userNamesCountFromText=" + userNamesCountFromText +
                ", webUrlCountFromText=" + webUrlCountFromText +
                ", hashTagsCountFromText=" + hashTagsCountFromText +
                ", repeatedPunctuationCountFromText=" + repeatedPunctuationCountFromText +
                "sanitizedText='" + sanitizedText +
                '}';
    }

}
