package outputs

import config.AppConfig
import org.apache.spark.sql.functions.{col, to_json}
import org.apache.spark.sql.types.{ArrayType, MapType, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SaveMode}

class DatasetOutputWriter(config: AppConfig) {

  def write(jobName: String, datasetName: String, df: Dataset[Row]): Unit = {
    config.csvOutputPath(jobName, datasetName).foreach { csvPath =>
      toCsvFriendly(df)
        .coalesce(1)
        .write
        .mode(SaveMode.Overwrite)
        .option("header", "true")
        .csv(csvPath)
    }

    df.write
      .mode(SaveMode.Overwrite)
      .parquet(config.parquetOutputPath(jobName, datasetName))
  }

  def writeParquetOnly(jobName: String, datasetName: String, df: Dataset[Row]): Unit =
    df.write
      .mode(SaveMode.Overwrite)
      .parquet(config.parquetOutputPath(jobName, datasetName))

  def writeCsvOnly(jobName: String, datasetName: String, df: Dataset[Row]): Unit =
    config.csvOutputPath(jobName, datasetName).foreach { csvPath =>
      toCsvFriendly(df)
        .coalesce(1)
        .write
        .mode(SaveMode.Overwrite)
        .option("header", "true")
        .csv(csvPath)
    }

  private def toCsvFriendly(df: Dataset[Row]): DataFrame =
    df.select(df.schema.fields.map { field =>
      field.dataType match {
        case _: ArrayType | _: MapType | _: StructType => to_json(col(field.name)).as(field.name)
        case _ => col(field.name)
      }
    }: _*)
}
