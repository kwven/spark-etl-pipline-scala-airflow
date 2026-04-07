package loaders

import config.ProjectPaths
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions.lit

object TweetsUserLoader {
  val TWITTER_LABEL: String = "tweets"
}

class TweetsUserLoader(sparkSession: SparkSession, inputPath: String => String = ProjectPaths.default().inputFile) {
  def loadTwitter(): Dataset[Row] = sparkSession.read
    .option("header", "true")
    .csv(inputPath("twitter_dataset.csv"))
    .withColumn("category", lit(TweetsUserLoader.TWITTER_LABEL))
}
