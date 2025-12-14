package util

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

object SparkConfig {
  private val conf = ConfigFactory.load()

  def getSession: SparkSession = {
    SparkSession.builder()
      .appName(conf.getString("spark.appName"))
      .master(conf.getString("spark.master"))
      .config("spark.hadoop.fs.s3a.access.key", conf.getString("aws.accessKey"))
      .config("spark.hadoop.fs.s3a.secret.key", conf.getString("aws.secretKey"))
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")
      .config("spark.hadoop.fs.s3a.aws.credentials.provider",
        "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")

//      // Spark 3.2.1 committer
//      .config("spark.hadoop.fs.s3a.committer.name", "directory")
//      .config("spark.hadoop.mapreduce.outputcommitter.factory.class",
//        "org.apache.hadoop.fs.s3a.commit.S3ACommitterFactory")
//      .config("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")


      .getOrCreate()
  }
}

