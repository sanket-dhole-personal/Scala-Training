package dao

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime
import models.{Event, Issue}
import services.EventKafkaProducer

import java.sql.Timestamp
import scala.util.{Failure, Success}

class IssueDAO @Inject()(dbConfigProvider: DatabaseConfigProvider,eventDAO: EventDAO, eventKafkaProducer:EventKafkaProducer )(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._


  private val events = TableQuery[eventDAO.EventTable]

  // --------------------------------------------------------------
  // Issue Table (EXACTLY matches your Issue model)
  // --------------------------------------------------------------
  private class IssueTable(tag: Tag) extends Table[Issue](tag, "issues") {

    def id          = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def eventId     = column[Int]("event_id")
    def raisedBy    = column[String]("raised_by")
    def description = column[String]("description")
    def priority    = column[String]("priority")
    def status      = column[String]("status")
    def createdAt   = column[Option[java.sql.Timestamp]]("created_at")
    def updatedAt   = column[Option[java.sql.Timestamp]]("updated_at")

    // --------------------------------------------------------------
    //  SAFE FOREIGN KEY (NO escaping-scope errors)
    // --------------------------------------------------------------
    def eventFK =
      foreignKey(
        "fk_issue_event",
        eventId,
        events
      )(
        _.id,
        onUpdate = ForeignKeyAction.Cascade,
        onDelete = ForeignKeyAction.Cascade
      )

    def * =
      (id.?, eventId, raisedBy, description, priority, status, createdAt, updatedAt) <>
        (Issue.tupled, Issue.unapply)
  }

  private val issues = TableQuery[IssueTable]

  // --------------------------------------------------------------
  // CRUD OPERATIONS
  // --------------------------------------------------------------

//  def create(issue: Issue): Future[Issue] = {
//    val now = Timestamp.valueOf(LocalDateTime.now())
//    val row = issue.copy(
//      createdAt = Some(now),
//      updatedAt = Some(now)
//    )
//
//    db.run(
//      (issues returning issues.map(_.id)
//        into ((iss, id) => iss.copy(id = Some(id)))) += row
//    )
//  }

  def create(issue: Issue): Future[Issue] = {
    val now = Timestamp.valueOf(LocalDateTime.now())
    val row = issue.copy(createdAt = Some(now), updatedAt = Some(now))

    val query = (issues returning issues.map(_.id)
      into ((iss, id) => iss.copy(id = Some(id)))) += row

    db.run(query).andThen {
      case Success(saved) => sendKafkaSafe(eventKafkaProducer.sendIssueReported(saved))
      case Failure(_)     => () // no kafka when DB fails
    }
  }

  def list(): Future[Seq[Issue]] =
    db.run(issues.sortBy(_.id.desc).result)

  def findById(id: Int): Future[Option[Issue]] =
    db.run(issues.filter(_.id === id).result.headOption)

  def update(id: Int, issue: Issue): Future[Int] = {
    val now = Timestamp.valueOf(LocalDateTime.now())

    // Fetch existing row first to keep createdAt unchanged
    val query = issues.filter(_.id === id)

    db.run(query.result.headOption).flatMap {
      case Some(existing) =>
        val updatedIssue = existing.copy(
          eventId     = issue.eventId,
          raisedBy    = issue.raisedBy,
          description = issue.description,
          priority    = issue.priority,
          status      = issue.status,
          updatedAt   = Some(now)   // Auto update this timestamp
        )

        db.run(query.update(updatedIssue))

      case None =>
        Future.successful(0) // no record found
    }
  }


  def delete(id: Int): Future[Int] =
    db.run(issues.filter(_.id === id).delete)


  private def sendKafkaSafe(f: => Future[Unit]): Unit =
    f.onComplete {
      case Success(_)  => println("✔ Kafka message sent")
      case Failure(ex) => println("❌ Kafka failed: " + ex.getMessage)
    }
}
