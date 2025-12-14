package services

import javax.inject._
import play.api.Configuration
import models.DailyConsumption
import org.apache.spark.sql.functions._

@Singleton
class AvroSummaryService @Inject()(
                                    sparkProvider: S3SparkProvider,
                                    config: Configuration
                                  ) {

  import sparkProvider.spark.implicits._

  private val basePath = config.get[String]("app.paths.analytics")

//  def readDailyUsage(householdId: Long): Seq[DailyConsumption] = {
//
//    val path = s"$basePath/daily_consumption_by_household"
//
//    try {
//      sparkProvider.spark
//        .read
//        .format("avro")
//        .option("basePath", path)                     // <-- IMPORTANT
//        .load(s"$path/date=*/")                       // <-- CORRECT PARTITION PATH
//        .withColumn(
//          "date",
//          regexp_extract(input_file_name(), "date=([0-9\\-]+)", 1)
//        )
//        .filter($"household_id" === householdId)
//        .select($"date", $"total_liters")
//        .as[DailyConsumption]
//        .collect()
//        .toSeq
//
//    } catch {
//      case ex: Throwable =>
//        println("ERROR reading daily consumption: " + ex.getMessage)
//        Seq.empty
//    }
//  }
//def readDailyUsage(householdId: Long): Seq[DailyConsumption] = {
//
//  val path = s"$basePath/daily_consumption_by_household"
//
//  try {
//    sparkProvider.spark
//      .read
//      .format("avro")
//      .option("basePath", path)
//      .load(path)                                     // <-- FIX HERE
//      .withColumn("date", regexp_extract(input_file_name(), "date=([0-9\\-]+)", 1))
//      .filter($"household_id" === householdId)
//      .select($"date", $"total_liters")
//      .as[DailyConsumption]
//      .collect()
//      .toSeq
//
//  } catch {
//    case ex: Throwable =>
//      println("ERROR reading daily consumption: " + ex.getMessage)
//      Seq.empty
//  }
//}
def readDailyUsage(householdId: Long): Seq[DailyConsumption] = {

  val path = s"$basePath/daily_consumption_by_household"

  try {
    val df = sparkProvider.spark
      .read
      .format("avro")
      .option("basePath", path)
      .load(path)     // <-- FIX #1 (load full directory, not "date=*/")
      .withColumn(
        "date",
        regexp_extract(input_file_name(), "date=([0-9\\-]+)", 1)
      )

    df.printSchema()
    df.show(false)

    df.filter($"household_id" === householdId)    // <-- FIX #2 (column will now exist)
      .select($"date", $"total_liters")
      .as[DailyConsumption]
      .collect()
      .toSeq

  } catch {
    case ex: Throwable =>
      println("ERROR reading daily consumption: " + ex.getMessage)
      Seq.empty
  }
}

}
