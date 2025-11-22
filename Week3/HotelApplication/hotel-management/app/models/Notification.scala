//package models
//
//import javax.inject.Inject
//import play.api.db.slick.DatabaseConfigProvider
//import slick.jdbc.JdbcProfile
//import scala.concurrent.{ExecutionContext, Future}
//import java.time.LocalDateTime
//
//case class Notification(
//                         id: Int,
//                         bookingId: Int,
//                         notificationType: String,
//                         sendDate: LocalDateTime,
//                         status: String
//                       )
//
//class NotificationDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
//
//  val dbConfig = dbConfigProvider.get[JdbcProfile]
//
//  import dbConfig._
//  import profile.api._
//
//  class NotificationTable(tag: Tag) extends Table[Notification](tag, "notifications") {
//
//    def id = column[Int]("id", O.PrimaryKey)
//
//    def bookingId = column[Int]("booking_id")
//
//    def notificationType = column[String]("notification_type")
//    def sendDate = column[LocalDateTime]("send_date")
//    def status = column[String]("status")
//
//    // Foreign key → Bookings(Id)
//    def bookingFk =
//      foreignKey("fk_booking", bookingId, TableQuery[BookingDAO#BookingTable])(_.id)
//
//    def * =
//      (id, bookingId, notificationType, sendDate, status) <>
//        ((Notification.apply _).tupled, Notification.unapply)
//  }
//
//  val notifications = TableQuery[NotificationTable]
//
//  // List all notifications
//  def list(): Future[Seq[Notification]] = db.run {
//    notifications.result
//  }
//
//  // Create new notification
//  def create(id: Int, bookingId: Int, notificationType: String, sendDate: LocalDateTime, status: String): Future[Notification] = db.run {
//    val newNotification = Notification(id, bookingId, notificationType, sendDate, status)
//    (notifications += newNotification).map(_ => newNotification)
//  }
//
//  // Update a notification
//  def update(id: Int, bookingId: Int, notificationType: String, sendDate: LocalDateTime, status: String): Future[Unit] = db.run {
//    notifications.filter(_.id === id)
//      .update(Notification(id, bookingId, notificationType, sendDate, status))
//      .map(_ => ())
//  }
//
//  // Delete a notification
//  def delete(id: Int): Future[Unit] = db.run {
//    notifications.filter(_.id === id)
//      .delete
//      .map(_ => ())
//  }
//}
//
package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime

case class Notification(
                         id: Int,
                         bookingId: Int,
                         notificationType: String,
                         sendDate: LocalDateTime,
                         status: String
                       )

class NotificationDAO @Inject()(
                                 dbConfigProvider: DatabaseConfigProvider
                               )(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  // --------------------------------------------------------
  // Minimal Booking Reference Table ONLY for FK
  // --------------------------------------------------------
  class BookingRefTable(tag: Tag) extends Table[Int](tag, "bookings") {
    def id = column[Int]("id", O.PrimaryKey)
    def * = id
  }

  private val bookingRef = TableQuery[BookingRefTable]

  // --------------------------------------------------------
  // Full Notification Table
  // --------------------------------------------------------
  class NotificationTable(tag: Tag) extends Table[Notification](tag, "notifications") {

    import java.time.LocalDateTime
    import java.sql.Timestamp

    implicit val localDateTimeColumnType =
      MappedColumnType.base[LocalDateTime, Timestamp](
        ldt => Timestamp.valueOf(ldt),
        ts => ts.toLocalDateTime
      )

    def id = column[Int]("id", O.PrimaryKey)
    def bookingId = column[Int]("booking_id")
    def notificationType = column[String]("notification_type")
    def sendDate = column[LocalDateTime]("send_date")
    def status = column[String]("status")

    // Foreign Key → bookings(id)
    def bookingFk =
      foreignKey("fk_notification_booking", bookingId, bookingRef)(_.id)

    def * =
      (id, bookingId, notificationType, sendDate, status) <>
        ((Notification.apply _).tupled, Notification.unapply)
  }

  val notifications = TableQuery[NotificationTable]

  // CRUD
  def list(): Future[Seq[Notification]] = db.run(notifications.result)

  def getById(id: Int): Future[Option[Notification]] =
    db.run(notifications.filter(_.id === id).result.headOption)

  def create(notification: Notification): Future[Notification] =
    db.run((notifications += notification).map(_ => notification))

  def update(notification: Notification): Future[Int] = db.run {
    notifications.filter(_.id === notification.id)
      .update(notification)
  }

  def delete(id: Int): Future[Int] =
    db.run(notifications.filter(_.id === id).delete)
}
