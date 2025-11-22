package service

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import scala.BookingJsonFormat._

object BookingCoordinatorActor {

  sealed trait Command
  final case class ProcessBooking(event: BookingEvent) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>

      val roomActor       = context.spawn(RoomServiceActor(), "roomServiceActor")
      val wifiActor       = context.spawn(WifiServiceActor(), "wifiServiceActor")
      val restaurantActor = context.spawn(RestaurantServiceActor(), "restaurantServiceActor")

      Behaviors.receiveMessage {
        case ProcessBooking(evt) =>
          evt.event match {
            case "BOOKING_CREATED" =>
              roomActor ! RoomServiceActor.HandleBooking(evt)
              wifiActor ! WifiServiceActor.ProvideWifi(evt)
              restaurantActor ! RestaurantServiceActor.SendWelcomeMenu(evt)
              restaurantActor ! RestaurantServiceActor.StartDailyMenu(evt)

            case "BOOKING_CHECKOUT" =>
              restaurantActor ! RestaurantServiceActor.StopDailyMenu(evt.booking.id)

            case other =>
              context.log.warn(s"⚠ Unknown event: $other")
          }
          Behaviors.same
      }
    }
}
