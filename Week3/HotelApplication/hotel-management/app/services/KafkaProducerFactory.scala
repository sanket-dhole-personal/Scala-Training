package services

import javax.inject._
import play.api.libs.json.Json
import com.google.inject.Singleton
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.concurrent.{ExecutionContext, Future}
import models.{Booking, Guest, Room}


@Singleton
class KafkaProducerFactory @Inject()()(implicit ec: ExecutionContext) {

  private val topic = "hotel_booking"

  private val props = new java.util.Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  /** Send FULL BOOKING CREATED event */
  def sendBookingCreated(
                          booking: Booking,
                          guest: Guest,
                          room: Room
                        ): Future[Unit] = Future {

    val json = Json.obj(
      "event" -> "BOOKING_CREATED",

      "booking" -> Json.obj(
        "id" -> booking.id,
        "checkIn" -> booking.checkInDate.toString,
        "checkOut" -> booking.checkOutDate.toString,
        "status" -> booking.status
      ),

      "guest" -> Json.obj(
        "id" -> guest.id,
        "fullName" -> guest.fullName,
        "email" -> guest.email,
        "phone" -> guest.phoneNo
      ),

      "room" -> Json.obj(
        "id" -> room.id,
        "roomNumber" -> room.roomNo,
        "floor" -> room.floor,
        "isAvailable" -> room.isAvailable
      )
    ).toString()
    println(json)
    producer.send(new ProducerRecord(topic, json))
  }

    /** Send CHECKOUT event */
    def sendBookingCheckout(
                             booking: Booking,
                             guest: Guest,
                             room: Room
                           ): Future[Unit] = Future {

      val json = Json.obj(
        "event" -> "BOOKING_CHECKOUT",

        "booking" -> Json.obj(
          "id" -> booking.id,
          "checkIn" -> booking.checkInDate.toString,
          "checkOut" -> booking.checkOutDate.toString,
          "status" -> "CHECKED_OUT"
        ),

        "guest" -> Json.obj(
          "id" -> guest.id,
          "fullName" -> guest.fullName,
          "email" -> guest.email,
          "phone" -> guest.phoneNo
        ),

        "room" -> Json.obj(
          "id" -> room.id,
          "roomNumber" -> room.roomNo,
          "floor" -> room.floor,
          "isAvailable" -> true   // After checkout room becomes available
        )
      ).toString()


     println(json)
    producer.send(new ProducerRecord(topic, json))
  }
}

//racit@192 kafka_2.12-3.5.1 % bin/kafka-topics.sh --create --topic hotel_booking --bootstrap-server localhost:9092 --partitions 3 --replication-factor
