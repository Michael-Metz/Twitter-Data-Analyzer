package nlp.tweetanalyzers;

import nlp.AnalyzedTweet;
import nlp.SanitizedTweet;

import java.util.List;

public interface TweetSentimentAnalyzer {


    /**
     * This method should run all the analysis on a SanitizedTweet's sanitizedText.
     *
     * The other child interfaces that extend this interface are marking interfaces, that indicate what type
     * of analysis is done on the SanitizedTweet
     *
     * Implement as many of these marker interfaces as you can
     *
     * Each marker interface you implement must perform the indicated analysis on the sanitizedTweet
     *
     * For example if you implement @SEE OverallSentimentAnalyzer and @SentenceSentimentAnalyzer you must analyze the
     * overall Sentiment of the tweet and break the tweet into sentences and the analyze the sentiment of each sentence
     * of that tweet
     *
     * @param sanitizedTweet
     * @return a AnalyzedTweet with the analysis(Indicated by the the marker interfaces) done and appropriate fields
     *         populated
     */
    AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet);

    /**
     * @SEE AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet);
     * @param sanitizedTweets
     * @return
     */
    List<AnalyzedTweet> analyzeSanitizedTweetsSentiment(List<? extends SanitizedTweet> sanitizedTweets);

    /**
     * Returns the name of the analyzed classname use this to sign each tweet you analyze with the name of your class.
     * i.e analyzedTweet.setAnalysisAuthorClassName(this.getClass().getName())
     * @return
     */
    String getAnalyzerClassName();
}
