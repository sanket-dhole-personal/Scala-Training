package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime

case class Guest(
                  id: Int,
                  fullName: String,
                  email: String,
                  phoneNo: Long,
                  createdAt: LocalDateTime,
                  idProof: String
                )

class GuestDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class GuestTable(tag: Tag) extends Table[Guest](tag, "guests") {

    import java.time.LocalDateTime
    import java.sql.Timestamp

    implicit val localDateTimeColumnType =
      MappedColumnType.base[LocalDateTime, Timestamp](
        ldt => Timestamp.valueOf(ldt),
        ts => ts.toLocalDateTime
      )

    def id = column[Int]("id", O.PrimaryKey)
    def fullName = column[String]("name")
    def email = column[String]("email")
    def phoneNo = column[Long]("phone")
    def createdAt = column[LocalDateTime]("created_at")
    def idProof = column[String]("id_proof")

    def * = (id, fullName, email, phoneNo, createdAt, idProof) <> ((Guest.apply _).tupled, Guest.unapply)
  }

  val guests = TableQuery[GuestTable]

  // List all guests
  def list(): Future[Seq[Guest]] = db.run {
    guests.result
  }

  // Create a new guest
  def create(id: Int, fullName: String, email: String, phoneNo: Long, createdAt: LocalDateTime, idProof: String): Future[Guest] = db.run {
    val newGuest = Guest(id, fullName, email, phoneNo, createdAt, idProof)
    (guests += newGuest).map(_ => newGuest)
  }

  def getById(id: Int): Future[Option[Guest]] =
    db.run(guests.filter(_.id === id).result.headOption)

  // Update guest details
  def update(id: Int, fullName: String, email: String, phoneNo: Long, createdAt: LocalDateTime, idProof: String): Future[Unit] = db.run {
    guests.filter(_.id === id)
      .update(Guest(id, fullName, email, phoneNo, createdAt, idProof))
      .map(_ => ())
  }

  // Delete guest
  def delete(id: Int): Future[Unit] = db.run {
    guests.filter(_.id === id)
      .delete
      .map(_ => ())
  }
}

