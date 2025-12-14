import scala.collection.immutable.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"
scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  // Akka (simple actor, not typed — you don't need typed for simulator)
  "com.typesafe.akka" %% "akka-actor" % "2.6.20",

  // Kafka producer client (DO NOT use %% )
  "org.apache.kafka" % "kafka-clients" % "3.6.0",

  // Config file support
  "com.typesafe" % "config" % "1.4.2",

  // JSON library (lightweight)
  "org.json4s" %% "json4s-native" % "3.6.12"
)
