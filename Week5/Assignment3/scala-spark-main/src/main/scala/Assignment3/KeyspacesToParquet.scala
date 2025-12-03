package Assignment3

import org.apache.spark.sql.SparkSession

object KeyspacesToParquet {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("RDS to Keyspaces Pipeline")
      .master("local[*]")
      // Cassandra connection
      .config("spark.cassandra.connection.host", "cassandra.us-east-1.amazonaws.com")
      .config("spark.cassandra.connection.port", "9142")
      .config("spark.cassandra.connection.ssl.enabled", "true")

     

      .config("spark.cassandra.auth.username", "user") // required
      .config("spark.cassandra.auth.password", "abcd")

      // Consistency recommended
      .config("spark.cassandra.input.consistency.level", "LOCAL_QUORUM")

      // Truststore
      .config("spark.cassandra.connection.ssl.trustStore.path", "/Users/racit/cassandra_truststore.jks")
      .config("spark.cassandra.connection.ssl.trustStore.password", "changeit")

      // S3 Write Support
      .config("spark.hadoop.fs.s3a.access.key", "abc")
      .config("spark.hadoop.fs.s3a.secret.key", "xyz")
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")

      .getOrCreate()

    println("✅ Spark Session created successfully.")

    // Read from Keyspaces/Cassandra
    val sales = spark.read
      .format("org.apache.spark.sql.cassandra")
      .option("keyspace", "retail")
      .option("table", "sales_data")
      .load()

    // Select required columns
    val selected = sales.select("customer_id", "order_id", "amount", "product_name", "quantity")

    // Write partitioned Parquet to S3
    selected.write
      .mode("overwrite") // or "append"
      .partitionBy("customer_id")
      .parquet("s3a://bucket3/retail-output/sales/parquet/")

    spark.stop()
  }
}
