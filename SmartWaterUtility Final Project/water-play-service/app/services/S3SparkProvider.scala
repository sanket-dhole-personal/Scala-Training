package services

import javax.inject._
import org.apache.spark.sql.SparkSession
import play.api.Configuration

@Singleton
class S3SparkProvider @Inject()(config: Configuration) {

  lazy val spark: SparkSession = {

    val accessKey = config.get[String]("app.aws.accessKey")
    val secretKey = config.get[String]("app.aws.secretKey")

    SparkSession.builder()
      .appName("Play-S3-Reader")
      .master("local[*]")
      .config("spark.hadoop.fs.s3a.access.key", accessKey)
      .config("spark.hadoop.fs.s3a.secret.key", secretKey)
      .config("spark.hadoop.fs.s3a.aws.credentials.provider",
        "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.impl",
        "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .getOrCreate()
  }
}
