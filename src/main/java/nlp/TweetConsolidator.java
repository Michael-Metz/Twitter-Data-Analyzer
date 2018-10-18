package nlp;

import nlp.tweetanalyzers.StandfordNLPTweetAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TweetConsolidator {
    ArrayList<AnalyzedTweet> stanford = null, apache = null;
    int[] sentimentConsensus;

    int posCount, negCount, neutralCount, undefindedCount;
    public TweetConsolidator(List<AnalyzedTweet> stanford, List<AnalyzedTweet> apache) {
        this.stanford = new ArrayList<>(stanford.size());
        this.apache = new ArrayList<>(apache.size());

        //copy them over to new lists because were going to manipulate them
        for(AnalyzedTweet tweet : stanford)
            this.stanford.add(tweet);
        for(AnalyzedTweet tweet : apache)
            this.apache.add(tweet);

        //sort em so we can do fast search
        Collections.sort(stanford);
        Collections.sort(apache);
    }

    /**
     * Will arrange tweets such that both stanford and apache will have the same number of tweets
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
    public void formConsensus(){
        sentimentConsensus = new int[stanford.size()];
        for(int i = 0; i < stanford.size(); i++){
            int stanfordSentiment = stanford.get(i).getOverallSentiment();
            int apacheSentiment = apache.get(i).getOverallSentiment();

            if(stanfordSentiment == AnalyzedTweet.VERY_POSITIVE_SENTIMENT)
                stanfordSentiment = AnalyzedTweet.POSITIVE_SENTIMENT;
            else if(stanfordSentiment == AnalyzedTweet.VERY_NEGATIVE_SENTIMENT)
                stanfordSentiment = AnalyzedTweet.NEGATIVE_SENTIMENT;

            if(apacheSentiment == AnalyzedTweet.VERY_POSITIVE_SENTIMENT)
                apacheSentiment = AnalyzedTweet.POSITIVE_SENTIMENT;
            else if(apacheSentiment == AnalyzedTweet.VERY_NEGATIVE_SENTIMENT)
                apacheSentiment = AnalyzedTweet.NEGATIVE_SENTIMENT;

            if(apacheSentiment == stanfordSentiment){
                switch (apacheSentiment){
                    case 1:
                        sentimentConsensus[i] = 1;
                        posCount++;
                        break;
                    case -1:
                        sentimentConsensus[i] = -1;
                        negCount++;
                        break;
                    case 0:
                        sentimentConsensus[i] = 0;
                        neutralCount++;
                        break;
                }
            }else {
                sentimentConsensus[i] = Byte.MIN_VALUE;
                undefindedCount++;
            }
        }
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


        System.out.println("Reading serialized");

        try {
            stanford = IOTweetHelper.readSerializedAnalyzedTweets(stanfordPath);
            apache = IOTweetHelper.readSerializedAnalyzedTweets(apachePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Generating object");
        TweetConsolidator tc = new TweetConsolidator(stanford, apache);
        System.out.println("Organizing tweets");

        tc.organizeTweets();
        System.out.println("forming consensus tweets");

        tc.formConsensus();
        System.out.println("Writing tweets");
        System.out.printf("%s memory\n\n",MemoryUsageUTIL.getPercentageUsedFormatted());
        IOTweetHelper.writeTweetConsolidatorToCSV(tc,"merged.csv");

        System.out.printf("%d tweets compared,\n\n",tc.apache.size());
        System.out.printf("pos: %d %.2f%%\n",tc.posCount,(tc.posCount/(tc.apache.size()*1.0)*100));
        System.out.printf("neg: %d %.2f%%\n",tc.negCount,(tc.negCount/(tc.apache.size()*1.0)*100));
        System.out.printf("neu: %d %.2f%%\n",tc.neutralCount,(tc.neutralCount/(tc.apache.size()*1.0))*100);
        System.out.printf("udf: %d %.2f%%\n",tc.undefindedCount,(tc.undefindedCount/(tc.apache.size()*1.0))*100);


//        List<Tweet> pos = tc.findPositiveTweets();
//        List<Tweet> neg = tc.findNegativeTweets();
//        List<Tweet> undefined = tc.findUndefinedTweets();

//        IOUtil.writeTweetsToCSVFile(AnalyzedTweet.POSITIVE_SENTIMENT, pos, "positive.csv");
//        IOUtil.writeTweetsToCSVFile(AnalyzedTweet.NEGATIVE_SENTIMENT, neg, "negative.csv");
//        IOUtil.writeTweetsToCSVFile(AnalyzedTweet.NEUTRAL_SENTIMENT, undefined, "undefined.csv");


    }
}
