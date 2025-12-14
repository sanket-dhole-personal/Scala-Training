package models

import play.api.libs.json._

case class DailyConsumption(
                             date: String,
                             total_liters: Double
                           )

object DailyConsumption {
  implicit val fmt: OFormat[DailyConsumption] = Json.format[DailyConsumption]
}
