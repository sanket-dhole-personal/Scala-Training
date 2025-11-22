package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.MySQLProfile.api._

case class Room(
                 id: Int,
                 roomNo: Int,
                 floor: Int,
                 category: String,
                 price: Long,
                 isAvailable: Boolean
               )



class RoomDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class RoomTable(tag: Tag) extends Table[Room](tag, "rooms") {

    def id = column[Int]("id", O.PrimaryKey)
    def roomNo = column[Int]("room_no")
    def floor = column[Int]("floor")
    def category = column[String]("category")
    def price = column[Long]("price")
    def isAvailable = column[Boolean]("is_available")

    def * = (id, roomNo, floor, category, price, isAvailable) <> ((Room.apply _).tupled, Room.unapply)
  }

//  val rooms = TableQuery[RoomTable]
   val rooms = TableQuery[RoomTable]

  // List all rooms
  def list(): Future[Seq[Room]] = db.run {
    rooms.result
  }

//   Create new room
  def create(id: Int, roomNo: Int, floor: Int, category: String, price: Long, isAvailable: Boolean): Future[Room] = db.run {
    (rooms += Room(id, roomNo, floor, category, price, isAvailable))
      .map(_ => Room(id, roomNo, floor, category, price, isAvailable))
  }



  def getById(id: Int): Future[Option[Room]] =
    db.run(rooms.filter(_.id === id).result.headOption)

  // Update room
  def update(id: Int, roomNo: Int, floor: Int, category: String, price: Long, isAvailable: Boolean): Future[Unit] = db.run {
    rooms.filter(_.id === id)
      .update(Room(id, roomNo, floor, category, price, isAvailable))
      .map(_ => ())
  }

  // Delete room
  def delete(id: Int): Future[Unit] = db.run {
    rooms.filter(_.id === id)
      .delete
      .map(_ => ())
  }

  // Extra Code
  // Check if room is available
  def isRoomAvailable(roomId: Int): Future[Boolean] = {
    db.run(
      rooms.filter(_.id === roomId).map(_.isAvailable).result.headOption
    ).map {
      case Some(value) => value
      case None => throw new Exception("Room not found")
    }
  }

  // Mark room as unavailable (after booking)
  def markRoomUnavailable(roomId: Int): Future[Int] = {
    db.run(
      rooms.filter(_.id === roomId)
        .map(_.isAvailable)
        .update(false)
    )
  }

  // Mark room as available (on checkout)
  def markRoomAvailable(roomId: Int): Future[Int] = {
    db.run(
      rooms.filter(_.id === roomId)
        .map(_.isAvailable)
        .update(true)
    )
  }
}

