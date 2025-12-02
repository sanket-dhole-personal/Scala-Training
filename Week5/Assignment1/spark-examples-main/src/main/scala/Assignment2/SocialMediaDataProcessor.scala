package Assignment2


import org.apache.spark.sql.{SaveMode, SparkSession}

import scala.util.Random

object SocialMediaDataProcessor {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Social Media Users & Posts Processor")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // -------------------------------------------------------
    // 1. Generate Users (1 Million)
    // -------------------------------------------------------
    val userCount = 1000000

    val userRDD = spark.sparkContext
      .parallelize(1 to userCount, 30)
      .map { id =>
        val name = Random.alphanumeric.take(8).mkString
        val age = 15 + Random.nextInt(60)
        (id, name, age)
      }

    val userDF = userRDD.toDF("userId", "name", "age")

    // -------------------------------------------------------
    // 2. Generate Posts (2 Million)
    // -------------------------------------------------------
    val postCount = 2000000

    val postRDD = spark.sparkContext
      .parallelize(1 to postCount, 40)
      .map { pid =>
        val user = Random.nextInt(userCount) + 1
        val txt = Random.alphanumeric.take(20).mkString
        (pid, user, txt)
      }

    val postDF = postRDD.toDF("postId", "userId", "text")

    // -------------------------------------------------------
    // 3. Join Users & Posts on userId
    // -------------------------------------------------------
    val joinedDF = postDF
      .join(userDF, Seq("userId"), "inner")

    println("\nSample joined data:")
    joinedDF.show(5)

    // -------------------------------------------------------
    // 4. Count posts per age group
    // -------------------------------------------------------
    val postsByAgeDF = joinedDF
      .groupBy("age")
      .count()
      .withColumnRenamed("count", "postCount")

    println("\nPosts per age group:")
    postsByAgeDF.show(10)

    // -------------------------------------------------------
    // 5. Write results to JSON
    // -------------------------------------------------------
    postsByAgeDF.write
      .mode(SaveMode.Overwrite)
      .json("output/posts_by_age_json")

    println("✔ Output saved to JSON.")

    // -------------------------------------------------------
    // 6. Required Observations
    // -------------------------------------------------------
    println("\n=== Observations ===")

    // Why join causes shuffle?
    println("1. Why does join cause shuffle?")
    println("   - Join requires matching userId across two DataFrames.")
    println("   - Rows with the same key from both datasets must be grouped together.")
    println("   - Spark must move data across partitions → this is SHUFFLE.")
    println("   - Therefore, join = BROAD transformation.")
    println()

    // Why DF join easier than RDD join?
    println("2. Why is DataFrame join easier than RDD join?")
    println("   - DataFrame API automatically handles:")
    println("        * Optimization (Catalyst optimizer)")
    println("        * Column matching")
    println("        * Shuffle planning")
    println("        * Serialization / deserialization")
    println("   - RDD join requires manual key-value structure:")
    println("        RDD[(K, V)] and RDD[(K, W)]")
    println("        and then using rdd1.join(rdd2)")
    println("   - DataFrame uses SQL-style syntax, much simpler and optimized.")
    println()

    println("Summary:")
    println("   * DF join = easier + optimized")
    println("   * RDD join = harder + slower + no automatic optimization")

    Thread.sleep(300000)
    spark.stop()
  }
}
