package nlp.tweetanalyzers;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import nlp.*;
import opennlp.tools.doccat.*;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class ApacheNLPTweetAnalyzer implements OverallSentimentAnalyzer, PercentageSentimentAnalyzer, SentenceSentimentAnalyzer {

    private static ApacheNLPTweetAnalyzer apacheNLPTweetAnalyzer = null;

    private DoccatModel doccatmodel;
    private DocumentCategorizer documentCategorizer;
    private TokenizerModel tokenizermodel;
    private Tokenizer tokenizer;
    private SentenceModel sentenceModel;
    private SentenceDetectorME sentenceDetectorME;

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
    private ApacheNLPTweetAnalyzer() {
        //do any nlp library set up here
        //read in the bin model
        //
        try {
            InputStream modelIn = new FileInputStream("OpenNLP_models/TrainingOutput.bin");
            doccatmodel = new DoccatModel(modelIn);
            documentCategorizer= new DocumentCategorizerME(doccatmodel);
            modelIn = new FileInputStream("OpenNLP_models/en-token.bin");
            tokenizermodel= new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(tokenizermodel);
            modelIn = new FileInputStream("/Users/benjaminmussell/IdeaProjects/Twitter-Data-Analyzer/OpenNLP_models/en-sent.bin");
            sentenceModel = new SentenceModel(modelIn);
            sentenceDetectorME = new SentenceDetectorME(sentenceModel);

        }
        catch(Exception e) {
            e.printStackTrace();
        }

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
        AnalyzedTweet analyzedTweet= new AnalyzedTweet(sanitizedTweet);

        analyzedTweet.setSentences(sentenceDetectorME.sentDetect(analyzedTweet.getText()));
        int len= analyzedTweet.getSentences().length;
        int[] temp= new int[len];
        String[] sentence= analyzedTweet.getSentences();
        String[] tokens;
        double[] outcomes=null;
        double[] posProbs= new double[len];
        double[] negProbs= new double[len];
        for(int i=0; i<len; i++){
            tokens= tokenizer.tokenize(sentence[i]);
            outcomes=documentCategorizer.categorize(tokens);
            posProbs[i]=outcomes[1];
            negProbs[i]=outcomes[0];
            if(outcomes[0]-outcomes[1]>0.1){
                temp[i]=-1;
            }
            else if(outcomes[1]-outcomes[0]>0.1){
                temp[i]=1;
            }
            else{
                temp[i]=0;
            }
        }
        double sum1=0;
        double sum2=0;
        for(int i=0; i<len; i++){
            sum1=sum1+negProbs[i];
            sum2=sum2+posProbs[i];
        }
        analyzedTweet.setSentenceSentiments(temp);
        int overallSentiment = analyzeOverallTweetSentiment(analyzedTweet);
        analyzedTweet.setOverallSentiment(overallSentiment);
        analyzedTweet.setOverallNegativeSentimentPercent(sum1/len);
        analyzedTweet.setOverallPositiveSentimentPercent(sum2/len);
        analyzedTweet.setAnalysisAuthorClassName(getAnalyzerClassName());
        return analyzedTweet;
    }

    /**
     * @param sanitizedTweets
     * @return
     * @SEE AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet);
     */
    @Override
    public List<AnalyzedTweet> analyzeSanitizedTweetsSentiment(List<? extends SanitizedTweet> sanitizedTweets) {
        List<AnalyzedTweet> analyzedTweets = new LinkedList<>();
        int numTweets = sanitizedTweets.size();

        for (int i = 0; i < numTweets; i++) {
            SanitizedTweet sanitizedTweet = sanitizedTweets.get(i);
            AnalyzedTweet analyzedTweet = analyzeSanitizedTweetSentiment(sanitizedTweet);
            if (i % 500 == 0)
                System.out.printf("%s analyzing %d out of %d   %.2f%% done\n", getAnalyzerClassName(),i, numTweets, (((i * 1.0) / numTweets) * 100));
            analyzedTweets.add(analyzedTweet);
        }
        return analyzedTweets;
    }

    public int analyzeOverallTweetSentiment(SanitizedTweet sanitizedTweet) {
        AnalyzedTweet analyzedTweet = null;

        boolean needToRunThroughPipeline = true;
        if(sanitizedTweet instanceof AnalyzedTweet)
        {
            analyzedTweet = (AnalyzedTweet) sanitizedTweet;

            //determine if have not  analyzed this with the pipeline
            if(analyzedTweet.getSentenceSentiments() != null){
                needToRunThroughPipeline = false;
            }
        }

        if(needToRunThroughPipeline) {
            analyzedTweet = analyzeSanitizedTweetSentiment(sanitizedTweet);
            System.out.println("running through pipeline again....");
        }

        int[] sentenceSentiments = analyzedTweet.getSentenceSentiments();
        int sent = 0;

        int totalPositive = 0;
        int totalVeryPositive = 0;
        int totalNegative = 0;
        int totalVeryNegative = 0;
        int totalNeutral = 0;
        for(int i = 0; i < sentenceSentiments.length; i++)
        {
            switch (sentenceSentiments[i])
            {
                case AnalyzedTweet.VERY_NEGATIVE_SENTIMENT :
                    totalVeryNegative++;
                    break;
                case AnalyzedTweet.NEGATIVE_SENTIMENT :
                    totalNegative++;
                    break;
                case AnalyzedTweet.NEUTRAL_SENTIMENT:
                    totalNeutral++;
                    break;
                case AnalyzedTweet.POSITIVE_SENTIMENT:
                    totalPositive++;
                    break;
                case AnalyzedTweet.VERY_POSITIVE_SENTIMENT:
                    totalVeryPositive++;
                    break;
            }
        }


        int sumNeg = (totalNegative + totalVeryNegative);
        int sumPos = (totalPositive + totalVeryPositive);
        if(1 <= sumPos)
        {
            sent = AnalyzedTweet.VERY_POSITIVE_SENTIMENT;//has only positive so strong postive
            if(1 <= totalNeutral)//has positive and neutral so semi positive
                sent = AnalyzedTweet.POSITIVE_SENTIMENT;
            if(1 <= sumNeg)//has positive and negative sentences so contradiction
                sent = AnalyzedTweet.UNDEFINED_SENTIMENT;
        }else if (1 <= sumNeg)
        {
            sent = AnalyzedTweet.VERY_NEGATIVE_SENTIMENT;
            if(1 <= totalNeutral)
                sent = AnalyzedTweet.NEGATIVE_SENTIMENT;
            if(1 <= sumPos)
                sent = AnalyzedTweet.UNDEFINED_SENTIMENT;
        }else {
            sent = AnalyzedTweet.NEUTRAL_SENTIMENT; //all neutral
        }

        return sent;
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
