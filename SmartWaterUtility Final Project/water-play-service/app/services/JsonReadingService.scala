package services

import javax.inject._
import play.api.Configuration
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import models.MeterReading

@Singleton
class JsonReadingService @Inject()(
                                    sparkProvider: S3SparkProvider,
                                    config: Configuration
                                  ) {

  import sparkProvider.spark.implicits._

  private val basePath = config.get[String]("app.paths.recentJson")

  def readRecent(householdId: Long): Seq[MeterReading] = {

    val path = s"$basePath/household_id=$householdId/*.json"

    try {

      val df = sparkProvider.spark
        .read
        .option("inferSchema", "true")
        .json(path)
        .withColumn("household_id", lit(householdId))   // FIX HERE
        .withColumn("timestamp", $"timestamp".cast("long"))

      df.show(false)

      df.as[MeterReading]
        .orderBy($"timestamp".desc)
        .take(50)
        .toSeq

    } catch {
      case ex: Throwable =>
        println("ERROR reading JSON: " + ex.getMessage)
        Seq.empty
    }
  }
}
