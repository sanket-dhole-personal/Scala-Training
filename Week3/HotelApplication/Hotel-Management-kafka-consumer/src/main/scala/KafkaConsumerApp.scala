import akka.actor.typed.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import service.BookingCoordinatorActor

import BookingJsonFormat._
import spray.json._

object KafkaConsumerApp {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[BookingCoordinatorActor.Command] =
      ActorSystem(BookingCoordinatorActor(), "kafkaConsumerSystem")

    val coordinator = system

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId("booking-consumer-1")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

    Consumer
      .plainSource(consumerSettings, Subscriptions.topics("hotel_booking"))
      .map(_.value())
      .map { msg =>
        try Some(msg.parseJson.convertTo[BookingEvent])
        catch {
          case _: Throwable =>
            println(s"❌ Invalid JSON: $msg")
            None
        }
      }
      .collect { case Some(evt) => evt }
      .runWith(Sink.foreach { evt =>
        coordinator ! BookingCoordinatorActor.ProcessBooking(evt)
      })
  }
}












//import akka.actor.typed.ActorSystem
//import akka.actor.typed.scaladsl.Behaviors
//import akka.kafka.{ConsumerSettings, Subscriptions}
//import akka.kafka.scaladsl.Consumer
//import akka.stream.scaladsl.Sink
//import org.apache.kafka.clients.consumer.ConsumerConfig
//import org.apache.kafka.common.serialization.StringDeserializer
//import BookingJsonFormat._
//import spray.json._
//
//object KafkaConsumerApp {
//
//  def main(args: Array[String]): Unit = {
//
//    implicit val system: ActorSystem[_] =
//      ActorSystem(Behaviors.empty, "kafkaConsumerSystem")
//    import system.executionContext
//
//    val consumerSettings =
//      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
//        .withBootstrapServers("localhost:9092")
//        .withGroupId("booking-consumer-1")     // REQUIRED
//        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//
//    Consumer
//      .plainSource(consumerSettings, Subscriptions.topics("hotel_booking"))
//      .map(_.value())
//      .map { msg =>
//        try {
//          Some(msg.parseJson.convertTo[BookingEvent])
//        } catch {
//          case _: Throwable =>
//            println(s"❌ Skipping invalid JSON: $msg")
//            None
//        }
//      }
//      .collect { case Some(evt) => evt }
//      .runWith(Sink.foreach(event =>
//        println(
//          s"""
//             |🔥 Booking Event Received from Kafka:
//             |-------------------------------------
//             |Event Type  : ${event.event}
//             |Booking ID  : ${event.booking.id}
//             |Check In    : ${event.booking.checkIn}
//             |Check Out   : ${event.booking.checkOut}
//             |Status      : ${event.booking.status}
//             |
//             |Guest Name  : ${event.guest.fullName}
//             |Guest Email : ${event.guest.email}
//             |Phone       : ${event.guest.phone}
//             |
//             |Room Number : ${event.room.roomNumber}
//             |Floor       : ${event.room.floor}
//             |Available   : ${event.room.isAvailable}
//             |-------------------------------------
//             |""".stripMargin)
//      ))
//  }
//}
//
//
////racit@192 kafka_2.12-3.5.1 % $KAFKA_PATH/bin/kafka-console-consumer.sh --topic hotel_booking --from-beginning --bootstrap-server localhost:9092 --property print.partition=true --property print.offset=true