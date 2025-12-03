import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.avro.functions.from_avro
import org.apache.spark.sql.functions._

object AvroKafkaConsumer {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("AvroKafkaConsumer")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    // Load schema from resources folder
    val schemaStream = getClass.getResourceAsStream("/record.avsc")
    val schemaString = scala.io.Source.fromInputStream(schemaStream).mkString

    // Read from Kafka
    val df = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("subscribe", "avro_topic")
      .load()

    // Decode Avro and extract columns
    val parsed = df
      .select(from_avro(col("value"), schemaString).alias("data"))
      .select("data.*")

    // Convert entire record to a JSON string → needed for text sink
    val parsedAsText = parsed.select(
      to_json(struct(parsed.columns.map(col): _*)).alias("value")
    )

    // Write to TEXT output (valid now)
    val query = parsedAsText.writeStream
      .outputMode("append")
      .format("text")
      .option("path", "/Users/vinodh/datasets/avroresults")
      .option("checkpointLocation", "/Users/vinodh/datasets/avropoint")
      .start()

    query.awaitTermination()
  }
}
