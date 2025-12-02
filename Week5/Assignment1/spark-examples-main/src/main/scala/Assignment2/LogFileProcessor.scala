package Assignment2


import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object LogFileProcessor {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Log File Processing")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // -------------------------------------------------
    // 1. Generate 5M Log Records
    // -------------------------------------------------
    val levels = Array("INFO", "WARN", "ERROR")
    val numLogs = 5000000

    val logsRDD = spark.sparkContext
      .parallelize(1 to numLogs, 40)
      .map { _ =>
        val ts = System.currentTimeMillis() - Random.nextInt(10000000)
        val level = levels(Random.nextInt(levels.length))
        val msg = Random.alphanumeric.take(15).mkString
        val user = Random.nextInt(10000)
        s"$ts|$level|$msg|$user"
      }

    val logsDF = logsRDD
      .map(_.split("\\|"))
      .map(arr => (arr(0), arr(1), arr(2), arr(3)))
      .toDF("timestamp", "level", "message", "userId")

    // -------------------------------------------------
    // 2. RDD Filter: Count ERROR logs
    // -------------------------------------------------
    val startRdd = System.currentTimeMillis()

    val errorRDD = logsRDD.filter(_.contains("|ERROR|"))
    val errorRDDCount = errorRDD.count()

    val endRdd = System.currentTimeMillis()

    println(s"\nRDD ERROR Count = $errorRDDCount")
    println(s"RDD Filter Time = ${(endRdd - startRdd) / 1000.0} sec")

    // -------------------------------------------------
    // 3. DataFrame Filter: Count ERROR logs
    // -------------------------------------------------
    val startDf = System.currentTimeMillis()

    val errorDF = logsDF.filter($"level" === "ERROR")
    val errorDFCount = errorDF.count()

    val endDf = System.currentTimeMillis()

    println(s"\nDataFrame ERROR Count = $errorDFCount")
    println(s"DataFrame Filter Time = ${(endDf - startDf) / 1000.0} sec")

    // -------------------------------------------------
    // 4. Write ERROR logs to plain text
    // -------------------------------------------------
    errorRDD.saveAsTextFile("output/error_logs")

    println("✔ ERROR logs saved to plain text")

    // -------------------------------------------------
    // 5. Write full logs to JSON
    // -------------------------------------------------
    logsDF.write
      .mode(SaveMode.Overwrite)
      .json("output/full_logs_json")

    println("✔ Full logs saved to JSON")

    // -------------------------------------------------
    // 6. Required Observations
    // -------------------------------------------------
    println("\n=== Observations ===")

    // Why plain text is slow?
    println("1. Plain text writing is slow because:")
    println("   - No compression or columnar optimization.")
    println("   - Writes many small files (1 record per line).")
    println("   - No binary encoding → heavy disk IO.")
    println()

    // Narrow vs Broad transformations
    println("2. Narrow vs Broad Transformations:")
    println("   - filter → Narrow (no shuffle).")
    println("   - map → Narrow (no shuffle).")
    println("   - sort → Broad (requires full shuffle).")
    println()
    println("   Narrow = transformation inside the same partition.")
    println("   Broad  = data must move across partitions (shuffle).")

    Thread.sleep(3000)
    spark.stop()
  }
}
