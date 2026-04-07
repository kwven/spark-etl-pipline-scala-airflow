package cleaners

import org.apache.spark.sql.functions.{col, expr, regexp_replace}
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{Dataset, Row, SparkSession}

class TweetsCleaner(sparkSession: SparkSession) {

  def cleanAllTweets(df: Dataset[Row]): Dataset[Row] ={
    df.withColumn("hashtags", regexp_replace(col("hashtags"), "[']", ""))
      .withColumn("hashtags", regexp_replace(col("hashtags"), "\\[", ""))
      .withColumn("hashtags", regexp_replace(col("hashtags"), "\\]", ""))
      .withColumn("hashtags", expr("filter(transform(split(hashtags, ','), x -> trim(x)), x -> x <> '')"))
      .withColumn("date", col("date").cast(DataTypes.DateType))
      .withColumn("user_created", col("user_created").cast(DataTypes.DateType))
      .withColumn("user_favourites", col("user_favourites").cast(DataTypes.LongType))
      .withColumn("user_friends", col("user_friends").cast(DataTypes.LongType))
      .withColumn("user_followers", col("user_followers").cast(DataTypes.LongType))
  }

}
