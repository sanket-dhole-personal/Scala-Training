package Assignment

import org.apache.spark.sql.SparkSession

object AccumulatorExample {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("AccumulatorExample")
      .master("local[*]")   // remove when running on cluster
      .getOrCreate()

    val sc = spark.sparkContext

    // ===========================================
    // Sample dataset of transaction amounts
    // ===========================================
    val transactions = List(100.0, 750.0, 320.0, 820.0, 150.0, 900.0, 450.0, 510.0)
    val txnRDD = sc.parallelize(transactions)

    // ===========================================
    // Threshold value
    // ===========================================
    val threshold = 500.0

    // ===========================================
    // 1. Create an accumulator
    // ===========================================
    val highValueTxnAcc = sc.longAccumulator("HighValueTransactions")

    // ===========================================
    // 2. Process dataset & update accumulator
    // ===========================================
    txnRDD.foreach { amount =>
      if (amount > threshold) {
        highValueTxnAcc.add(1)   // accumulator update
      }
    }

    // ===========================================
    // 3. Print accumulator result in Driver
    // ===========================================
    println("======================================")
    println(s"Total transactions > $threshold = ${highValueTxnAcc.value}")
    println("======================================")

    spark.stop()
  }
}

