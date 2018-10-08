package nlp.tweetanalyzers;

import java.util.List;
import nlp.*;

public class ApacheNLPTweetAnalyzer implements OverallSentimentAnalyzer {

    private static ApacheNLPTweetAnalyzer apacheNLPTweetAnalyzer = null;

    /**
     * Singleton design pattern
     * @return
     */
    public static ApacheNLPTweetAnalyzer getInstance(){
        if(apacheNLPTweetAnalyzer == null)
            apacheNLPTweetAnalyzer = new ApacheNLPTweetAnalyzer();
        return apacheNLPTweetAnalyzer;
    }

    /**
     * Private constructor called by the getInstance.
     */
    private ApacheNLPTweetAnalyzer(){
        //do any nlp library set up here
    }

    /**
     * This method should run all the analysis on a SanitizedTweet's sanitizedText.
     * <p>
     * The other child interfaces that extend this interface are marking interfaces, that indicate what type
     * of analysis is done on the SanitizedTweet
     * <p>
     * Implement as many of these marker interfaces as you can
     * <p>
     * Each marker interface you implement must perform the indicated analysis on the sanitizedTweet
     * <p>
     * For example if you implement @SEE OverallSentimentAnalyzer and @SentenceSentimentAnalyzer you must analyze the
     * overall Sentiment of the tweet and break the tweet into sentences and the analyze the sentiment of each sentence
     * of that tweet
     *
     * @param sanitizedTweet
     * @return a AnalyzedTweet with the analysis(Indicated by the the marker interfaces) done and appropriate fields
     * populated
     */
    @Override
    public AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet) {
        return null;
    }

    /**
     * @param sanitizedTweets
     * @return
     * @SEE AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet);
     */
    @Override
    public List<AnalyzedTweet> analyzeSanitizedTweetsSentiment(List<SanitizedTweet> sanitizedTweets) {
        return null;
    }

    /**
     * Returns the name of the analyzed classname use this to sign each tweet you analyze with the name of your class.
     * i.e analyzedTweet.setAnalysisAuthorClassName(this.getClass().getName())
     *
     * @return
     */
    @Override
    public String getAnalyzerClassName() {
        return this.getAnalyzerClassName();
    }
}
