package util

case class AppConfig(
                      kafkaBrokers: String,
                      inputTopic: String,
                      startingOffsets: String,
                      checkpointPath: String,
                      lakePath: String,
                      recentJsonPath: String,
                      awsAccessKey: String,
                      awsSecretKey: String,
                      awsRegion: String
                    )
