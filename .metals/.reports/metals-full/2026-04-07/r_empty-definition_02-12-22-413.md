error id: file://<WORKSPACE>/src/main/scala/cleaners/TransactionCleaner.scala:functions.
file://<WORKSPACE>/src/main/scala/cleaners/TransactionCleaner.scala
empty definition using pc, found symbol in pc: functions.
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -org/apache/spark/sql/functions.
	 -scala/Predef.org.apache.spark.sql.functions.
offset: 49
uri: file://<WORKSPACE>/src/main/scala/cleaners/TransactionCleaner.scala
text:
```scala
package cleaners

import org.apache.spark.sql.fun@@ctions.{col, trim}
import org.apache.spark.sql.{Dataset, Row, SparkSession}

class TransactionCleaner(sparkSession: SparkSession) {

  def cleanAllTransactions(df: Dataset[Row]): Dataset[Row] ={
    df.withColumn("ID", trim(col("ID")))
      .withColumn("first_name", trim(col("first_name")))
      .withColumn("last_name", trim(col("last_name")))
      .withColumn("email", trim(col("email")))
      .withColumn("gender", trim(col("gender")))
      .withColumn("Currency", trim(col("Currency")))
      .withColumn("Product", trim(col("Product")))
      .withColumn("Country", trim(col("Country")))
      .withColumn("Postal_Code", trim(col("Postal_Code")))
      .withColumn("Street_Address", trim(col("Street_Address")))
  }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: functions.