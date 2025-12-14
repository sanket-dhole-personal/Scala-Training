package model

import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.Serialization.write

case class MeterEvent(
                       meter_id: String,
                       household_id: Int,
                       consumption_liters: Double,
                       pressure: Double,
                       device_status: String,
                       timestamp: Long
                     )

object MeterEvent {
  implicit val formats: Formats = DefaultFormats

  def toJson(e: MeterEvent): String = write(e)
}


