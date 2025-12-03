import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession

object CSVToAvroConverter {
  def main(args: Array[String]): Unit = {
    // Create Spark session
    val spark = SparkSession.builder()
      .appName("CSV to Avro Conversion")
      .master("local[*]") // Using local mode for this example
      .getOrCreate()

    // Set log level to minimize console output during execution
    spark.sparkContext.setLogLevel("ERROR")

    // Define the path to the CSV file
    val csvFilePath = "/Users/vinodh/datasets/jio_mart_items.csv"

    // Read the CSV file into a DataFrame
    val df = spark.read
      .option("header", "true") // Uses the first line as header
      .option("inferSchema", "true") // Infers the data types of columns
      .csv(csvFilePath)

    // Print the schema to verify correct data types and structure
    df.printSchema()

    // Show the first few rows of the DataFrame to verify the read operation
    df.show()

    // Define the output path for the Avro files
    val avroOutputPath = "/Users/vinodh/datasets/jio_mart_items.avro"

    // Write the DataFrame to an Avro file
    df.write
      .format("avro")
      .save(avroOutputPath)

    // Stop the Spark session
    spark.stop()
  }
}
