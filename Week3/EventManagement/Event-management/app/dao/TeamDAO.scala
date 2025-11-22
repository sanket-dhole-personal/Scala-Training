package dao

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Team
import java.sql.Timestamp
import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}

class TeamDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  implicit val localDateTimeColumnType =
    MappedColumnType.base[LocalDateTime, Timestamp](
      ldt => Timestamp.valueOf(ldt),
      ts => ts.toLocalDateTime
    )

  class TeamTable(tag: Tag) extends Table[Team](tag, "teams") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def phoneNo = column[String]("phone_no")
    def createdAt = column[Option[LocalDateTime]]("created_at")
    def updatedAt = column[Option[LocalDateTime]]("updated_at")

    def * = (id.?, name, phoneNo, createdAt, updatedAt) <> (Team.tupled, Team.unapply)
  }

  val teams = TableQuery[TeamTable]

  def list(): Future[Seq[Team]] = db.run(teams.result)

  def get(id: Int): Future[Option[Team]] =
    db.run(teams.filter(_.id === id).result.headOption)

  def create(team: Team): Future[Team] =
    db.run((teams returning teams.map(_.id) into ((t, id) => t.copy(id = Some(id)))) += team)

  def update(id: Int, team: Team): Future[Int] =
    db.run(teams.filter(_.id === id).update(team.copy(id = Some(id))))

  def delete(id: Int): Future[Int] =
    db.run(teams.filter(_.id === id).delete)
}
