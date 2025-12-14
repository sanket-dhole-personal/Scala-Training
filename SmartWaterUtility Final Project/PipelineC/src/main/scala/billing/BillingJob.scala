package billing

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{SaveMode, SparkSession}
import util.DBConfig

object BillingJob {

  def main(args: Array[String]): Unit = {

    println("=== Pipeline-C Billing Batch Job Started ===")
    val db = DBConfig.load()

    val spark = SparkSession.builder()
      .appName("SmartWater-BillingJob")
      .master("local[*]")
      .config("spark.hadoop.fs.s3a.access.key", db.accessKey)
      .config("spark.hadoop.fs.s3a.secret.key", db.secretKey)
      .config("spark.hadoop.fs.s3a.endpoint", "s3.amazonaws.com")
      .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
      .config("spark.hadoop.fs.s3a.path.style.access", "false")
      .config("spark.hadoop.fs.s3a.aws.credentials.provider",
        "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")// real deployment → remove this
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")
    import spark.implicits._



    // ---------------------------------------------------------------
    // 1. Load Avro consolidated water readings (output of Pipeline B)
    // ---------------------------------------------------------------
    val meterData = spark.read
      .format("avro")
      .load(db.lakePath)           // s3://water/lake/meter_readings/...

    meterData.printSchema()

    // ---------------------------------------------------------------
    // 2. Load MySQL master data
    // ---------------------------------------------------------------
    val customerDF = spark.read.format("jdbc")
      .option("url", db.jdbcUrl)
      .option("dbtable", "customer")
      .option("user", db.user)
      .option("password", db.password)
      .load()

    val householdDF = spark.read.format("jdbc")
      .option("url", db.jdbcUrl)
      .option("dbtable", "household")
      .option("user", db.user)
      .option("password", db.password)
      .load()

    // 🔥 FIXED HERE — rename billing_plan_id so join key matches plan table
    val householdDfRenamed = householdDF.withColumnRenamed("billing_plan_id", "plan_id")

    val planDF = spark.read.format("jdbc")
      .option("url", db.jdbcUrl)
      .option("dbtable", "billing_plan")
      .option("user", db.user)
      .option("password", db.password)
      .load()



    // -----------------------------------------------------------------
    // 3. Filter Avro records for selected billing month
    // -----------------------------------------------------------------


//    val billingMonth = "2025-12"     // you can pass via args
//
//    val filteredUsage = meterData
//      .filter($"date".startsWith(billingMonth))

    val billingMonth = "2025-12"  // << change this for testing months

    val meterDataWithMonth = meterData
      .withColumn(
        "billing_month_key",
        date_format($"ingestion_timestamp", "yyyy-MM")
      )

    println("=== Distinct months in data ===")
    meterDataWithMonth.select("billing_month_key").distinct().show(false)

    val filteredUsage = meterDataWithMonth.filter($"billing_month_key" === billingMonth)

    println(s"Filtered Usage Count for month $billingMonth = " + filteredUsage.count())



    // -----------------------------------------------------------------
    // 4. Aggregate total consumption per household
    // -----------------------------------------------------------------
    val monthlyUsage = filteredUsage
      .groupBy("household_id")
      .agg(
        sum("consumption_liters").as("total_consumption_liters")
      )

    // -----------------------------------------------------------------
    // 5. Join household → plan → customer → usage
    // -----------------------------------------------------------------
    val joined =  monthlyUsage
      .join(householdDfRenamed, Seq("household_id"), "inner")
      .join(planDF, Seq("plan_id"), "inner")
      .join(customerDF, Seq("customer_id"), "inner")
      .withColumn("billing_month", to_date(lit(billingMonth + "-01")))
//      .withColumn("billing_month", lit(billingMonth))

    
    // -----------------------------------------------------------------
    // 6. Calculate bill amount
    // -----------------------------------------------------------------
    val billed = joined.withColumn(
      "total_amount",
      $"total_consumption_liters" * $"rate_per_liter" +
        $"fixed_charge" +
        ($"total_consumption_liters" * $"rate_per_liter" * $"tax_percent" / 100.0)
    )

    // -----------------------------------------------------------------
    // 7. Select target table fields
    // -----------------------------------------------------------------
    val billHistory = billed
      .select(
//        $"customer_id",
        $"household_id",
        $"billing_month",
        $"total_consumption_liters",
        $"total_amount",
        current_timestamp().as("generated_at")
      )

    val cleanedBillHistory = billHistory.drop("customer_id")
    // -----------------------------------------------------------------
    // 8. Write result back into MySQL billing history
    // -----------------------------------------------------------------
    cleanedBillHistory.write
      .format("jdbc")
      .option("url", db.jdbcUrl)
      .option("dbtable", "billing_history")
      .option("user", db.user)
      .option("password", db.password)
      .mode(SaveMode.Append)
      .save()

    // Debug and test 
    println("=== HOUSEHOLDS PRESENT IN AVRO ===")
    meterData.select("household_id").distinct().show(50, false)
    println("=== Pipeline-C Billing Job Completed Successfully ===")

    spark.stop()
  }
}


