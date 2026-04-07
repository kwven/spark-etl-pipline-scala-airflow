error id: file://<WORKSPACE>/src/main/scala/Main.scala:SparkSession.
file://<WORKSPACE>/src/main/scala/Main.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -org/apache/spark/sql/SparkSession.
	 -SparkSession.
	 -scala/Predef.SparkSession.
offset: 373
uri: file://<WORKSPACE>/src/main/scala/Main.scala
text:
```scala
import config.AppConfig
import jobs.{ForbesJob, TransactionJob, TweetsJob, TweetsUserJob}
import org.apache.spark.sql.SparkSession
import outputs.DatasetOutputWriter


object Main {

  def main(args: Array[String]): Unit = {
    if (args.contains("--help")) {
      println(AppConfig.usage)
      return
    }

    val config = AppConfig.load(args)
    val spark = SparkSes@@sion.builder()
      .appName("project-spark-scala-etl")
      .master("local[*]")
      .config("spark.sql.parquet.datetimeRebaseModeInWrite", "LEGACY")
      .config("spark.sql.parquet.int96RebaseModeInWrite", "LEGACY")
      .config("spark.sql.shuffle.partitions", "4")
      .config("spark.ui.showConsoleProgress", "true")
      .config("spark.hadoop.fs.defaultFS", config.hdfs.baseUri)
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    val outputWriter = new DatasetOutputWriter(config)

    try {
      println(s"Running pipelines: ${config.pipelines.toSeq.sorted.mkString(", ")}")
      println(s"Input directory: ${config.paths.inputDir}")
      println(s"Output directory: ${config.paths.outputDir}")

      if (config.pipelines.contains("transactions")) {
        new TransactionJob(spark, config, outputWriter).run()
      }

      if (config.pipelines.contains("tweets")) {
        new TweetsJob(spark, config, outputWriter).run()
      }

      if (config.pipelines.contains("forbes")) {
        new ForbesJob(spark, config, outputWriter).run()
      }

      if (config.pipelines.contains("tweets-user")) {
        new TweetsUserJob(spark, config, outputWriter).run()
      }
    } finally {
      spark.stop()
    }
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 