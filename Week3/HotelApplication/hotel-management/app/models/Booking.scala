//package models
//
//import javax.inject.Inject
//import play.api.db.slick.DatabaseConfigProvider
//import slick.jdbc.JdbcProfile
//import scala.concurrent.{ExecutionContext, Future}
//import java.time.LocalDateTime
//
//case class Booking(
//                    id: Int,
//                    guestId: Int,
//                    roomId: Int,
//                    checkInDate: LocalDateTime,
//                    checkOutDate: LocalDateTime,
//                    idProof: String
//                  )
//
//class BookingDAO @Inject()(dbConfigProvider: DatabaseConfigProvider  ,guestDAO: GuestDAO,
//  roomDAO: RoomDAO)(implicit ec: ExecutionContext) {
//
//  val dbConfig = dbConfigProvider.get[JdbcProfile]
//
//  import dbConfig._
//  import profile.api._
//
//  class BookingTable(tag: Tag) extends Table[Booking](tag, "bookings") {
//
//    def id = column[Int]("id", O.PrimaryKey)
//
//    def guestId = column[Int]("guest_id")
//    def roomId = column[Int]("room_id")
//
//    def checkInDate = column[LocalDateTime]("check_in_date")
//    def checkOutDate = column[LocalDateTime]("check_out_date")
//
//    def idProof = column[String]("idproof")
//
//    // Foreign keys
////    def guestFk = foreignKey("fk_guest", guestId, TableQuery[GuestDAO#GuestTable])(_.id)
////    def roomFk = foreignKey("fk_room", roomId, TableQuery[RoomDAO#RoomTable])(_.id)
//    def guestFk = foreignKey("fk_guest", guestId, guestDAO.guests)(_.id)
//    def roomFk = foreignKey("fk_room", roomId, roomDAO.rooms)(_.id)
//
//    def * = (id, guestId, roomId, checkInDate, checkOutDate, idProof) <> ((Booking.apply _).tupled, Booking.unapply)
//  }
//
//  val bookings = TableQuery[BookingTable]
//
//  // List all bookings
//  def list(): Future[Seq[Booking]] = db.run {
//    bookings.result
//  }
//
//  // Create booking
//  def create(id: Int, guestId: Int, roomId: Int, checkInDate: LocalDateTime, checkOutDate: LocalDateTime, idProof: String): Future[Booking] = db.run {
//    val newBooking = Booking(id, guestId, roomId, checkInDate, checkOutDate, idProof)
//    (bookings += newBooking).map(_ => newBooking)
//  }
//
//  // Update an existing booking
//  def update(id: Int, guestId: Int, roomId: Int, checkInDate: LocalDateTime, checkOutDate: LocalDateTime, idProof: String): Future[Unit] = db.run {
//    bookings.filter(_.id === id)
//      .update(Booking(id, guestId, roomId, checkInDate, checkOutDate, idProof))
//      .map(_ => ())
//  }
//
//  // Delete booking
//  def delete(id: Int): Future[Unit] = db.run {
//    bookings.filter(_.id === id)
//      .delete
//      .map(_ => ())
//  }
//}
//
package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import services.KafkaProducerFactory
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDateTime

case class Booking(
                    id: Int,
                    guestId: Int,
                    roomId: Int,
                    checkInDate: LocalDateTime,
                    checkOutDate: LocalDateTime,
                    status: String
                  )

class BookingDAO @Inject()(
                            dbConfigProvider: DatabaseConfigProvider,
                            roomDAO: RoomDAO,
                            guestDAO: GuestDAO,
                            kafka: KafkaProducerFactory
                          )(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  // ------------------------------
  // Local minimal Guest table for foreign key reference only
  // ------------------------------
  class GuestRefTable(tag: Tag) extends Table[(Int)](tag, "guests") {
    def id = column[Int]("id", O.PrimaryKey)
    def * = id
  }
  private val guestRef = TableQuery[GuestRefTable]

  // ------------------------------
  // Local minimal Room table for foreign key reference only
  // ------------------------------
  class RoomRefTable(tag: Tag) extends Table[(Int)](tag, "rooms") {
    def id = column[Int]("id", O.PrimaryKey)
    def * = id
  }
  private val roomRef = TableQuery[RoomRefTable]


  // ------------------------------
  // Full Booking table
  // ------------------------------
  class BookingTable(tag: Tag) extends Table[Booking](tag, "bookings") {

    import java.time.LocalDateTime
    import java.sql.Timestamp

    implicit val localDateTimeColumnType =
      MappedColumnType.base[LocalDateTime, Timestamp](
        ldt => Timestamp.valueOf(ldt),
        ts => ts.toLocalDateTime
      )

    def id = column[Int]("id", O.PrimaryKey)
    def guestId = column[Int]("guest_id")
    def roomId = column[Int]("room_id")
    def checkInDate = column[LocalDateTime]("check_in")
    def checkOutDate = column[LocalDateTime]("check_out")
    def status = column[String]("status")

    // ----------- FOREIGN KEYS -----------
    def guestFk = foreignKey("fk_booking_guest", guestId, guestRef)(_.id)
    def roomFk  = foreignKey("fk_booking_room", roomId, roomRef)(_.id)

    def * = (id, guestId, roomId, checkInDate, checkOutDate, status) <>
      ((Booking.apply _).tupled, Booking.unapply)
  }

  val bookings = TableQuery[BookingTable]


  // ------------------------------
  // CRUD Operations
  // ------------------------------

  def list(): Future[Seq[Booking]] = db.run {
    bookings.result
  }

  def getById(id: Int): Future[Option[Booking]] = db.run {
    bookings.filter(_.id === id).result.headOption
  }

//  def create(booking: Booking): Future[Booking] = {
//    roomDAO.isRoomAvailable(booking.roomId).flatMap { available =>
//      if (available) {
//
//        val action = for {
//          // insert booking
//          _ <- bookings += booking
//
//          // update room availability
//          _ <- roomDAO.rooms
//            .filter(_.id === booking.roomId)
//            .map(_.isAvailable)
//            .update(false)
//
//        } yield booking
//
//        db.run(action.transactionally)
//
//      } else {
//        Future.failed(new Exception("Room is not available"))
//      }
//    }
//  }

  def create(booking: Booking): Future[Booking] = {
    roomDAO.isRoomAvailable(booking.roomId).flatMap { available =>
      if (!available) {
        Future.failed(new Exception("Room is not available"))
      } else {

        val transaction = for {
          _ <- bookings += booking
          _ <- roomDAO.rooms.filter(_.id === booking.roomId).map(_.isAvailable).update(false)
        } yield booking

        // Run DB transaction
        db.run(transaction.transactionally).flatMap { savedBooking =>

          // --- Fetch full objects ----
          val guestFuture = guestDAO.getById(savedBooking.guestId)
          val roomFuture = roomDAO.getById(savedBooking.roomId)

          for {
            maybeGuest <- guestFuture
            maybeRoom <- roomFuture
            guest = maybeGuest.get
            room = maybeRoom.get

            // Send Kafka Event
            _ <- kafka.sendBookingCreated(
              savedBooking,
              guest,
              room
            )
          } yield savedBooking
        }
      }
    }
  }



  def update(booking: Booking): Future[Int] = db.run {
    bookings.filter(_.id === booking.id)
      .update(booking)
  }

  

  def delete(id: Int): Future[Int] = db.run {
    bookings.filter(_.id === id).delete
  }

  def checkoutBooking(bookingId: Int): Future[Unit] = {

    val action = for {
      // Fetch booking
      bookingOpt <- bookings.filter(_.id === bookingId).result.headOption
      booking <- bookingOpt match {
        case Some(b) => DBIO.successful(b)
        case None    => DBIO.failed(new Exception("Booking not found"))
      }

      // Update booking status
      _ <- bookings
        .filter(_.id === bookingId)
        .map(_.status)
        .update("CHECKED_OUT")

      // Mark room available
      _ <- roomDAO.rooms
        .filter(_.id === booking.roomId)
        .map(_.isAvailable)
        .update(true)

    } yield booking

    // Run Transaction
    db.run(action.transactionally).flatMap { updatedBooking =>

      // Fetch guest + room to send Kafka event
      val guestFuture = guestDAO.getById(updatedBooking.guestId)
      val roomFuture  = roomDAO.getById(updatedBooking.roomId)

      for {
        maybeGuest <- guestFuture
        maybeRoom  <- roomFuture
        guest = maybeGuest.get
        room  = maybeRoom.get

        // Send Kafka CHECKOUT Event
        _ <- kafka.sendBookingCheckout(
          updatedBooking.copy(status = "CHECKED_OUT"),
          guest,
          room.copy(isAvailable = true)
        )

      } yield ()
    }
  }

}
