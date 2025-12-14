package batch

import util.{SparkConfig, AppConfig}
import org.apache.spark.sql.{SparkSession, functions => F}

object DailyReportsJob {

  def run(targetDate: String, conf: AppConfig): Unit = {

    val spark: SparkSession = SparkConfig.getSession(conf)
    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    println(s"=== Pipeline-D Running for $targetDate ===")

    // READ PIPELINE-B OUTPUT (Avro lake)
    val meterDF = spark.read
      .format("avro")
      .option("basePath", conf.lakePath)
      .load(s"${conf.lakePath}/run=*/date=*/hour=*/")
      .filter($"date" === targetDate)


    println(s"Read meter data count = ${meterDF.count()}")

    // READ household master data
    val household = spark.read
      .format("jdbc")
      .option("url", conf.mysqlUrl)
      .option("dbtable", "household")
      .option("user", conf.mysqlUser)
      .option("password", conf.mysqlPassword)
      .load()

    val enriched = meterDF.join(household, Seq("household_id"))

    // ===============================================================
    // 1) DAILY CONSUMPTION
    // ===============================================================
    val dailyConsumption = enriched
      .groupBy("household_id", "date")
      .agg(F.sum("consumption_liters").as("total_liters"))

    dailyConsumption.write
      .format("avro")
      .mode("append")
      .partitionBy("date")
      .save(conf.analyticsPath + "/daily_consumption_by_household")

    // ===============================================================
    // 2) DAILY PRESSURE STATS
    // ===============================================================
    val pressureStats = enriched
      .groupBy("date")
      .agg(
        F.min("pressure").as("min_pressure"),
        F.max("pressure").as("max_pressure"),
        F.avg("pressure").as("avg_pressure")
      )

    pressureStats.write
      .format("avro")
      .mode("append")
      .partitionBy("date")
      .save(conf.analyticsPath + "/daily_pressure_stats")

    // ===============================================================
    // 3) ANOMALY COUNTS
    // ===============================================================
    val anomalyCounts = enriched
      .groupBy("date")
      .agg(
        F.sum(F.when($"is_spike" === true, 1).otherwise(0)).as("spike_count"),
        F.sum(F.when($"is_drop" === true, 1).otherwise(0)).as("drop_count")
      )

    anomalyCounts.write
      .format("avro")
      .mode("append")
      .partitionBy("date")
      .save(conf.analyticsPath + "/daily_anomaly_counts")

    println("=== Pipeline-D Daily Analytics Completed ===")
  }
}


