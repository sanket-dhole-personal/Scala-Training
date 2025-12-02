package Assignment2


import org.apache.spark.sql.{SparkSession, functions => F}

object EmployeeData {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Employee Data Analysis - 1M")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._

    import scala.util.Random

    // -------------------------------------------------------------------
    // 🔹 DATA GENERATION (1 Million Employees)
    // -------------------------------------------------------------------



    val numEmp = 1000000

    val departments = Array("HR", "IT", "Sales", "Finance")  // FIXED: moved outside

    val empRDD = spark.sparkContext.parallelize(1 to numEmp, 20)
      .map { id =>
        val name = Random.alphanumeric.take(7).mkString
        val dept = departments(Random.nextInt(4))            // Now works
        val salary = 30000 + Random.nextInt(70000)
        (id, name, dept, salary)
      }


    val empDF = empRDD.toDF("empId", "name", "department", "salary")

    println("\n===== SAMPLE EMPLOYEE DATA =====")
    empDF.show(5, false)

    // -------------------------------------------------------------------
    // 🔹 EXERCISE: AVERAGE SALARY PER DEPARTMENT
    // -------------------------------------------------------------------
    val avgSalaryDF = empDF
      .groupBy("department")
      .agg(F.avg("salary").as("avg_salary"))

    println("\n===== AVERAGE SALARY BY DEPARTMENT =====")
    avgSalaryDF.show(false)

    // -------------------------------------------------------------------
    // 🔹 WRITE RESULT TO CSV
    // -------------------------------------------------------------------
    val outputCSV = "output/employee_avg_salary_csv"

    avgSalaryDF.write
      .mode("overwrite")
      .option("header", "true")
      .csv(outputCSV)

    println(s"\nCSV written to: $outputCSV")

    // -------------------------------------------------------------------
    // 🔹 LOAD CSV BACK (Schema Inference Test)
    // -------------------------------------------------------------------
    val csvDF = spark.read
      .option("header", "true")
      .csv(outputCSV)

    println("\n===== CSV LOADED BACK (Schema Inference) =====")
    csvDF.printSchema()
    csvDF.show(false)

    // -------------------------------------------------------------------
    // 🔹 OBSERVATIONS (Printed on Console)
    // -------------------------------------------------------------------
    println(
      """
        |==================== OBSERVATIONS ====================
        |1️⃣ CSV reader infers all columns as STRING.
        |   • avg_salary becomes STRING instead of DOUBLE.
        |
        |2️⃣ To get correct schema, we must:
        |   • Provide schema manually OR
        |   • Use Parquet (which preserves data types)
        |
        |3️⃣ Parquet stores metadata → CSV does not.
        |
        |======================================================
        |""".stripMargin)

    Thread.sleep(300000)

    spark.stop()
  }
}
