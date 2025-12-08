import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object BroadcastingExample {

  def main(args: Array[String]): Unit = {

    // ========================================
    // 1. Spark Session
    // ========================================
    val spark = SparkSession.builder()
      .appName("BroadcastingExchangeRates")
      .master("local[*]")       // remove in cluster mode
      .getOrCreate()

    import spark.implicits._

    // ========================================
    // 2. Large Dataset: Transactions
    // ========================================
    // transaction_id, amount, currency
    val transactionsDF = Seq(
      (1, 100.0, "EUR"),
      (2, 200.0, "INR"),
      (3, 50.0, "GBP"),
      (4, 500.0, "USD"),
      (5, 300.0, "EUR"),
      (6, 700.0, "INR")
    ).toDF("txn_id", "amount", "currency")

    // ========================================
    // 3. Small Dataset: Exchange Rates (to USD)
    // ========================================
    val exchangeRates = Map(
      "USD" -> 1.0,
      "EUR" -> 1.08,
      "GBP" -> 1.25,
      "INR" -> 0.012
    )

    // ========================================
    // 4. Broadcast the Exchange Rate Map
    // ========================================
    val broadcastRates = spark.sparkContext.broadcast(exchangeRates)

    // ========================================
    // 5. UDF for converting amount → USD
    // ========================================
    val convertToUSD = udf((amount: Double, currency: String) => {
      val rateMap = broadcastRates.value
      val rate = rateMap.getOrElse(currency, 1.0)
      amount * rate
    })

    // ========================================
    // 6. Add USD Conversion Column
    // ========================================
    val convertedDF = transactionsDF
      .withColumn("amount_usd", convertToUSD(col("amount"), col("currency")))

    // ========================================
    // 7. Count Transactions per Currency
    // ========================================
    val txnCountDF = transactionsDF
      .groupBy("currency")
      .count()

    // ========================================
    // 8. Print Results
    // ========================================
    println("=========== Converted Transactions (USD) ===========")
    convertedDF.show(false)

    println("=========== Transaction Count Per Currency ==========")
    txnCountDF.show(false)

    // ========================================
    // 9. Stop Spark
    // ========================================
    spark.stop()
  }
}
