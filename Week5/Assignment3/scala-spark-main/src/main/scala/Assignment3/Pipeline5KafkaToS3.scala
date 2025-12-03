package Assignment3

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro._
import org.apache.spark.sql.functions._

object Pipeline5KafkaToS3 extends App {

  val spark = SparkSession.builder()
    .appName("Kafka-Avro-to-S3-JSON")
    .master("local[*]")
    .config("spark.hadoop.fs.s3a.access.key", "abc")
    .config("spark.hadoop.fs.s3a.secret.key", "xyz")
    .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .config("spark.hadoop.fs.s3a.path.style.access", "false")
    .getOrCreate()


  // --------------------------------------------------------------------
  // Load Avro schema from resource file
  // --------------------------------------------------------------------
  val schemaPath = "src/main/resources/orders.avsc"
  val avroSchema = spark.read.textFile(schemaPath).collect().mkString(" ")

  // --------------------------------------------------------------------
  // 1. Read Kafka Stream
  // --------------------------------------------------------------------
  val kafkaDF = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "orders_avro_topic")
    .option("startingOffsets", "latest")
    .load()

  // --------------------------------------------------------------------
  // 2. Decode Avro data
  // --------------------------------------------------------------------
  val decodedDF = kafkaDF.select(
    from_avro(col("value"), avroSchema).as("data")
  ).select("data.*")

  // --------------------------------------------------------------------
  // 3. Convert to JSON
  // --------------------------------------------------------------------
  val jsonDF = decodedDF.select(
    to_json(struct("*")).alias("value")
  )

  // --------------------------------------------------------------------
  // 4. Write JSON to S3 (Streaming Sink)
  // --------------------------------------------------------------------
  val query = jsonDF.writeStream
    .format("json")
    .option("path", "s3a://bucket3/stream/json/")
    .option("checkpointLocation", "s3a://bucket3/checkpoints/pipeline5/")
    .outputMode("append")
    .start()

  query.awaitTermination()
}
