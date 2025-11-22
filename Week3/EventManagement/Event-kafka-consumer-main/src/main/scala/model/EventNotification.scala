package model

//case class EventNotification(
//                              event: String,
//                              eventId: Int,
//                              eventName: Option[String],
//                              taskId: Option[Int],
//                              teamId: Option[Int],
//                              status: Option[String],
//                              taskDescription: Option[String],
//                              deadline: Option[String],
//                              message: Option[String],
//                              reportedAt: Option[String]
//                            )

case class EventNotification(
                              event: String,
                              eventId: Option[Int],
                              eventName: Option[String],
                              taskId: Option[Int],
                              teamId: Option[Int],
                              status: Option[String],
                              taskDescription: Option[String],
                              deadline: Option[String],
                              message: Option[String],
                              reportedAt: Option[String]
                            )


