package loaders

import config.JdbcConfig
import org.apache.spark.sql.{Dataset, Row, SparkSession}

class TransactionLoader(sparkSession: SparkSession, jdbcConfig: JdbcConfig = JdbcConfig.default()) {

  def loadTransactions(): Dataset[Row] = sparkSession.read
    .format("jdbc")
    .option("driver", "org.postgresql.Driver")
    .option("url", jdbcConfig.url)
    .option("dbtable", "\"Transaction\"")
    .option("user", jdbcConfig.user)
    .option("password", jdbcConfig.password)
    .load()

}
