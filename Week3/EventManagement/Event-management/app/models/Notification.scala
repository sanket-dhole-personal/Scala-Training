package models

import java.time.LocalDateTime

case class Notification(
                         id: Option[Int] = None,          // Auto-increment PRIMARY KEY
                         eventId: Int,                    // FK → Event
                         message: String,
                         payload: String,
                         createdAt: Option[LocalDateTime] = None
                       )
