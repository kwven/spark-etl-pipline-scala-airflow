package jobs

import analysers.{TweetsAnalyzer, TweetsSearch}
import cleaners.TweetsCleaner
import config.AppConfig
import loaders.TweetsLoader
import org.apache.spark.sql.SparkSession
import outputs.DatasetOutputWriter

class TweetsJob(spark: SparkSession, config: AppConfig, outputWriter: DatasetOutputWriter) {

  def run(): Unit = {
    val loader = new TweetsLoader(spark, config.inputPath)
    val cleaner = new TweetsCleaner(spark)
    val search = new TweetsSearch(spark)
    val analyzer = new TweetsAnalyzer(spark)

    import search._

    val raw = loader.loadAllTweets().cache()
    val cleaned = cleaner.cleanAllTweets(raw).cache()
    val trumpTweetsInUs = cleaned.transform(searchByKeyWord("Trump")).transform(onlyInLocation("United States"))
    val sourceCount = analyzer.calculateSourceCount(trumpTweetsInUs)
    val hashtagCount = analyzer.calculateHashtags(trumpTweetsInUs)

    outputWriter.write("tweets", "cleaned_tweets", cleaned)
    outputWriter.write("tweets", "trump_tweets_us", trumpTweetsInUs)
    outputWriter.write("tweets", "source_count", sourceCount)
    outputWriter.write("tweets", "hashtag_count", hashtagCount)

    println("Tweets pipeline completed.")
  }
}
