import org.apache.spark.sql.SparkSession

object AvroToJsonConverter {
  def main(args: Array[String]): Unit = {
    // Create Spark session
    val spark = SparkSession.builder()
      .appName("Avro to JSON Conversion")
      .master("local[*]") // Using local mode for this example
      .getOrCreate()

    // Set log level to minimize console output during execution
    spark.sparkContext.setLogLevel("ERROR")

    // Define the path to the Avro file
    val avroInputPath = "/Users/vinodh/datasets/jio_mart_items.avro"

    // Read the Avro file into a DataFrame
    val df = spark.read
      .format("avro")
      .load(avroInputPath)

    // Print the schema to verify correct data types and structure
    df.printSchema()

    // Show the first few rows of the DataFrame to verify the read operation
    df.show()

    // Define the output path for the JSON files
    val jsonOutputPath = "/Users/vinodh/datasets/output/jio_mart_items.json"

    // Write the DataFrame to a JSON file
    df.write
      .format("json")
      .save(jsonOutputPath)

    // Stop the Spark session
    spark.stop()
  }
}
