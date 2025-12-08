package Assignment

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CachingExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("CachingExample")
      .master("local[*]") // remove in cluster
      .getOrCreate()

    import spark.implicits._

    // =========================================
    // Sample Sales Dataset (Large Dataset Example)
    // =========================================
    val salesDF = Seq(
      (1, 101, 2, 500.0),
      (1, 102, 1, 200.0),
      (2, 103, 5, 800.0),
      (2, 101, 3, 750.0),
      (3, 104, 2, 300.0),
      (3, 105, 4, 1200.0)
    ).toDF("customerId", "productId", "quantity", "amount")

    // =========================================
    // 1. Cache Dataset
    // =========================================
    println("=== Caching the dataset ===")
    salesDF.cache()           // OR salesDF.persist()
    salesDF.count()           // triggers cache

    // =========================================
    // 2. Total Amount Spent per Customer
    // =========================================
    val start1 = System.currentTimeMillis()

    val amountPerCustomer = salesDF
      .groupBy("customerId")
      .agg(sum("amount").alias("totalAmount"))

    amountPerCustomer.show()

    val end1 = System.currentTimeMillis()

    // =========================================
    // 3. Total Quantity Sold per Product
    // =========================================
    val start2 = System.currentTimeMillis()

    val quantityPerProduct = salesDF
      .groupBy("productId")
      .agg(sum("quantity").alias("totalQuantity"))

    quantityPerProduct.show()

    val end2 = System.currentTimeMillis()

    // =========================================
    // Compare execution time
    // =========================================
    println("===================================================")
    println(s"Time for aggregation 1 (Amount per Customer): ${end1 - start1} ms")
    println(s"Time for aggregation 2 (Quantity per Product): ${end2 - start2} ms")
    println("===================================================")

    // =========================================
    // Run again WITHOUT cache (for comparison)
    // =========================================
    val startNoCache1 = System.currentTimeMillis()
    salesDF.unpersist()         // remove cache

    val amountNoCache = salesDF
      .groupBy("customerId")
      .agg(sum("amount"))
    amountNoCache.show()
    val endNoCache1 = System.currentTimeMillis()

    val startNoCache2 = System.currentTimeMillis()
    val quantityNoCache = salesDF
      .groupBy("productId")
      .agg(sum("quantity"))
    quantityNoCache.show()
    val endNoCache2 = System.currentTimeMillis()

    println("======== TIME WITHOUT CACHE ================")
    println(s"No-cache: amount per customer -> ${endNoCache1 - startNoCache1} ms")
    println(s"No-cache: quantity per product -> ${endNoCache2 - startNoCache2} ms")

    spark.stop()
  }
}

