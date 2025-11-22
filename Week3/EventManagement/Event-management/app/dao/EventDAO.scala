package dao

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import java.time.{LocalDate, LocalDateTime}
import java.sql.Timestamp
import models.Event
import services.EventKafkaProducer
import slick.lifted.Tag
import slick.lifted.OptionMapperDSL._

import scala.util.{Failure, Success}

class EventDAO @Inject()(dbConfigProvider: DatabaseConfigProvider,eventKafkaProducer: EventKafkaProducer)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  // -------------------------------
  // EVENT TABLE (INNER CLASS)
  // -------------------------------
  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ts => Timestamp.valueOf(ts),
      ts => ts.toLocalDateTime
    )

  implicit val localDateColumnType =
    MappedColumnType.base[LocalDate, java.sql.Date](
      ld => java.sql.Date.valueOf(ld),
      d => d.toLocalDate
    )


  class EventTable(tag: Tag) extends Table[Event](tag, "events") {
    def id         = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name       = column[String]("name")
    def eventType  = column[String]("type")
    def eventDate  = column[Option[LocalDateTime]]("event_date")
    def guestCount = column[Int]("guest_count")
    def createdBy  = column[String]("created_by")
    def createdAt  = column[Option[LocalDateTime]]("created_at")
    def updatedAt  = column[Option[LocalDateTime]]("updated_at")

    def * =
      (id.?, name, eventType, eventDate, guestCount, createdBy, createdAt, updatedAt) <>
        (Event.tupled, Event.unapply)
  }

  val events = TableQuery[EventTable]

  // -------------------------------
  // CRUD
  // -------------------------------
//  def create(event: Event): Future[Event] = {
//    val now = Some(LocalDateTime.now())
//    val toInsert = event.copy(createdAt = now, updatedAt = now)
//
//    val q = (events returning events.map(_.id)
//      into ((ev, id) => ev.copy(id = Some(id)))) += toInsert
//    db.run(q)
//  }
  def create(event: Event): Future[Event] = {
    val now = Some(LocalDateTime.now())
    val toInsert = event.copy(createdAt = now, updatedAt = now)

    val q = (events returning events.map(_.id)
      into ((ev, id) => ev.copy(id = Some(id)))) += toInsert

    db.run(q).map { saved =>
      try {
        eventKafkaProducer.sendEventCreated(saved)
      } catch {
        case ex: Throwable =>
          println(s"⚠ Kafka send failed: ${ex.getMessage}")
      }
      saved
    }
  }

  def list(): Future[Seq[Event]] =
    db.run(events.sortBy(_.id.desc).result)

  def get(id: Int): Future[Option[Event]] =
    db.run(events.filter(_.id === id).result.headOption)

  def update(id: Int, event: Event): Future[Int] = {
    val u = event.copy(id = Some(id), updatedAt = Some(LocalDateTime.now()))
    db.run(events.filter(_.id === id).update(u))
  }

  def delete(id: Int): Future[Int] =
    db.run(events.filter(_.id === id).delete)


//  def getTodayEvents(): Future[Seq[Event]] = {
//    val today = LocalDate.now()
//
//    val startOfDay = today.atStartOfDay()
//    val endOfDay   = today.atTime(23, 59, 59)
//
//    db.run(
//      events
//        .filter(_.eventDate.isDefined)
//        .filter(_.eventDate >= startOfDay)
//        .filter(_.eventDate <= endOfDay)
//        .result
//    )
//  }





  private def sendKafkaSafe(f: => Future[Unit]): Unit =
    f.onComplete {
      case Success(_)  => println("✔ Kafka message sent")
      case Failure(ex) => println("❌ Kafka failed: " + ex.getMessage)
    }

}
