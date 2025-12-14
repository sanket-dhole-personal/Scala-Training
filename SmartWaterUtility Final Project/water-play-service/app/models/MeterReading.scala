package models

import play.api.libs.json._

case class MeterReading(
                         meter_id: String,
                         household_id: Long,
                         consumption_liters: Double,
                         pressure: Double,
                         device_status: String,
                         timestamp: Long,
                         mean: Option[Double] = None,
                         std: Option[Double] = None,
                         is_spike: Option[Boolean] = None,
                         is_drop: Option[Boolean] = None,
                         event_time: Option[String] = None,
                         date: Option[String] = None,
                         hour: Option[String] = None
                       )


object MeterReading {
  implicit val fmt: OFormat[MeterReading] = Json.format[MeterReading]
}


