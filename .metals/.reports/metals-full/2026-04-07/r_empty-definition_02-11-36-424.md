error id: file://<WORKSPACE>/src/main/scala/cleaners/ForbesCleaner.scala:withColumn.
file://<WORKSPACE>/src/main/scala/cleaners/ForbesCleaner.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -sparkSession/implicits.
	 -sparkSession/implicits#
	 -sparkSession/implicits().
	 -scala/Predef.
	 -scala/Predef#
	 -scala/Predef().
offset: 771
uri: file://<WORKSPACE>/src/main/scala/cleaners/ForbesCleaner.scala
text:
```scala
package cleaners

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.DataTypes
import org.apache.spark.sql.{Dataset, Row, SparkSession}

class ForbesCleaner(sparkSession: SparkSession) {

  import sparkSession.implicits._

  def cleanForbes(df: Dataset[Row]): Dataset[Row] = {
    df.withColumn("rank", col("rank").cast(DataTypes.IntegerType))
      .filter($"rank".isNotNull)
      .withColumn("age", col("age").cast(DataTypes.IntegerType))
      .withColumn("finalWorth", col("finalWorth").cast(DataTypes.IntegerType))
      .withColumn("year", col("year").cast(DataTypes.IntegerType))
      .withColumn("month", col("month").cast(DataTypes.IntegerType))
      .withColumn("birthDate", col("birthDate").cast(DataTypes.DateType))
      .withColu@@mn("philanthropyScore", col("philanthropyScore").cast(DataTypes.FloatType))
      .na.fill("N/A")
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 