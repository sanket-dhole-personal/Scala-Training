package models

import java.time.LocalDateTime

case class Team(
                 id: Option[Int] = None,        // Auto-increment PRIMARY KEY
                 name: String,
                 phoneNo: String,
                 createdAt: Option[LocalDateTime] = None,
                 updatedAt: Option[LocalDateTime] = None
               )
