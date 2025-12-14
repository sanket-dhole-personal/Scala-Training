package models

import play.api.libs.json._

import java.sql.Date
import java.time.LocalDate

case class BillingHistory(
                           bill_id: Long,
                           household_id: Long,
                           billing_month: LocalDate,
                           total_consumption_liters: Double,
                           total_amount: Double
                         )

object BillingHistory {
  implicit val fmt = Json.format[BillingHistory]
}
