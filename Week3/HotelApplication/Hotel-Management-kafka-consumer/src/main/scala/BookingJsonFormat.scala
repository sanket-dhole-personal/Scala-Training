package scala

import spray.json._

case class Booking(id: Int, checkIn: String, checkOut: String, status: String)
case class Guest(id: Int, fullName: String, email: String, phone: Long)
case class Room(id: Int, roomNumber: Int, floor: Int, isAvailable: Boolean)
case class BookingEvent(event: String, booking: Booking, guest: Guest, room: Room)

object BookingJsonFormat extends DefaultJsonProtocol {
  implicit val bookingFormat = jsonFormat4(Booking)
  implicit val guestFormat   = jsonFormat4(Guest)
  implicit val roomFormat    = jsonFormat4(Room)
  implicit val eventFormat   = jsonFormat4(BookingEvent)
}
