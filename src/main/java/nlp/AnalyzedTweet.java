package nlp;

import java.util.HashMap;
import java.util.Map;

public class AnalyzedTweet extends SanitizedTweet implements Comparable{

    public static final int VERY_NEGATIVE_SENTIMENT = -2;
    public static final int NEGATIVE_SENTIMENT = -1;
    public static final int NEUTRAL_SENTIMENT = 0;
    public static final int POSITIVE_SENTIMENT = 1;
    public static final int VERY_POSITIVE_SENTIMENT = 2;
    public static final int UNDEFINED_SENTIMENT = -127;

    //TODO consider using a package to do inline map initialization or immutable maps.
    private static Map<Integer, String> sentimentMap = null;

    private String analysisAuthorClassName;
    private int overallSentiment;
    private double overallNegativeSentimentPercent, overallPositiveSentimentPercent;
    private String[] sentences = null;
    private int[] sentenceSentiments = null;

    public AnalyzedTweet(SanitizedTweet tweet) {
        super(tweet);
        //map overallSentiment values to messages.
        if(sentimentMap == null){
            sentimentMap = new HashMap<>(10);
            sentimentMap.put(-2, "Very Negative");
            sentimentMap.put(-1, "Negative");
            sentimentMap.put(0,  "Neutral");
            sentimentMap.put(1,  "Positive");
            sentimentMap.put(2,  "Very Positive");
        }
        overallSentiment = Integer.MIN_VALUE;
    }

    public String getAnalysisAuthorClassName() {
        return analysisAuthorClassName;
    }

    public void setAnalysisAuthorClassName(String analysisAuthorClassName) {
        this.analysisAuthorClassName = analysisAuthorClassName;
    }

    public String getSentimentString(){
        return sentimentMap.get(overallSentiment);
    }

    public int getOverallSentiment() {
        return overallSentiment;
    }

    public void setOverallSentiment(int overallSentiment) {
        this.overallSentiment = overallSentiment;
    }

    public double getOverallNegativeSentimentPercent() {
        return overallNegativeSentimentPercent;
    }

    public void setOverallNegativeSentimentPercent(double overallNegativeSentimentPercent) {
        this.overallNegativeSentimentPercent = overallNegativeSentimentPercent;
    }

    public double getOverallPositiveSentimentPercent() {
        return overallPositiveSentimentPercent;
    }

    public void setOverallPositiveSentimentPercent(double overallPositiveSentimentPercent) {
        this.overallPositiveSentimentPercent = overallPositiveSentimentPercent;
    }

    public String[] getSentences() {
        return sentences;
    }

    public void setSentences(String[] sentences) {
        this.sentences = sentences;
    }

    public int[] getSentenceSentiments() {
        return sentenceSentiments;
    }

    public void setSentenceSentiments(int[] sentenceSentiments) {
        this.sentenceSentiments = sentenceSentiments;
    }

    /**
     * Stanford nlp only does sentiment on sentences so we must determine the over sentiment of the tweet based on the
     * sentiment of each sentence.
     *
     * @return true if successful, false if unsuccessful
     */
    public boolean populateOverallSentiment(){
        if(sentences == null || sentenceSentiments == null)
            return false;

        int totalPositive = 0;
        int totalNegative = 0;
        for(int i = 0; i < sentences.length; i++){
            switch (sentenceSentiments[i]){
                case NEGATIVE_SENTIMENT :
                    totalNegative++;
                    break;
                case POSITIVE_SENTIMENT:
                    totalPositive++;
                    break;
            }
        }

        int sum = (totalNegative*-1) + (totalPositive*1);
        if(sum < 0)
            overallSentiment = NEGATIVE_SENTIMENT;
        else if(0 < sum)
            overallSentiment = POSITIVE_SENTIMENT;
        else if (sum == 0)
            overallSentiment = NEUTRAL_SENTIMENT;

        return true;
    }

}
