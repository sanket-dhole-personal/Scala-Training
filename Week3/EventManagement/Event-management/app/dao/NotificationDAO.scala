package dao

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Notification

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class NotificationDAO @Inject()(dbConfigProvider: DatabaseConfigProvider,val eventDAO: EventDAO)(implicit ec: ExecutionContext)
  {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

 

  import dbConfig._
  import profile.api._

  private val events = TableQuery[eventDAO.EventTable]   // <-- correct reference

  class NotificationTable(tag: Tag) extends Table[Notification](tag, "notifications") {

    import slick.jdbc.JdbcProfile
    import java.sql.Timestamp
    import java.time.LocalDateTime

    implicit val localDateTimeColumnType =
      MappedColumnType.base[LocalDateTime, Timestamp](
        ldt => Timestamp.valueOf(ldt),
        ts => ts.toLocalDateTime
      )


    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def eventId = column[Int]("event_id")
    def message = column[String]("message")
    def payload = column[String]("payload")
    def createdAt = column[Option[LocalDateTime]]("created_at")

    def eventFK = foreignKey("fk_notification_event", eventId, events)(_.id)

    def * = (id.?, eventId, message, payload, createdAt) <> (Notification.tupled, Notification.unapply)
  }

  val notifications = TableQuery[NotificationTable]


  def list(): Future[Seq[Notification]] =
    db.run(notifications.result)

  def get(id: Int): Future[Option[Notification]] =
    db.run(notifications.filter(_.id === id).result.headOption)

  def create(n: Notification): Future[Notification] =
    db.run((notifications returning notifications.map(_.id) into ((no, id) => no.copy(id = Some(id)))) += n)

  def delete(id: Int): Future[Int] =
    db.run(notifications.filter(_.id === id).delete)
}
