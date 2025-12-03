package Assignment3


import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object ParquetToJsonAggregation extends App {

  println("🚀 Starting Pipeline 3: Parquet → Aggregation → JSON")

  val spark = SparkSession.builder()
    .appName("ParquetToJsonAggregation")
    .master("local[*]")
    .config("spark.hadoop.fs.s3a.access.key", "abc")
    .config("spark.hadoop.fs.s3a.secret.key", "xyz")
    .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .config("spark.hadoop.fs.s3a.path.style.access", "false")

    .getOrCreate()

  println(">>> Spark Session created successfully!")

  // -----------------------
  // READ PARQUET FROM S3
  // -----------------------
  println(">>> Reading parquet from S3...")

  val inputPath = "s3a://sanket-s3-bucket3/retail-output/sales/parquet/"

  val df = spark.read
    .parquet(inputPath)

  println(">>> Read completed. Total rows = " + df.count())
  df.show(10, truncate = false)

  // -----------------------
  // AGGREGATION
  // -----------------------
  println(">>> Performing aggregations for each product...")

  val result = df.groupBy("product_name")
    .agg(
      sum("quantity").alias("total_quantity"),
      sum("amount").alias("total_revenue")
    )

  println(">>> Aggregation complete. Sample output:")
  result.show(10, truncate = false)

  // -----------------------
  // WRITE JSON TO S3
  // -----------------------
  val outputPath = "s3a://sanket-s3-bucket3/retail-output/aggregates/products.json"

  println(s">>> Writing aggregated JSON to: $outputPath")

  result.write
    .mode("overwrite")
    .json(outputPath)

  println(">>> Successfully written aggregated results to S3!")
  println("========== PIPELINE 3 COMPLETED ==========")

  spark.stop()
}

