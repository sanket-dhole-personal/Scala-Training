package Assignment2


import org.apache.spark.sql.{SparkSession, functions => F}

object FinancialTransactions {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Financial Transactions Analysis - 3M")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._

    import scala.util.Random

    // -------------------------------------------------------------------
    // 🔹 DATA GENERATION (3 Million Records)
    // -------------------------------------------------------------------
    val numTxns = 3000000

    val txnRDD = spark.sparkContext.parallelize(1 to numTxns, 40)
      .map { id =>
        val acc = "ACC_" + Random.nextInt(100000)
        val amt = Random.nextDouble() * 10000
        (acc, amt)
      }

    val txnDF = txnRDD.toDF("accountId", "amount")

    // -------------------------------------------------------------------
    // 🔹 RDD APPROACH
    //     reduceByKey → sum amounts per account
    //     sortBy → find top 10 highest spending accounts
    // -------------------------------------------------------------------
    val rddStart = System.currentTimeMillis()

    val rddTopAccounts = txnRDD
      .reduceByKey(_ + _)              // broad → shuffle
      .sortBy(_._2, ascending = false) // broad → shuffle
      .take(10)

    val rddEnd = System.currentTimeMillis()

    println("\n===== RDD: TOP 10 ACCOUNTS =====")
    rddTopAccounts.foreach(println)
    println(s"RDD Execution Time = ${(rddEnd - rddStart) / 1000.0} sec")

    // -------------------------------------------------------------------
    // 🔹 DATAFRAME APPROACH
    //     groupBy → aggregation
    //     orderBy → sorting
    // -------------------------------------------------------------------
    val dfStart = System.currentTimeMillis()

    val dfTopAccounts = txnDF
      .groupBy("accountId")   // broad → shuffle
      .agg(F.sum("amount").as("totalSpent"))
      .orderBy(F.desc("totalSpent"))  // broad → shuffle
      .limit(10)

    dfTopAccounts.show(false)

    val dfEnd = System.currentTimeMillis()
    println(s"DF Execution Time = ${(dfEnd - dfStart) / 1000.0} sec")

    // -------------------------------------------------------------------
    // 🔹 OBSERVATIONS (Printed)
    // -------------------------------------------------------------------
    println(
      """
        |==================== OBSERVATIONS ====================
        |1️⃣ Sorting is a broad transformation → Requires shuffle.
        |
        |2️⃣ reduceByKey (RDD) vs groupBy (DF):
        |   • Both require shuffle because data must be grouped
        |     by accountId across the cluster.
        |
        |3️⃣ DataFrame is faster because:
        |   • Catalyst Optimizer rewrites query plan efficiently
        |   • Tungsten does better memory management
        |   • Columnar format speeds aggregations
        |
        |======================================================
        |""".stripMargin)

    Thread.sleep(300000)
    spark.stop()
  }
}
