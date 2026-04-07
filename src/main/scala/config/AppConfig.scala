package config

import java.nio.file.{Files, Path, Paths}

final case class ProjectPaths(inputDir: String, outputDir: String) {

  def inputFile(fileName: String): String =
    Paths.get(inputDir, fileName).toAbsolutePath.normalize().toString

  def csvOutput(jobName: String, datasetName: String): String =
    Paths.get(outputDir, "csv", jobName, datasetName).toAbsolutePath.normalize().toString

  def parquetOutput(jobName: String, datasetName: String): String =
    Paths.get(outputDir, "parquet", jobName, datasetName).toAbsolutePath.normalize().toString

  def ensureOutputDirectories(): Unit = {
    Files.createDirectories(Paths.get(outputDir, "csv"))
    Files.createDirectories(Paths.get(outputDir, "parquet"))
  }
}

object ProjectPaths {
  def default(projectRoot: Path = Paths.get(sys.props.getOrElse("user.dir", ".")).toAbsolutePath.normalize()): ProjectPaths =
    ProjectPaths(
      inputDir = projectRoot.resolve("data").resolve("input").toString,
      outputDir = projectRoot.resolve("data").resolve("output").toString
    )
}

final case class JdbcConfig(url: String, user: String, password: String)

object JdbcConfig {
  def default(): JdbcConfig =
    JdbcConfig(
      url = "jdbc:postgresql://localhost:5438/postgres",
      user = "postgres",
      password = "postgres"
    )
}

final case class HdfsConfig(
  enabled: Boolean,
  baseUri: String,
  rawInputDir: String,
  curatedParquetDir: String,
  curatedCsvDir: String
) {

  private def normalizedBaseUri: String =
    baseUri.stripSuffix("/")

  private def normalizePath(path: String): String =
    if (path.startsWith("/")) path else s"/$path"

  def rawInputFile(fileName: String): String =
    s"$normalizedBaseUri${normalizePath(rawInputDir)}/$fileName"

  def curatedParquetOutput(jobName: String, datasetName: String): String =
    s"$normalizedBaseUri${normalizePath(curatedParquetDir)}/$jobName/$datasetName"

  def curatedCsvOutput(jobName: String, datasetName: String): String =
    s"$normalizedBaseUri${normalizePath(curatedCsvDir)}/$jobName/$datasetName"
}

object HdfsConfig {
  def default(): HdfsConfig =
    HdfsConfig(
      enabled = false,
      baseUri = "hdfs://namenode:9000",
      rawInputDir = "/project-spark/raw/input",
      curatedParquetDir = "/project-spark/curated/parquet",
      curatedCsvDir = "/project-spark/curated/csv"
    )
}

final case class AppConfig(
  paths: ProjectPaths,
  jdbc: JdbcConfig,
  hdfs: HdfsConfig,
  pipelines: Set[String],
  writeTransactionsToDatabase: Boolean
) {

  def inputPath(fileName: String): String =
    if (hdfs.enabled) hdfs.rawInputFile(fileName) else paths.inputFile(fileName)

  def parquetOutputPath(jobName: String, datasetName: String): String =
    if (hdfs.enabled) hdfs.curatedParquetOutput(jobName, datasetName) else paths.parquetOutput(jobName, datasetName)

  def csvOutputPath(jobName: String, datasetName: String): Option[String] =
    if (hdfs.enabled) Some(hdfs.curatedCsvOutput(jobName, datasetName)) else Some(paths.csvOutput(jobName, datasetName))
}

object AppConfig {
  private val SupportedPipelines = Set("transactions", "tweets", "forbes", "tweets-user")

  val usage: String =
    """Usage:
      |  sbt "run --pipelines=all"
      |  sbt "run --pipelines=tweets,forbes,tweets-user"
      |
      |Options:
      |  --input-dir=/path/to/input
      |  --output-dir=/path/to/output
      |  --jdbc-url=jdbc:postgresql://localhost:5438/postgres
      |  --jdbc-user=postgres
      |  --jdbc-password=postgres
      |  --use-hdfs=true|false
      |  --hdfs-uri=hdfs://namenode:9000
      |  --hdfs-raw-dir=/project-spark/raw/input
      |  --hdfs-curated-parquet-dir=/project-spark/curated/parquet
      |  --hdfs-curated-csv-dir=/project-spark/curated/csv
      |  --pipelines=all|transactions,tweets,forbes,tweets-user
      |  --write-transactions-to-db=true|false
      |""".stripMargin

  def load(args: Array[String]): AppConfig = {
    val cliArgs = parseArgs(args)
    val defaultPaths = ProjectPaths.default()
    val defaultJdbc = JdbcConfig.default()
    val defaultHdfs = HdfsConfig.default()

    val paths = ProjectPaths(
      inputDir = resolve(cliArgs, "input-dir", "project.inputDir", "PROJECT_INPUT_DIR").getOrElse(defaultPaths.inputDir),
      outputDir = resolve(cliArgs, "output-dir", "project.outputDir", "PROJECT_OUTPUT_DIR").getOrElse(defaultPaths.outputDir)
    )

    val jdbc = JdbcConfig(
      url = resolve(cliArgs, "jdbc-url", "project.jdbcUrl", "PROJECT_JDBC_URL").getOrElse(defaultJdbc.url),
      user = resolve(cliArgs, "jdbc-user", "project.jdbcUser", "PROJECT_JDBC_USER").getOrElse(defaultJdbc.user),
      password = resolve(cliArgs, "jdbc-password", "project.jdbcPassword", "PROJECT_JDBC_PASSWORD").getOrElse(defaultJdbc.password)
    )

    val hdfs = HdfsConfig(
      enabled = resolve(cliArgs, "use-hdfs", "project.useHdfs", "PROJECT_USE_HDFS").map(_.toBoolean).getOrElse(defaultHdfs.enabled),
      baseUri = resolve(cliArgs, "hdfs-uri", "project.hdfsUri", "PROJECT_HDFS_URI").getOrElse(defaultHdfs.baseUri),
      rawInputDir = resolve(cliArgs, "hdfs-raw-dir", "project.hdfsRawDir", "PROJECT_HDFS_RAW_DIR").getOrElse(defaultHdfs.rawInputDir),
      curatedParquetDir = resolve(cliArgs, "hdfs-curated-parquet-dir", "project.hdfsCuratedParquetDir", "PROJECT_HDFS_CURATED_PARQUET_DIR")
        .orElse(resolve(cliArgs, "hdfs-curated-dir", "project.hdfsCuratedDir", "PROJECT_HDFS_CURATED_DIR"))
        .getOrElse(defaultHdfs.curatedParquetDir),
      curatedCsvDir = resolve(cliArgs, "hdfs-curated-csv-dir", "project.hdfsCuratedCsvDir", "PROJECT_HDFS_CURATED_CSV_DIR")
        .getOrElse(defaultHdfs.curatedCsvDir)
    )

    val pipelines = resolve(cliArgs, "pipelines", "project.pipelines", "PROJECT_PIPELINES")
      .map(parsePipelines)
      .getOrElse(SupportedPipelines)

    val writeTransactionsToDatabase = resolve(
      cliArgs,
      "write-transactions-to-db",
      "project.writeTransactionsToDb",
      "PROJECT_WRITE_TRANSACTIONS_TO_DB"
    ).map(_.toBoolean).getOrElse(true)

    if (!hdfs.enabled) {
      paths.ensureOutputDirectories()
    }

    AppConfig(
      paths = paths,
      jdbc = jdbc,
      hdfs = hdfs,
      pipelines = pipelines,
      writeTransactionsToDatabase = writeTransactionsToDatabase
    )
  }

  private def resolve(
    cliArgs: Map[String, String],
    cliKey: String,
    propertyKey: String,
    envKey: String
  ): Option[String] =
    cliArgs.get(cliKey)
      .orElse(sys.props.get(propertyKey))
      .orElse(sys.env.get(envKey))
      .map(_.trim)
      .filter(_.nonEmpty)

  private def parseArgs(args: Array[String]): Map[String, String] =
    args.iterator
      .filter(_.startsWith("--"))
      .flatMap { arg =>
        arg.drop(2).split("=", 2) match {
          case Array(key, value) => Some(key -> value)
          case Array(key) => Some(key -> "true")
          case _ => None
        }
      }
      .toMap

  private def parsePipelines(value: String): Set[String] = {
    val parsed = value
      .split(",")
      .iterator
      .map(_.trim.toLowerCase)
      .filter(_.nonEmpty)
      .map {
        case "tweets_user" | "tweetsuser" => "tweets-user"
        case pipeline => pipeline
      }
      .toSet

    if (parsed.contains("all")) {
      SupportedPipelines
    } else {
      val invalidPipelines = parsed.diff(SupportedPipelines)
      require(
        invalidPipelines.isEmpty,
        s"Unsupported pipelines: ${invalidPipelines.toSeq.sorted.mkString(", ")}. Supported values: all, ${SupportedPipelines.toSeq.sorted.mkString(", ")}"
      )
      parsed
    }
  }
}
