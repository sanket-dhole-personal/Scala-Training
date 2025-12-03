package Assignment3

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object RdbmsToKeyspaces extends App {

  println("🚀 Starting RDS → Keyspaces ETL Job...")

  val spark = SparkSession.builder
    .appName("RDS to Keyspaces Pipeline")
    .master("local[*]")
    // Cassandra connection
    .config("spark.cassandra.connection.host", "cassandra.us-east-1.amazonaws.com")
    .config("spark.cassandra.connection.port", "9142")
    .config("spark.cassandra.connection.ssl.enabled", "true")


    .config("spark.cassandra.auth.username", "User") // required
    .config("spark.cassandra.auth.password", "jsjsd")

    // Consistency recommended
    .config("spark.cassandra.input.consistency.level", "LOCAL_QUORUM")

    // Truststore
    .config("spark.cassandra.connection.ssl.trustStore.path", "/Users/racit/cassandra_truststore.jks")
    .config("spark.cassandra.connection.ssl.trustStore.password", "changeit")

    .getOrCreate()

  println("✅ Spark Session created successfully.")

  // --------------------- MySQL JDBC -------------------------
  val jdbcUrl = "jdbc:mysql://database.com:3306/sanket"
  val jdbcProps = new java.util.Properties()
  jdbcProps.setProperty("user", "admin")
  jdbcProps.setProperty("password", "abc")
//  jdbcProps.setProperty("driver", "com.mysql.cj.jdbc.Driver")

  println("📡 Reading tables from RDS MySQL...")

  val customersDF = spark.read.jdbc(jdbcUrl, "customers", jdbcProps)
  val ordersDF    = spark.read.jdbc(jdbcUrl, "orders", jdbcProps)
  val itemsDF     = spark.read.jdbc(jdbcUrl, "order_items", jdbcProps)

  println(s"📊 customers count = ${customersDF.count()}")
  println(s"📊 orders count = ${ordersDF.count()}")
  println(s"📊 order_items count = ${itemsDF.count()}")

  // --------------------- Join Tables -------------------------
  println("🔗 Performing joins...")

  val finalDF =
    customersDF
      .join(ordersDF, "customer_id")
      .join(itemsDF, "order_id")
      .select(
        col("customer_id"),
        col("name"),
        col("email"),
        col("city"),
        col("order_id"),
        col("order_date").cast("timestamp"),
        col("amount"),
        col("item_id"),
        col("product_name"),
        col("quantity")
      )

  val joinedCount = finalDF.count()
  println(s"📦 Joined dataset row count = $joinedCount")

  if (joinedCount == 0) {
    println("⚠️ No records found after join. Aborting write to Keyspaces.")
    spark.stop()
    System.exit(1)
  }

  // --------------------- Write to Keyspaces -------------------------
  println("📝 Writing final dataset to Amazon Keyspaces (Cassandra)...")

  finalDF.write
    .format("org.apache.spark.sql.cassandra")
    .option("keyspace", "retail")
    .option("table", "sales_data")
    .mode("append")
    .save()

  println(s"✅ Successfully wrote $joinedCount records to retail.sales_data in Amazon Keyspaces.")

  spark.stop()
  println("🏁 Job finished successfully.")
}
