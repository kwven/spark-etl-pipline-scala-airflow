ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"

lazy val sparkVersion = "3.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "project-spark-scala-etl",
    fork := true,
    Test / fork := true,
    Test / parallelExecution := false,
    assembly / assemblyJarName := s"${name.value}-assembly-${version.value}.jar",
    assembly / mainClass := Some("Main"),
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case x if x.endsWith("module-info.class") => sbtassembly.MergeStrategy.discard
      case x if x.startsWith("META-INF/services/") => sbtassembly.MergeStrategy.concat
      case PathList("META-INF", xs @ _*) => sbtassembly.MergeStrategy.discard
      case _ => sbtassembly.MergeStrategy.first
    },
    javaOptions ++= Seq(
      "--add-opens=java.base/java.lang=ALL-UNNAMED",
      "--add-opens=java.base/java.nio=ALL-UNNAMED",
      "--add-opens=java.base/java.util=ALL-UNNAMED",
      "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"
    )
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.postgresql" % "postgresql" % "42.7.0",
  "org.scalactic" %% "scalactic" % "3.2.18",
  "org.scalatest" %% "scalatest" % "3.2.18" % "test"
)
