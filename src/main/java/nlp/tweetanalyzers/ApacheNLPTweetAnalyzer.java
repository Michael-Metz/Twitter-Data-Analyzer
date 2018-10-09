package nlp.tweetanalyzers;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import nlp.*;
import opennlp.tools.doccat.*;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

public class ApacheNLPTweetAnalyzer implements OverallSentimentAnalyzer, PercentageSentimentAnalyzer {

    private static ApacheNLPTweetAnalyzer apacheNLPTweetAnalyzer = null;

    private DoccatModel doccatmodel;
    private DocumentCategorizer documentCategorizer;
    private TokenizerModel tokenizermodel;
    private Tokenizer tokenizer;
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
            InputStream modelIn = new FileInputStream("/Users/benjaminmussell/IdeaProjects/Twitter-Data-Analyzer/OpenNLP_models/TrainingOutput.bin");
            doccatmodel = new DoccatModel(modelIn);
            documentCategorizer= new DocumentCategorizerME(doccatmodel);
            modelIn = new FileInputStream("/Users/benjaminmussell/IdeaProjects/Twitter-Data-Analyzer/OpenNLP_models/en-token.bin");
            tokenizermodel= new TokenizerModel(modelIn);
            tokenizer = new TokenizerME(tokenizermodel);

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

        String[] tokens= tokenizer.tokenize(analyzedTweet.getText());
        double[] outcomes=null;
        outcomes=documentCategorizer.categorize(tokens);

        analyzedTweet.setOverallNegativeSentimentPercent(outcomes[0]);
        analyzedTweet.setOverallPositiveSentimentPercent(outcomes[1]);

        if(outcomes[0]-outcomes[1]>0.1){
            analyzedTweet.setOverallSentiment(-1);
        }
        else if(outcomes[1]-outcomes[0]>0.1){
            analyzedTweet.setOverallSentiment(1);
        }
        else{
            analyzedTweet.setOverallSentiment(0);
        }
        analyzedTweet.setAnalysisAuthorClassName(getAnalyzerClassName());
        return analyzedTweet;
    }

    /**
     * @param sanitizedTweets
     * @return
     * @SEE AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet);
     */
    @Override
    public List<AnalyzedTweet> analyzeSanitizedTweetsSentiment(List<SanitizedTweet> sanitizedTweets) {
        List<AnalyzedTweet> analyzedTweets = new LinkedList<>();
        for(SanitizedTweet sanitizedTweet : sanitizedTweets){
            AnalyzedTweet analyzedTweet = analyzeSanitizedTweetSentiment(sanitizedTweet);
            System.out.println("ananlyzing " + analyzedTweet.getSanitizedText());
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
