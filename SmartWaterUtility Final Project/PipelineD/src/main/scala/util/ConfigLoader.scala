package util

import com.typesafe.config.ConfigFactory

object ConfigLoader {
  def load(): AppConfig = {
    val conf = ConfigFactory.load()

    AppConfig(
      conf.getString("spark.appName"),
      conf.getString("spark.master"),
      conf.getString("paths.lake"),
      conf.getString("paths.analytics"),
      conf.getString("mysql.url"),
      conf.getString("mysql.user"),
      conf.getString("mysql.password"),
      conf.getString("aws.accessKey"),
      conf.getString("aws.secretKey"),
      conf.getString("aws.region")
    )
  }
}
