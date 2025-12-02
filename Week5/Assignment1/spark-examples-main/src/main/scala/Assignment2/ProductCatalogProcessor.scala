package Assignment2


import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object ProductCatalogProcessor {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Product Catalog Processor")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ---------------------------------------------------------
    // 1. Generate Product Catalog (2 Million Products)
    // ---------------------------------------------------------
    val numProducts = 2000000
    val categories = Array("Electronics", "Clothes", "Books")

    val productRDD = spark.sparkContext
      .parallelize(1 to numProducts, 40)
      .map { id =>
        val category = categories(Random.nextInt(categories.length))
        val price = Random.nextDouble() * 2000
        val desc = Random.alphanumeric.take(50).mkString
        (id.toLong, category, price, desc)
      }

    val productDF = productRDD.toDF("productId", "category", "price", "description")

    // ---------------------------------------------------------
    // 2. DataFrame Filter: price > 1000
    // ---------------------------------------------------------
    val expensiveDF = productDF.filter($"price" > 1000)

    println("\nSample expensive products:")
    expensiveDF.show(5)

    // ---------------------------------------------------------
    // 3. Sort by price (Broad Shuffle!)
    // ---------------------------------------------------------
    val startSort = System.currentTimeMillis()

    val sortedDF = expensiveDF.orderBy($"price".desc)

    sortedDF.show(5)

    val endSort = System.currentTimeMillis()
    println(s"\nSort Time = ${(endSort - startSort) / 1000.0} sec")

    // ---------------------------------------------------------
    // 4. Write sorted data to CSV
    // ---------------------------------------------------------
    val startCsv = System.currentTimeMillis()

    sortedDF.write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .csv("output/product_sorted_csv")

    val endCsv = System.currentTimeMillis()

    println(s"CSV Write Time = ${(endCsv - startCsv) / 1000.0} sec")

    // ---------------------------------------------------------
    // 5. Write sorted data to Parquet
    // ---------------------------------------------------------
    val startParquet = System.currentTimeMillis()

    sortedDF.write
      .mode(SaveMode.Overwrite)
      .parquet("output/product_sorted_parquet")

    val endParquet = System.currentTimeMillis()

    println(s"Parquet Write Time = ${(endParquet - startParquet) / 1000.0} sec")

    // ---------------------------------------------------------
    // 6. Observations (Exam / Interview Ready)
    // ---------------------------------------------------------
    println("\n=== Observations ===")

    println("1. CSV vs Parquet Write Performance:")
    println("   - CSV is slower because:")
    println("       * Writes data as plain text (large output size).")
    println("       * No compression.")
    println("       * CPU must convert every field to string.")
    println("       * Produces many small files.")
    println("   - Parquet is faster because:")
    println("       * Columnar binary format.")
    println("       * Built-in compression + encoding.")
    println("       * Very optimized for analytics.")
    println()

    println("2. Why sorting causes a broad shuffle:")
    println("   - Sorting requires global ordering of data.")
    println("   - Records must move across partitions to achieve correct order.")
    println("   - This forces a full data shuffle → BROAD transformation.")
    println("   - Shuffle = expensive: network transfer + disk spill.")
    println()

    println("Narrow = transformation stays within the same partition.")
    println("Broad = requires data movement across partitions (shuffle).")

    Thread.sleep(300000)
    spark.stop()
  }
}
