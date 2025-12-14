package util

case class AppConfig(
                      sparkAppName: String,
                      sparkMaster: String,
                      lakePath: String,
                      analyticsPath: String,
                      mysqlUrl: String,
                      mysqlUser: String,
                      mysqlPassword: String,
                      awsAccessKey: String,
                      awsSecretKey: String,
                      awsRegion: String
                    )
