package dao

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Task
import services.EventKafkaProducer

import java.sql.Timestamp
import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class TaskDAO @Inject()(
                         dbConfigProvider: DatabaseConfigProvider,
                         val eventDAO: EventDAO,
                         val teamDAO: TeamDAO,
                         eventKafkaProducer:EventKafkaProducer
                       )(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ldt => Timestamp.valueOf(ldt),
      ts => ts.toLocalDateTime
    )

  // SAFE REFERENCES
  private val events = TableQuery[eventDAO.EventTable]
  private val teams  = TableQuery[teamDAO.TeamTable]
  
  class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def eventId = column[Int]("event_id")
    def teamId = column[Int]("team_id")
    def taskName = column[String]("task_name")
    def dueDate = column[LocalDateTime]("due_date")
    def status = column[String]("status")
    def specialRequests = column[Option[String]]("special_requests")
    def createdAt = column[Option[LocalDateTime]]("created_at")
    def updatedAt = column[Option[LocalDateTime]]("updated_at")

//    def eventFK =
//      foreignKey("fk_task_event", eventId, eventDAO.events)(_.id, onDelete = ForeignKeyAction.Cascade)
//
//    def teamFK =
//      foreignKey("fk_task_team", teamId, teamDAO.teams)(_.id, onDelete = ForeignKeyAction.SetNull)

    // 🔥 MUST USE events and teams, NOT eventDAO / teamDAO
    def eventFK = foreignKey("fk_task_event", eventId, events)(_.id, onDelete = ForeignKeyAction.Cascade)
    def teamFK  = foreignKey("fk_task_team", teamId,  teams )(_.id, onDelete = ForeignKeyAction.SetNull)

    def * = (
      id.?, eventId, teamId, taskName, dueDate, status,
      specialRequests, createdAt, updatedAt
    ) <> (Task.tupled, Task.unapply)
  }

  val tasks = TableQuery[TaskTable]

  def list(): Future[Seq[Task]] = db.run(tasks.result)

  def get(id: Int): Future[Option[Task]] =
    db.run(tasks.filter(_.id === id).result.headOption)

//  def create(task: Task): Future[Task] =
//    db.run((tasks returning tasks.map(_.id) into ((t, id) => t.copy(id = Some(id)))) += task)
def create(task: Task): Future[Task] = {
  val now = Some(LocalDateTime.now())
  val newTask = task.copy(createdAt = now, updatedAt = now)

  db.run((tasks returning tasks.map(_.id)
    into ((t, id) => t.copy(id = Some(id)))) += newTask).flatMap { saved =>

    for {
      Some(event) <- eventDAO.get(saved.eventId)
      Some(team)  <- teamDAO.get(saved.teamId)
      _ <- eventKafkaProducer.sendTaskAssigned(event, team, saved)
    } yield saved
  }
}

  def update(id: Int, task: Task): Future[Int] =
    db.run(tasks.filter(_.id === id).update(task.copy(id = Some(id))))

  def updateStatus(taskId: Int, status: String): Future[Int] = {
    val getTeamIdQuery = tasks.filter(_.id === taskId).map(_.teamId).result.head

    db.run(getTeamIdQuery).flatMap { teamId =>
      val q = tasks.filter(_.id === taskId).map(_.status).update(status)

      db.run(q).flatMap { rows =>
        eventKafkaProducer.sendTaskProgressUpdate(taskId, teamId, status)
          .map(_ => rows)
          .recover { case ex =>
//            Logger.error("Kafka Task Progress failed: " + ex.getMessage)
            rows
          }
      }
    }
  }


  def delete(id: Int): Future[Int] =
    db.run(tasks.filter(_.id === id).delete)
}
