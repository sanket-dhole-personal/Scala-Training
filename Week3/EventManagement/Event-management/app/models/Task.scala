package models

import java.time.LocalDateTime

case class Task(
                 id: Option[Int] = None,        // Auto-increment PRIMARY KEY
                 eventId: Int,                  // event also int auto-increment
                 teamId: Int,
                 taskName: String,
                 dueDate: LocalDateTime,
                 status: String,
                 specialRequests: Option[String],
                 createdAt: Option[LocalDateTime] = None,
                 updatedAt: Option[LocalDateTime] = None
               )
