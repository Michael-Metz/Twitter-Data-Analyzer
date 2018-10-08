# Twitter sentiment analysis

#TwitterPreProcessor

This program will read a twitter csv file and output it to "sanitized.csv"

* remove username mentions @<username>
* Remove hashtags #<hashtag>
* remove web url links
* remove repeated publication "hello?????" -> "hello?"

The input twitter data file is read as a csv with columns `A-Z`

The output file outputs the exact same `A-Z` and then add addition columns `AA - AE`

We call a tweet with columns `A-S` a `Tweet` which is represented in object form by the `Tweet` class
We call a tweet with columns `A-AE` a `Sanitized Tweet` which is represented in object form by the `SanitizedTweet` class


|  AA |  AB | AC  |  AD | AE  | 
|---|---|---|---|---|
| Sanitized  |  User names count from text |  Web url count from text | Hash tag count from text  | Repeated punctuation count from text  
|  String | Integer  | Integer  |  Integer |   Integer 
#TweetAnalyzerDriver

* reads a sanitized csv file 
* lets you choose which TweetSentimentAnalyzer implementation to use
* lets you read/write serialized Analyzed Tweet List so you don't have to rerun analyzers
* lets you write analyzed tweets to csv

We call a tweet with columns `A-AI` a `Analyzed Tweet` which is represented in object form by the `AnalyzedTweet` class

| AF |  AG | AH  | AI |
---|---|---|---|
| Analysis author class name | Overall sentiment | Overall positive sentiment percent | Overall negative sentiment percent
| String| Integer| double| double |

#tweetanalyzers

this package holds all the logic for the analysis done on the tweets

Interfaces
* TweetSentimentAnalyzer

Marker Interfaces
* OverallSentimentAnalyzer : must analyze the overall sanitized text sentiment
* PercentageSentimentAnalyzer : must analyze the  % neg & % pos of overall sanitized text sentiment
* SentenceSentimentAnalyzer : must analyze the sentiment of each sentence of sanitized text

To create a new analyzer you must implement at least 1 marker interface these marker interfaces extend
TweetSentimentAnalyzer so you must implement the following methods

```java
    AnalyzedTweet analyzeSanitizedTweetSentiment(SanitizedTweet sanitizedTweet);
    List<AnalyzedTweet> analyzeSanitizedTweetsSentiment(List<SanitizedTweet> sanitizedTweets);
    String getAnalyzerClassName();
```
These marker interfaces indicate what analysis must be done with the `analyzeSanitizedTweetSentiment`

The current concrete implementations are

* StanfordNLPTweetAnalyzer
* RandomizedSentimentAnalyzer
* AoacheNLPTweetAnalyzer (in progress)


