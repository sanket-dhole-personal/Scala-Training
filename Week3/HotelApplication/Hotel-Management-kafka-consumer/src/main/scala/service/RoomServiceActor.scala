package service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import scala.BookingJsonFormat._

object RoomServiceActor {

  sealed trait Command
  final case class HandleBooking(booking: BookingEvent) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case HandleBooking(evt) =>
          val guest = evt.guest
          val room  = evt.room

          val emailBody =
            s"""
               |Dear ${guest.fullName},
               |
               |Welcome to our Hotel!
               |Your room ${room.roomNumber} on floor ${room.floor} is ready.
               |
               |Enjoy your stay!
               |""".stripMargin

          EmailHelper.sendEmail(guest.email, "Welcome to the Hotel", emailBody)

          context.log.info(s"[RoomService] Email sent for booking ${evt.booking.id}")
          Behaviors.same
      }
    }
}
