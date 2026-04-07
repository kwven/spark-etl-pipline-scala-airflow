package Ingestions

import config.JdbcConfig
import org.apache.spark.sql.{Dataset, Row, SaveMode}

class TransactionIngests(jdbcConfig: JdbcConfig = JdbcConfig.default()) {

  private def writeTable(df: Dataset[Row], tableName: String): Unit = {
    df.write
      .mode(SaveMode.Overwrite)
      .format("jdbc")
      .option("driver", "org.postgresql.Driver")
      .option("url", jdbcConfig.url)
      .option("dbtable", tableName)
      .option("user", jdbcConfig.user)
      .option("password", jdbcConfig.password)
      .save()
  }

  def ingestTransactionUSA(df: Dataset[Row]): Unit =
    writeTable(df, "\"Transaction_USA\"")

  def ingestTransactionFrance(df: Dataset[Row]): Unit =
    writeTable(df, "\"Transaction_France\"")

  def ingestTransactionChina(df: Dataset[Row]): Unit =
    writeTable(df, "\"Transaction_China\"")

  def ingestTransactionPoland(df: Dataset[Row]): Unit =
    writeTable(df, "\"Transaction_Poland\"")

}
