name := """water-play-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test

libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
libraryDependencySchemes += "org.scala-lang.modules" %% "scala-parser-combinators" % VersionScheme.Always

evictionErrorLevel := Level.Warn

libraryDependencies ++= Seq(
  jdbc,
  ws,       // <-- Play WS needed for Play app runtime
  "mysql" % "mysql-connector-java" % "8.0.33",

  // Spark for Scala 2.13.14
  "org.apache.spark" %% "spark-core" % "3.5.1",
  "org.apache.spark" %% "spark-sql"  % "3.5.1",
  "org.apache.spark" %% "spark-avro" % "3.5.1",

  // Hadoop S3 access
  "org.apache.hadoop" % "hadoop-aws" % "3.3.6",
  "org.apache.hadoop" % "hadoop-common" % "3.3.6",

  // Play JSON
  "com.typesafe.play" %% "play-json" % "2.10.0-RC5",
  "org.playframework.anorm" %% "anorm" % "2.7.0",
//  "com.auth0" % "java-jwt" % "4.4.0",

)

// Needed to avoid XML & parser combinator eviction errors
evictionErrorLevel := Level.Warn
