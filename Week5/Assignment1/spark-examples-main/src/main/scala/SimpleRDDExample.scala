import org.apache.spark.sql.SparkSession

object SimpleRDDExample {
  def main(args: Array[String]): Unit = {
    // Initialize SparkSession
    val spark = SparkSession.builder()
      .appName("Simple RDD Example")
      .master("local[*]")  // Use local mode
      .getOrCreate()

    // Create an RDD from a list of numbers
    val numbers = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val numbersRDD = spark.sparkContext.parallelize(numbers)

    // Calculate the sum of all numbers
    val sum = numbersRDD.reduce(_ + _)
    println(s"Sum of numbers: $sum")

    // Find the maximum value in the RDD
    val max = numbersRDD.max()
    println(s"Maximum value: $max")

    // Filter out even numbers
    val evenNumbersRDD = numbersRDD.filter(_ % 2 == 0)
    println("Even numbers:")
    evenNumbersRDD.collect().foreach(println)

    // Stop the Spark session
    spark.stop()
  }
}
