package nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TweetConsolidator {
    ArrayList<AnalyzedTweet> stanford = null, apache = null;

    public TweetConsolidator(List<AnalyzedTweet> stanford, List<AnalyzedTweet> apache) {
        this.stanford = new ArrayList<>(stanford.size());
        this.apache = new ArrayList<>(apache.size());

        //copy them over to new lists because were goint to manipulate them
        for(AnalyzedTweet tweet : stanford)
            this.stanford.add(tweet);
        for(AnalyzedTweet tweet : apache)
            this.apache.add(tweet);

        //sort em so we can do fast search
        Collections.sort(stanford);
        Collections.sort(apache);
    }

    /**
     * Will arrange tweets such that both stanford and apachi will have the same number of tweets
     * in other words stanford and apache will have the exact same tweets in the exact same order
     * Except they will have different sentiment decisions for each tweet
     */
    public void organizeTweets(){
        ArrayList<AnalyzedTweet> stanfordTwo = new ArrayList(stanford.size());
        ArrayList<AnalyzedTweet> apacheTwo = new ArrayList(apache.size());

        //the equals and compare method are inherited from tweet so we use those to compare analyzed tweets
        for (int i = 0; i < stanford.size(); i++)
        {
            AnalyzedTweet stanfordTweet = stanford.get(i);
            int apacheIndex = apache.indexOf(stanfordTweet);
            //#TODO consider adding a processed indexes map to avoid duplicates. this is a overkill but will solve edge cases
            if(apacheIndex != -1){
                stanfordTwo.add(stanfordTweet);
                apacheTwo.add(apache.get(apacheIndex));
            }
        }
        stanford = stanfordTwo;
        apache = apacheTwo;
    }

    /**
     * call organizeTweets first
     * @return
     */
    public List<Tweet> findPositiveTweets(){
        List<Tweet> tweets = new LinkedList<>();
        for(int i = 0; i < stanford.size(); i++)
        {
            int ss = stanford.get(i).getOverallSentiment();
            int as = apache.get(i).getOverallSentiment();
            if(as == ss && ss == AnalyzedTweet.POSITIVE_SENTIMENT)
            {
                   tweets.add(stanford.get(i));
            }
        }
        return tweets;
    }

    /**
     * call organizeTweets first
     * @return
     */
    public List<Tweet> findNegativeTweets(){
        List<Tweet> tweets = new LinkedList<>();
        for(int i = 0; i < stanford.size(); i++)
        {
            int ss = stanford.get(i).getOverallSentiment();
            int as = apache.get(i).getOverallSentiment();
            if(as == ss && ss == AnalyzedTweet.NEGATIVE_SENTIMENT)
            {
                tweets.add(stanford.get(i));
            }
        }
        return tweets;
    }

    /**
     * call organizeTweets first
     * @return
     */
    public List<Tweet> findUndefinedTweets(){
        List<Tweet> tweets = new LinkedList<>();
        for(int i = 0; i < stanford.size(); i++)
        {
            int ss = stanford.get(i).getOverallSentiment();
            int as = apache.get(i).getOverallSentiment();
            if(as != ss)
            {
                tweets.add(stanford.get(i));
            }
        }
        return tweets;
    }

    public static void main(String args[]){
        String stanfordPath = args[0];
        String apachePath = args[1];
        List<AnalyzedTweet> apache = null, stanford = null;


//        try {
//            stanford = IOUtil.readSerializedTweets(stanfordPath);
//            apache = IOUtil.readSerializedTweets(apachePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        TweetConsolidator tc = new TweetConsolidator(stanford, apache);
        tc.organizeTweets();

        List<Tweet> pos = tc.findPositiveTweets();
        List<Tweet> neg = tc.findNegativeTweets();
        List<Tweet> undefined = tc.findUndefinedTweets();

//        IOUtil.writeTweetsToCSVFile(AnalyzedTweet.POSITIVE_SENTIMENT, pos, "positive.csv");
//        IOUtil.writeTweetsToCSVFile(AnalyzedTweet.NEGATIVE_SENTIMENT, neg, "negative.csv");
//        IOUtil.writeTweetsToCSVFile(AnalyzedTweet.NEUTRAL_SENTIMENT, undefined, "undefined.csv");


    }
}
