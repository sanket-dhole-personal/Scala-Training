package models

import play.api.libs.json._

case class Household(
                      household_id: Long,
                      customer_id: Long,
                      address: String,
                      district: String,
                      state: String,
                      pincode: String,
                      meter_id: String
                    )

object Household {
  implicit val fmt = Json.format[Household]
}
