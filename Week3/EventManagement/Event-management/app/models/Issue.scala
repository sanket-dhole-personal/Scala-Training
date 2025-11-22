package models

import java.sql.Timestamp
import java.time.LocalDateTime

case class Issue(
                  id: Option[Int] = None,          // Auto-increment PRIMARY KEY
                  eventId: Int,                    // FK → Event
                  raisedBy: String,
                  description: String,
                  priority: String,
                  status: String,
                  createdAt: Option[Timestamp],
                  updatedAt: Option[Timestamp]
                )
