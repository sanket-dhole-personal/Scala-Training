package util

import com.typesafe.config.ConfigFactory

object ConfigLoader {

  def load(): AppConfig = {
    val c = ConfigFactory.load()

    AppConfig(
      kafkaBrokers   = c.getString("kafka.brokers"),
      inputTopic     = c.getString("kafka.inputTopic"),
      startingOffsets = c.getString("kafka.startingOffsets"),
      checkpointPath = c.getString("paths.checkpoint"),
      lakePath       = c.getString("paths.lake"),
      recentJsonPath = c.getString("paths.recent"),
      awsAccessKey   = c.getString("aws.accessKey"),
      awsSecretKey   = c.getString("aws.secretKey"),
      awsRegion      = c.getString("aws.region")
    )
  }
}
