package util

import com.typesafe.config.ConfigFactory

case class DBConfig(
                     jdbcUrl: String,
                     user: String,
                     password: String,
                     lakePath: String,
                     accessKey:String,
                     secretKey:String
                   )

object DBConfig {

  def load(): DBConfig = {
    val conf = ConfigFactory.load()

    DBConfig(
      jdbcUrl = conf.getString("mysql.url"),
      user = conf.getString("mysql.user"),
      password = conf.getString("mysql.password"),
      lakePath = conf.getString("paths.lake"),
      accessKey=conf.getString("aws.accessKey"),
      secretKey=conf.getString("aws.secretKey")
    )
  }
}

