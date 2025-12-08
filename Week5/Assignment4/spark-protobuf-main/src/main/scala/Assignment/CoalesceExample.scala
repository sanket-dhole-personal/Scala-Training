package Assignment

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object CoalesceExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("CoalesceExample")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // =============================================
    // 1. Large dataset simulation (logs)
    // =============================================
    val logData = (1 to 1000000).map(i => (i, s"log message $i"))
    val logDF = spark.sparkContext.parallelize(logData, 50).toDF("id", "message")

    println(s"Initial partitions = ${logDF.rdd.getNumPartitions}")

    // =============================================
    // 2. Filter to create a small dataset
    // =============================================
    val filteredDF = logDF.filter($"id" % 200000 === 0)

    println(s"Filtered records = ${filteredDF.count()}")
    println(s"Filtered partitions (before coalesce) = ${filteredDF.rdd.getNumPartitions}")

    // =============================================
    // 3. Reduce partitions with coalesce()
    // =============================================
    val startCoalesce = System.currentTimeMillis()

    val coalescedDF = filteredDF.coalesce(2)

    println(s"Filtered partitions (after coalesce) = ${coalescedDF.rdd.getNumPartitions}")

    // =============================================
    // 4. Write output (Measure performance)
    // =============================================
    coalescedDF.write.mode("overwrite").parquet("filtered_output_coalesce")

    val endCoalesce = System.currentTimeMillis()

    println(s"Write time using coalesce = ${endCoalesce - startCoalesce} ms")

    // =============================================
    // 5. Write without coalesce (for comparison)
    // =============================================
    val startNoCoalesce = System.currentTimeMillis()

    filteredDF.write.mode("overwrite").parquet("filtered_output_nocoalesce")

    val endNoCoalesce = System.currentTimeMillis()

    println(s"Write time without coalesce = ${endNoCoalesce - startNoCoalesce} ms")

    // =============================================
    // NOTE: File count can be verified by:
    // hdfs dfs -ls filtered_output_coalesce/
    // hdfs dfs -ls filtered_output_nocoalesce/
    // =============================================

    spark.stop()
  }
}

