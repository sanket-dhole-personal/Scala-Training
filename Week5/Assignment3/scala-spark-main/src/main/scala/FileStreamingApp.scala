import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.slf4j.LoggerFactory

object FileStreamingApp {
  def main(args: Array[String]): Unit = {
    // Initialize logger
    val logger = LoggerFactory.getLogger(this.getClass)

    // Initialize Spark Session
    val spark = SparkSession.builder()
      .appName("File Streaming Example")
      .master("local[*]") // Use local mode for testing
      .getOrCreate()

    // Set log level to see only errors (optional)
    spark.sparkContext.setLogLevel("info")

    logger.info("Spark Session created.")

    // Define the input path
    val inputPath = "/Users/vinodh/datasets/datastreams" // Change this to your monitored directory
    logger.info(s"Monitoring directory: $inputPath")

    // Read streaming data from a directory
    val textFileStream = spark.readStream
      .format("text")
      .option("path", inputPath)
      .load()

    logger.info("Stream started from directory.")

    // Simple transformation: Split lines into words
    val words = textFileStream.select(explode(split(col("value"), " ")).alias("words"))
    val wordCounts = words.groupBy("words").count()

    logger.info("Word count transformation applied.")

    // Start the streaming query and print the output to the console
    val query = wordCounts.writeStream
      .outputMode("update")
      .format("console")
      .trigger(Trigger.ProcessingTime("1 minute"))
      .start()

    logger.info("Streaming query started, writing to console.")

    query.awaitTermination()

    logger.info("Streaming query terminated.")
  }
}
