package util

import org.apache.spark.sql.SparkSession

object SparkConfig {
  def getSession(conf: AppConfig): SparkSession = {
    SparkSession.builder()
      .appName(conf.sparkAppName)
      .master(conf.sparkMaster)
      .config("spark.hadoop.fs.s3a.access.key", conf.awsAccessKey)
      .config("spark.hadoop.fs.s3a.secret.key", conf.awsSecretKey)
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .getOrCreate()
  }
}
