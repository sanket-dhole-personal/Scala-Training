package Assignment2


import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object SensorDataProcessor {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("IoT Sensor Data Processor")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // ---------------------------------------------------------
    // 1. Generate IoT Sensor Data (3 Million Records)
    // ---------------------------------------------------------
    val numSensors = 3000000

    val sensorRDD = spark.sparkContext
      .parallelize(1 to numSensors, 40)
      .map { _ =>
        val deviceId = "DEV_" + Random.nextInt(5000)
        val temp = 20 + Random.nextDouble() * 15
        val hum = 40 + Random.nextDouble() * 20
        val hour = Random.nextInt(24)
        (deviceId, temp, hum, hour)
      }

    val sensorDF = sensorRDD
      .toDF("deviceId", "temperature", "humidity", "hour")

    // ---------------------------------------------------------
    // 2. Average temperature per hour (DataFrame API)
    // ---------------------------------------------------------
    val avgTempPerHourDF = sensorDF
      .groupBy("hour")
      .avg("temperature")
      .withColumnRenamed("avg(temperature)", "avgTemperature")

    println("\nSample hourly averages:")
    avgTempPerHourDF.show(5)

    // ---------------------------------------------------------
    // 3. Write to Parquet partitioned by hour
    // ---------------------------------------------------------
    avgTempPerHourDF.write
      .mode(SaveMode.Overwrite)
      .partitionBy("hour")
      .parquet("output/sensor_avg_temp_parquet")

    println("✔ Parquet data written with partitionBy(hour).")

    // ---------------------------------------------------------
    // 4. Required Observations
    // ---------------------------------------------------------
    println("\n=== Observations ===")

    // How many output folders?
    println("1. How many output folders are created?")
    println("   → 24 folders (hour = 0 to 23).")
    println("   Each partition = one folder: hour=0, hour=1, ..., hour=23")
    println()

    // Why broad transformation?
    println("2. Why is groupBy(hour) a broad transformation?")
    println("   - groupBy requires all rows of the same hour to be collected together.")
    println("   - Spark must shuffle data across partitions to group hours globally.")
    println("   - This data movement across partitions = BROAD transformation.")
    println()
    println("   Narrow: computation stays inside each partition.")
    println("   Broad: requires shuffle → global grouping or sorting.")

    Thread.sleep(300000)
    spark.stop()
  }
}
