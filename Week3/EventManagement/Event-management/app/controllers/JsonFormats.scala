package controllers

import play.api.libs.json._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonFormats {
  // ISO formatter
  private val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

  implicit val localDateTimeReads: Reads[LocalDateTime] =
    Reads[LocalDateTime] { js =>
      js.validate[String].flatMap { str =>
        try JsSuccess(LocalDateTime.parse(str, fmt))
        catch { case _: Exception => JsError("Invalid date format, expected ISO_LOCAL_DATE_TIME") }
      }
    }

  implicit val localDateTimeWrites: Writes[LocalDateTime] =
    Writes[LocalDateTime](ldt => JsString(ldt.format(fmt)))

  // Option[LocalDateTime] handled automatically by Play when base Reads/Writes are in scope
  // Generic helpers for Option[Int] id in models are handled by default Reads/Writes

  // NOTE: controllers will define model-specific Format implicits (using these date implicits)
}
