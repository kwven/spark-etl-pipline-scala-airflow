package jobs

import analysers.TweetsUserAnalyzer
import cleaners.TweetsUserCleaner
import config.AppConfig
import loaders.TweetsUserLoader
import org.apache.spark.sql.SparkSession
import outputs.DatasetOutputWriter

class TweetsUserJob(spark: SparkSession, config: AppConfig, outputWriter: DatasetOutputWriter) {

  def run(): Unit = {
    val loader = new TweetsUserLoader(spark, config.inputPath)
    val cleaner = new TweetsUserCleaner(spark)
    val analyzer = new TweetsUserAnalyzer(spark)

    val raw = loader.loadTwitter().cache()
    val cleaned = cleaner.cleanTweetsUser(raw).cache()
    val wordCount = analyzer.countWordsInTextColumn(cleaned)
    val userPostCount = analyzer.countUserPosts(cleaned)
    val topLikedPosts = analyzer.top10LikedPosts(cleaned)
    val topRetweetedPosts = analyzer.top10RetweetedPost(cleaned)

    outputWriter.write("tweets-user", "cleaned_tweets_user", cleaned)
    outputWriter.write("tweets-user", "word_count", wordCount)
    outputWriter.write("tweets-user", "user_post_count", userPostCount)
    outputWriter.write("tweets-user", "top_10_liked_posts", topLikedPosts)
    outputWriter.write("tweets-user", "top_10_retweeted_posts", topRetweetedPosts)

    println("Tweets user pipeline completed.")
  }
}
