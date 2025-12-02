package Assignment2


import org.apache.spark.sql.{SparkSession, functions => F}

object StudentScores {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Student Scores Sorting - 1.5M")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    import scala.util.Random

    spark.sparkContext.setLogLevel("ERROR")

    // --------------------------------------------------------------
    // 🔹 DATA GENERATION (1.5M STUDENTS)
    // --------------------------------------------------------------
    val numStudents = 1500000

    val studentRDD = spark.sparkContext.parallelize(1 to numStudents, 20)
      .map { id =>
        val name = Random.alphanumeric.take(6).mkString
        val score = Random.nextInt(100)
        (id, name, score)
      }

    val studentDF = studentRDD.toDF("studentId", "name", "score")

    println("\n===== SAMPLE STUDENT DATA =====")
    studentDF.show(5, false)

    // --------------------------------------------------------------
    // 🔹 SORT STUDENTS BY SCORE DESCENDING
    // --------------------------------------------------------------
    val sortedDF = studentDF.orderBy(F.col("score").desc)

    println("\n===== SORTED STUDENTS (Top 10) =====")
    sortedDF.show(10, false)

    // --------------------------------------------------------------
    // 🔹 WRITE SORTED OUTPUT TO JSON
    // --------------------------------------------------------------
    val jsonPath = "output/student_scores_json"

    sortedDF.write
      .mode("overwrite")
      .json(jsonPath)

    println(s"\nJSON written to: $jsonPath")

    // --------------------------------------------------------------
    // 🔹 OBSERVATIONS
    // --------------------------------------------------------------
    println(
      """
        |==================== OBSERVATIONS ====================
        |1️⃣ Sorting is a BROAD transformation.
        |   • Spark must SHUFFLE all 1.5M records across partitions.
        |   • Sorting requires global ordering → triggers full shuffle.
        |
        |2️⃣ JSON writing is slow because:
        |   • JSON is row-based (cannot compress well)
        |   • Each row written as separate text object
        |   • CPU-heavy serialization
        |
        |3️⃣ Parquet/ORC would be MUCH faster due to:
        |   • Columnar format
        |   • Compression
        |   • Binary efficient encoding
        |
        |======================================================
        |""".stripMargin)

    Thread.sleep(300000)
    spark.stop()
  }
}
