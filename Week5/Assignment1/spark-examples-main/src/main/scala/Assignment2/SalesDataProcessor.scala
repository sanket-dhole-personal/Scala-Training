package Assignment2


import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object SalesDataProcessor {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Sales Data Processor")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ------------------------------------------------
    // 1. Generate 10M Sales Records (Store, Amount)
    // ------------------------------------------------
    val numSales = 10000000
    val stores = (1 to 100).map(i => s"Store_$i").toArray

    val salesRDD = spark.sparkContext
      .parallelize(1 to numSales, 50)
      .map { id =>
        val store = stores(Random.nextInt(stores.length))
        val amount = Random.nextDouble() * 500
        (store, amount)
      }

    // Convert to DataFrame
    val salesDF = salesRDD.toDF("storeId", "amount")

    // -----------------------------------------------
    // 2. RDD Comparison: groupByKey vs reduceByKey
    // -----------------------------------------------

    val startGroup = System.currentTimeMillis()

    val groupByResult = salesRDD
      .groupByKey()                      // FIXED: remove (_._1)
      .mapValues(values => values.sum)   // Sum all amounts

    groupByResult.take(5)

    val endGroup = System.currentTimeMillis()


    val startReduce = System.currentTimeMillis()
    val reduceByResult = salesRDD
      .map(x => (x._1, x._2))
      .reduceByKey(_ + _)
    reduceByResult.take(5)
    val endReduce = System.currentTimeMillis()

    println("\n=== Performance Comparison (10M Records) ===")
    println(s"groupByKey Time: ${(endGroup - startGroup) / 1000.0} sec")
    println(s"reduceByKey Time: ${(endReduce - startReduce) / 1000.0} sec")

    // -----------------------------------------------
    // 3. RDD → DataFrame total sales per store
    // -----------------------------------------------
    val dfStoreSales = salesDF
      .groupBy("storeId")
      .sum("amount")
      .withColumnRenamed("sum(amount)", "totalSales")

    dfStoreSales.show(10)

    // -----------------------------------------------
    // 4. Save result to Parquet
    // -----------------------------------------------
    dfStoreSales.write
      .mode(SaveMode.Overwrite)
      .parquet("output/store_sales_parquet")

    println("✔ Parquet file saved successfully")

    // ------------------------------------------------
    // 5. Required Observations
    // ------------------------------------------------

    println("\n=== Observations ===")
    println("1. groupByKey is MUCH slower than reduceByKey because:")
    println("   - It shuffles ALL values across nodes without aggregation.")
    println("   - Causes heavy network I/O and memory pressure.")
    println("   - reduceByKey performs map-side combine → less shuffle.")
    println()
    println("2. Shuffling Occurred During:")
    println("   - groupByKey: full shuffle (wide transformation).")
    println("   - reduceByKey: shuffle also occurs but after map-side partial aggregation.")
    println()
    println("3. Why reduceByKey = narrow → broad transformation chain?")
    println("   - Narrow: map-side combining (local reduce).")
    println("   - Broad: reduce across partitions (shuffle).")
    println("   → This makes reduceByKey faster and scalable.")

    Thread.sleep(300000)
    spark.stop()
  }
}
