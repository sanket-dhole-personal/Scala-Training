package models

import play.api.libs.json._

case class Customer(
                     customer_id: Long,
                     name: String,
                     email: String,
                     phone: String
                   )

object Customer {
  implicit val fmt = Json.format[Customer]
}
