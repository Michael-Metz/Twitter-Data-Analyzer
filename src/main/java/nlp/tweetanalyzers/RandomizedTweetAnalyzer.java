package nlp.tweetanalyzers;


import nlp.AnalyzedTweet;
import nlp.SanitizedTweet;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomizedTweetAnalyzer implements OverallSentimentAnalyzer, PercentageSentimentAnalyzer {

    Random random;

    public RandomizedTweetAnalyzer(long seed){
        random = new Random(seed);
    }
    public RandomizedTweetAnalyzer(){
        this(System.currentTimeMillis());
    }

    @Override
    public AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet) {
        AnalyzedTweet analyzedTweet = new AnalyzedTweet(sanitizedTweet);

        //set overall sentiment percentages
        double percentPositive = random.nextDouble();
        double percentNegative = 1.0 - percentPositive;
        analyzedTweet.setOverallPositiveSentimentPercent(percentPositive);
        analyzedTweet.setOverallNegativeSentimentPercent(percentNegative);

        //determine overall sentiment
        int decision = AnalyzedTweet.NEUTRAL_SENTIMENT;
        if( percentNegative < percentPositive){
            decision = AnalyzedTweet.POSITIVE_SENTIMENT;
            if(0.90 <= percentPositive);
                decision = AnalyzedTweet.VERY_POSITIVE_SENTIMENT;
        }
        else if(percentPositive < percentNegative){
            decision = AnalyzedTweet.NEGATIVE_SENTIMENT;
            if(0.90 <= percentPositive);
                decision = AnalyzedTweet.VERY_NEGATIVE_SENTIMENT;
        }
        analyzedTweet.setOverallSentiment(decision);

        //sign with class nameSSSS
        analyzedTweet.setAnalysisAuthorClassName(getAnalyzerClassName());
        return analyzedTweet;
    }

    @Override
    public List<AnalyzedTweet> analyzeSanitizedTweetsSentiment(List<SanitizedTweet> sanitizedTweets) {
        List<AnalyzedTweet> analyzedTweets = new LinkedList<>();
        for(SanitizedTweet sanitizedTweet : sanitizedTweets){
            AnalyzedTweet analyzedTweet = analyzeSanitizedTweetSentiment(sanitizedTweet);
            analyzedTweets.add(analyzedTweet);
        }
        return analyzedTweets;
    }

    /**
     * Returns the name of the analyzed classname use this to sign each tweet you analyze with the name of your class.
     * i.e analyzedTweet.setAnalysisAuthorClassName(this.getClass().getName())
     *
     * @return
     */
    @Override
    public String getAnalyzerClassName() {
        return this.getClass().getName();
    }

}

