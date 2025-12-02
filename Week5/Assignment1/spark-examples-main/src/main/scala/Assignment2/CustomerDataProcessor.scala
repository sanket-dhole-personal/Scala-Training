package Assignment2



import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object CustomerDataProcessor {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Customer Data Processing")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // -------------------------------
    // 1. Generate 5M Customer Records
    // -------------------------------
    val numRecords = 5000000
    val cities = (1 to 50).map(i => s"City_$i").toArray

    val customersRDD = spark.sparkContext
      .parallelize(1 to numRecords, 50)
      .map { id =>
        val name = Random.alphanumeric.take(10).mkString
        val age = 18 + Random.nextInt(53)
        val city = cities(Random.nextInt(cities.length))
        (id.toLong, name, age, city)
      }

    val customersDF = customersRDD.toDF("customerId", "name", "age", "city")

    // -------------------------------------
    // 2. RDD: Count of customers in each city
    // -------------------------------------
    val rddCityCount = customersRDD
      .map { case (_, _, _, city) => (city, 1) }
      .reduceByKey(_ + _)

    println("RDD City Count Sample:")
    rddCityCount.take(10).foreach(println)

    // ----------------------------------------
    // 3. DataFrame: Count customers using groupBy
    // ----------------------------------------
    val dfCityCount = customersDF
      .groupBy("city")
      .count()

    dfCityCount.show(10)

    // ----------------------------------------
    // 4. Write DataFrame Output (CSV/JSON/Parquet)
    // ----------------------------------------

    // Write CSV
    dfCityCount.write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .csv("output/city_count_csv")

    // Write JSON
    dfCityCount.write
      .mode(SaveMode.Overwrite)
      .json("output/city_count_json")

    // Write Parquet
    dfCityCount.write
      .mode(SaveMode.Overwrite)
      .parquet("output/city_count_parquet")

    println("✔ Output saved successfully!")
   Thread.sleep(300000)
    spark.stop()
  }
}

