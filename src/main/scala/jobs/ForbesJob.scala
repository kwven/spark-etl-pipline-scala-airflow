package jobs

import analysers.ForbesAnalyzer
import cleaners.ForbesCleaner
import config.AppConfig
import loaders.ForbesLoader
import org.apache.spark.sql.SparkSession
import outputs.DatasetOutputWriter

class ForbesJob(spark: SparkSession, config: AppConfig, outputWriter: DatasetOutputWriter) {

  def run(): Unit = {
    val loader = new ForbesLoader(spark, config.inputPath)
    val cleaner = new ForbesCleaner(spark)
    val analyzer = new ForbesAnalyzer(spark)

    val raw = loader.loadForbes().cache()
    val cleaned = cleaner.cleanForbes(raw).cache()
    val underFifty = analyzer.filterUnderFiftyAgeForbes(cleaned)
    val countryCounts = analyzer.countCountriesForbes(cleaned)
    val topSelfMade = analyzer.top10SelfMade(cleaned)

    outputWriter.write("forbes", "cleaned_forbes", cleaned)
    outputWriter.write("forbes", "under_fifty", underFifty)
    outputWriter.write("forbes", "country_counts", countryCounts)
    outputWriter.write("forbes", "top_10_self_made", topSelfMade)

    println("Forbes pipeline completed.")
  }
}
