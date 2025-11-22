package service

import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}
import akka.actor.typed.Behavior
import scala.concurrent.duration._
import scala.BookingJsonFormat._

object RestaurantServiceActor {

  sealed trait Command
  final case class StartDailyMenu(event: BookingEvent) extends Command
  final case class StopDailyMenu(bookingId: Int) extends Command
  final case class SendWelcomeMenu(event: BookingEvent) extends Command
  private final case class SendMenu(bookingId: Int, email: String, fullName: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.withTimers { timers =>
      Behaviors.receive { (context, message) =>

        message match {
          case StartDailyMenu(evt) =>
            val guest = evt.guest
            timers.startTimerWithFixedDelay(
              evt.booking.id,
              SendMenu(evt.booking.id, guest.email, guest.fullName),
              24.hours
            )
            context.log.info(s"[RestaurantService] Daily menu emails started for booking ${evt.booking.id}")
            Behaviors.same

          case StopDailyMenu(id) =>
            timers.cancel(id)
            context.log.info(s"[RestaurantService] Daily menu emails stopped for $id")
            Behaviors.same

          case SendWelcomeMenu(evt) =>
            val guest = evt.guest

            val body =
              s"""
                 |Hello ${guest.fullName},
                 |
                 |🍽️ Welcome to our Hotel Restaurant!
                 |
                 |Your stay includes full access to our buffet dining services.
                 |
                 |⏰ Dining Timings:
                 | • Breakfast: 8:00 AM – 10:00 AM
                 | • Lunch:     1:00 PM – 3:00 PM
                 | • Dinner:    8:00 PM – 10:00 PM
                 |
                 |🥗 Today's Menu Highlights:
                 | • Breakfast: Idli, Dosa, Upma
                 | • Lunch: Paneer Butter Masala, Veg Biryani, Naan
                 | • Dinner: Dal Tadka, Jeera Rice, Aloo Gobi
                 |
                 |We hope you enjoy your stay and dining with us!
                 |
                 |Warm Regards,
                 |Restaurant Team
                 |""".stripMargin

            EmailHelper.sendEmail(guest.email, "Restaurant Welcome", body)
            context.log.info(s"[RestaurantService] Welcome restaurant email sent for booking ${evt.booking.id}")
            Behaviors.same

          case SendMenu(_, email, fullName) =>
            val body =
              s"""
                 |Hello $fullName,
                 |
                 |Today's Menu:
                 | - Breakfast: Idli / Dosa
                 | - Lunch: Paneer Butter Masala
                 | - Dinner: Dal Tadka with Jeera Rice
                 |
                 |Regards,
                 |Restaurant Team
                 |""".stripMargin

            EmailHelper.sendEmail(email, "Today's Menu", body)
            Behaviors.same
        }
      }
    }
}
