package service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import scala.BookingJsonFormat._

object WifiServiceActor {

  sealed trait Command
  final case class ProvideWifi(event: BookingEvent) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match {
        case ProvideWifi(evt) =>
          val guest = evt.guest
          val password = s"wifi-${evt.booking.id}-guest"

          val emailBody =
            s"""
               |Dear ${guest.fullName},
               |
               |Your WiFi Credentials:
               |SSID     : Hotel-WiFi
               |Password : $password
               |
               |""".stripMargin

          EmailHelper.sendEmail(guest.email, "WiFi Login Credentials", emailBody)

          context.log.info(s"[WifiService] WiFi email sent to ${guest.email}")
          Behaviors.same
      }
    }
}
