package streming

import util.{SparkConfig, AppConfig, ConfigLoader}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.streaming.Trigger

object StreamingJob {

  def start(): Unit = {

    val conf: AppConfig = ConfigLoader.load()

    val spark = SparkSession.builder()
      .appName("Pipeline-B")
      .master("local[*]")

      // S3 authentication
      .config("spark.hadoop.fs.s3a.access.key", conf.awsAccessKey)
      .config("spark.hadoop.fs.s3a.secret.key", conf.awsSecretKey)
      .config("spark.hadoop.fs.s3a.aws.credentials.provider",
        "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
      .config("spark.hadoop.fs.s3a.endpoint", s"s3.${conf.awsRegion}.amazonaws.com")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")

      // REQUIRED FOR S3A DIRECTORY COMMITTER
      .config("spark.hadoop.fs.s3a.committer.name", "directory")
      .config("spark.hadoop.mapreduce.outputcommitter.factory.class",
        "org.apache.hadoop.fs.s3a.commit.S3ACommitterFactory")
      .config("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

      .getOrCreate()

    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    println("=== Pipeline-B Starting ===")
    println(s"Kafka Topic        : ${conf.inputTopic}")
    println(s"Lake Avro Path     : ${conf.lakePath}")
    println(s"Recent JSON Path   : ${conf.recentJsonPath}")
    println(s"Checkpoint Path    : ${conf.checkpointPath}")

    // -------------------------------
    // Kafka Reading
    // -------------------------------
    val raw = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", conf.kafkaBrokers)
      .option("subscribe", conf.inputTopic)
      .option("startingOffsets", conf.startingOffsets)
      .load()

    val readingSchema = new StructType()
      .add("meter_id", StringType)
      .add("household_id", LongType)
      .add("consumption_liters", DoubleType)
      .add("pressure", DoubleType)
      .add("device_status", StringType)
      .add("timestamp", LongType)

    val parsed = raw
      .selectExpr("CAST(value AS STRING) AS json_str")
      .select(from_json($"json_str", readingSchema).alias("data"))
      .select("data.*")
      .withColumn("ingestion_timestamp", current_timestamp())

    parsed.writeStream
      .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
        processBatch(batchDF, batchId, conf, spark)
      }
      .option("checkpointLocation", conf.checkpointPath)
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .start()
      .awaitTermination()
  }

  // --------------------------------------------------------------------
  // PROCESS EACH BATCH
  // --------------------------------------------------------------------
  private def processBatch(
                            df: DataFrame,
                            batchId: Long,
                            conf: AppConfig,
                            spark: SparkSession
                          ): Unit = {

    import spark.implicits._

    if (df.rdd.isEmpty()) {
      println(s"[Batch $batchId] Empty — skipping")
      return
    }

    println(s"[Batch $batchId] Processing ${df.count()} rows")

    // ---------------------------
    // Anomaly detection
    // ---------------------------
    val stats = df.groupBy("meter_id")
      .agg(
        avg("consumption_liters").as("mean"),
        stddev_pop("consumption_liters").as("std")
      )

    val enriched = df.join(stats, "meter_id")
      .withColumn("is_spike", $"consumption_liters" > $"mean" + lit(3) * $"std")
      .withColumn("is_drop", $"consumption_liters" < $"mean" - lit(3) * $"std")
      .withColumn("event_time", ($"timestamp" / 1000).cast(TimestampType))
      .withColumn("date", to_date($"event_time"))
      .withColumn("hour", date_format($"event_time", "HH"))

    // ---------------------------
    // Write outputs
    // ---------------------------
    writeAvro(enriched, conf)
    writeRecentJson(enriched, conf, spark)
  }

  // --------------------------------------------------------------------
  // FIXED AVRO WRITER — THIS IS THE IMPORTANT FIX
  // --------------------------------------------------------------------
  private def writeAvro(df: DataFrame, conf: AppConfig): Unit = {

    val runId = java.util.UUID.randomUUID().toString  // UNIQUE FOLDER

    val output = s"${conf.lakePath}/run=$runId"

    println(s"Writing Avro → $output")

    df.write
      .format("avro")
      .mode("append")
      .partitionBy("date", "hour")
      .save(output)
  }

  // --------------------------------------------------------------------
  // LAST 50 READINGS PER HOUSEHOLD → JSON
  // --------------------------------------------------------------------
  private def writeRecentJson(df: DataFrame, conf: AppConfig, spark: SparkSession): Unit = {

    import spark.implicits._

    println("Writing recent JSON readings...")

    val windowSpec = Window
      .partitionBy($"household_id")
      .orderBy($"event_time".desc)

    val limited = df
      .withColumn("rn", row_number().over(windowSpec))
      .filter($"rn" <= 50)
      .drop("rn")

    limited.write
      .mode("overwrite")
      .partitionBy("household_id")
      .json(conf.recentJsonPath)

    println("JSON write completed.")
  }
}
