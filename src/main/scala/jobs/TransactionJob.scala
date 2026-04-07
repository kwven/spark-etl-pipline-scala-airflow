package jobs

import Ingestions.TransactionIngests
import analysers.TransactionAnalyzer
import cleaners.TransactionCleaner
import config.AppConfig
import loaders.TransactionLoader
import org.apache.spark.sql.SparkSession
import outputs.DatasetOutputWriter

class TransactionJob(spark: SparkSession, config: AppConfig, outputWriter: DatasetOutputWriter) {

  def run(): Unit = {
    val loader = new TransactionLoader(spark, config.jdbc)
    val cleaner = new TransactionCleaner(spark)
    val analyzer = new TransactionAnalyzer(spark)
    val ingestor = new TransactionIngests(config.jdbc)

    val raw = loader.loadTransactions().cache()
    val cleaned = cleaner.cleanAllTransactions(raw).cache()
    val usa = analyzer.filterClientsUSA(cleaned)
    val france = analyzer.filterClientsFrance(cleaned)
    val china = analyzer.filterClientsChina(cleaned)
    val poland = analyzer.filterClientsPoland(cleaned)
    val topCountries = analyzer.filterTop10ClientsCount(cleaned)

    outputWriter.write("transactions", "cleaned_transactions", cleaned)
    outputWriter.write("transactions", "usa", usa)
    outputWriter.write("transactions", "france", france)
    outputWriter.write("transactions", "china", china)
    outputWriter.write("transactions", "poland", poland)
    outputWriter.write("transactions", "top_10_countries", topCountries)

    if (config.writeTransactionsToDatabase) {
      ingestor.ingestTransactionUSA(usa)
      ingestor.ingestTransactionFrance(france)
      ingestor.ingestTransactionChina(china)
      ingestor.ingestTransactionPoland(poland)
    }

    println("Transactions pipeline completed.")
  }
}
