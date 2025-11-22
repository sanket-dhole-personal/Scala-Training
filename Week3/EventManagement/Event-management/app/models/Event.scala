package models

import java.time.LocalDateTime



import java.time.LocalDateTime

case class Event(
                  id: Option[Int],
                  name: String,
                  eventType: String,
                  eventDate: Option[LocalDateTime],
                  guestCount: Int,
                  createdBy: String,
                  createdAt: Option[LocalDateTime],
                  updatedAt: Option[LocalDateTime]
                )



