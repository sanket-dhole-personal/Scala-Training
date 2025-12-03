import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger

object KafkaStream {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Kafka Spark Streaming")
      .master("local[*]") // use local for testing, in production you would use a cluster manager
      .getOrCreate()



    // Assuming the messages are simple text messages
    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092") // replace with your Kafka server address
      .option("subscribe", "textinfo")
      .option("failOnDataLoss","false")// replace with your topic
      .option("startingOffsets", "earliest")
      .load()

    import spark.implicits._
    // Assuming messages are UTF8 encoded; adjust accordingly
    val stringDF = df.selectExpr("CAST(value AS STRING)").as[String]

    // Filter messages containing specific keywords
    val filteredDF = stringDF.filter(x=>x.toLowerCase().contains("error") || x.toLowerCase.contains("critical"))
    // Output path for the text file
    val query = filteredDF.writeStream
      .outputMode("append")
      .format("text")
      .option("path", "/Users/vinodh/datasets/textinfo")
      .option("checkpointLocation", "/Users/vinodh/datasets/checkpointDir")
      .trigger(Trigger.ProcessingTime("1 minute"))
      .start()

    query.awaitTermination()
  }
}
