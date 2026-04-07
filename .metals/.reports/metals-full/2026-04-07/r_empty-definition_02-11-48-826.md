error id: file://<WORKSPACE>/src/main/scala/cleaners/TweetsCleaner.scala:
file://<WORKSPACE>/src/main/scala/cleaners/TweetsCleaner.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -org/apache/spark/sql/functions/regexp_replace.
	 -org/apache/spark/sql/functions/regexp_replace#
	 -org/apache/spark/sql/functions/regexp_replace().
	 -regexp_replace.
	 -regexp_replace#
	 -regexp_replace().
	 -scala/Predef.regexp_replace.
	 -scala/Predef.regexp_replace#
	 -scala/Predef.regexp_replace().
offset: 472
uri: file://<WORKSPACE>/src/main/scala/cleaners/TweetsCleaner.scala
text:
```scala
package cleaners

import org.apache.spark.sql.functions.{col, expr, regexp_replace}
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{Dataset, Row, SparkSession}

class TweetsCleaner(sparkSession: SparkSession) {

  def cleanAllTweets(df: Dataset[Row]): Dataset[Row] ={
    df.withColumn("hashtags", regexp_replace(col("hashtags"), "[']", ""))
      .withColumn("hashtags", regexp_replace(col("hashtags"), "\\[", ""))
      .withColumn("hashtags", r@@egexp_replace(col("hashtags"), "\\]", ""))
      .withColumn("hashtags", expr("filter(transform(split(hashtags, ','), x -> trim(x)), x -> x <> '')"))
      .withColumn("date", col("date").cast(DataTypes.DateType))
      .withColumn("user_created", col("user_created").cast(DataTypes.DateType))
      .withColumn("user_favourites", col("user_favourites").cast(DataTypes.LongType))
      .withColumn("user_friends", col("user_friends").cast(DataTypes.LongType))
      .withColumn("user_followers", col("user_followers").cast(DataTypes.LongType))
  }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 