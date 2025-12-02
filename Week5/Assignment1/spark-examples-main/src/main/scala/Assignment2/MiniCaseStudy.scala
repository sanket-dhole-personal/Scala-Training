package Assignment2


import org.apache.spark.sql.{SparkSession, functions => F}

object MiniCaseStudy {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Mini Case Study - Customers & Transactions")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    import scala.util.Random

    spark.sparkContext.setLogLevel("ERROR")

    // ================================================================
    // 🔹 DATA GENERATION — CUSTOMERS (2 Million)
    // ================================================================
    val custCount = 2000000

    val custRDD = spark.sparkContext.parallelize(1 to custCount, 50)
      .map { id =>
        val name = Random.alphanumeric.take(8).mkString
        (id, name)
      }

    val custDF = custRDD.toDF("customerId", "name")

    println("\n===== SAMPLE CUSTOMERS =====")
    custDF.show(5, false)

    // ================================================================
    // 🔹 DATA GENERATION — TRANSACTIONS (5 Million)
    // ================================================================
    val txnCount = 5000000

    val txnRDD2 = spark.sparkContext.parallelize(1 to txnCount, 80)
      .map { tid =>
        val cust = Random.nextInt(custCount) + 1
        val amt  = Random.nextDouble() * 1000
        (tid, cust, amt)
      }

    val txnDF2 = txnRDD2.toDF("txnId", "customerId", "amount")

    println("\n===== SAMPLE TRANSACTIONS =====")
    txnDF2.show(5, false)

    // ================================================================
    // 🔹 JOIN CUSTOMERS & TRANSACTIONS (Broad Shuffle)
    // ================================================================
    val joinedDF = txnDF2
      .join(custDF, "customerId")

    println("\n===== JOINED DATA (Customer + Transactions) =====")
    joinedDF.show(5, false)

    // ================================================================
    // 🔹 TOTAL SPEND PER CUSTOMER
    // ================================================================
    val spendDF = joinedDF
      .groupBy("customerId", "name")
      .agg(F.sum("amount").as("total_spend"))

    println("\n===== TOTAL SPEND PER CUSTOMER =====")
    spendDF.show(10, false)

    // ================================================================
    // 🔹 SAVE FINAL RESULT TO PARQUET
    // ================================================================
    val parquetOutput = "output/customer_total_spend_parquet"

    spendDF.write
      .mode("overwrite")
      .parquet(parquetOutput)

    println(s"\nParquet written to: $parquetOutput")

    // ================================================================
    // 🔹 OBSERVATIONS
    // ================================================================
    println(
      """
        |==================== OBSERVATIONS ====================
        |
        |1️⃣ JOIN causes the LARGEST shuffle in Spark.
        |   • Both sides (2M customers, 5M txns)
        |   • Must be shuffled by join key `customerId`
        |   • Data redistributed across cluster → HEAVY SHUFFLE
        |
        |2️⃣ groupBy(customerId) also causes a BROAD SHUFFLE.
        |   • All transactions for the same customer must go
        |     to the SAME partition to calculate total spend.
        |
        |3️⃣ Parquet is BEST for analytical output because:
        |   • Columnar format → fast reads for aggregations
        |   • Compression reduces storage massively
        |   • Predicate pushdown improves performance
        |   • Preserves schema (unlike CSV)
        |
        |======================================================
        |""".stripMargin)

    Thread.sleep(300000)

    spark.stop()
  }
}
