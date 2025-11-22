package consumer

import akka.actor.typed.ActorSystem
import service.EventCoordinatorActor
import service.EventCoordinatorActor.ProcessNotification
import model.EventNotification

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import java.util.Properties
import java.time.Duration
import scala.collection.JavaConverters._
import spray.json._
import DefaultJsonProtocol._

object EventKafkaConsumerApp {

  // JSON formats for converting to EventNotification
  implicit val eventNotificationFormat = jsonFormat10(EventNotification)

  def main(args: Array[String]): Unit = {

    // Start Actor System + Coordinator Actor
    implicit val system: ActorSystem[EventCoordinatorActor.Command] =
      ActorSystem(EventCoordinatorActor(), "EventSystem")

    val coordinator = system

    // Kafka config
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-management-consumer")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(java.util.Collections.singletonList("event-management"))

    println("🚀 Kafka Consumer + Akka Connected")

    while (true) {
      val records = consumer.poll(Duration.ofMillis(500))
      for (record <- records.asScala) {
        val msg = record.value()
        println(s"\n📩 RAW => $msg")

        try {
          // Convert JSON to EventNotification
          val event = msg.parseJson.convertTo[EventNotification]

          // send message to EventCoordinatorActor
          coordinator ! ProcessNotification(event)

          println(s"📤 Sent to Actor: ${event.event}")

        } catch {
          case e: Exception =>
            println(s"❌ JSON ERROR: ${e.getMessage}")
        }
      }
    }
  }
}













//package consumer
//
//import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
//import java.util.Properties
//import java.time.Duration
//import scala.collection.JavaConverters._
//import spray.json._
//import DefaultJsonProtocol._
//
//object EventKafkaConsumerApp {
//
//  def main(args: Array[String]): Unit = {
//
//    val props = new Properties()
//    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
//    props.put(ConsumerConfig.GROUP_ID_CONFIG, "event-management-consumer")
//    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
//    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
//    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//
//    val consumer = new KafkaConsumer[String, String](props)
//    consumer.subscribe(java.util.Collections.singletonList("event-management"))
//
//    println("🚀 Kafka Consumer Started...")
//
//    while (true) {
//      val records = consumer.poll(Duration.ofMillis(500))
//      for (record <- records.asScala) {
//        val msg = record.value()
//        println(s"\n📩 RAW => $msg")
//
//        try {
//          val json = msg.parseJson.asJsObject
//          val event = json.fields("event").convertTo[String]
//
//          event match {
//
//            case "EVENT_CREATED" =>
//              val data = json.fields("data").asJsObject
//              println(
//                s"""
//🔔 EVENT CREATED
//ID: ${data.fields("id")}
//Name: ${data.fields("name")}
//Type: ${data.fields("type")}
//Date: ${data.fields("date")}
//Guest Count: ${data.fields("guestCount")}
//                """
//              )
//
//            case "TASK_ASSIGNED" =>
//              val task = json.fields("task").asJsObject
//              val team = json.fields("team").asJsObject
//              println(
//                s"""
//🧾 TASK ASSIGNED
//Event ID: ${json.fields("eventId")}
//Team ID: ${json.fields("teamId")}
//Task ID: ${task.fields("taskId")}
//Task Name: ${task.fields("taskName")}
//Due Date: ${task.fields("dueDate")}
//Request: ${task.fields("specialRequest")}
//Team Name: ${team.fields("name")}
//Contact: ${team.fields("contact")}
//                """
//              )
//
//            case "REMINDER" =>
//              println(
//                s"""
//🔔 REMINDER
//Event ID: ${json.fields("eventId")}
//Team ID: ${json.fields("teamId")}
//Message: ${json.fields("message")}
//                """
//              )
//
//            case "TASK_PROGRESS_UPDATE" =>
//              println(
//                s"""
//🔁 TASK PROGRESS UPDATE
//Task ID: ${json.fields("taskId")}
//Team ID: ${json.fields("teamId")}
//Status: ${json.fields("status")}
//                """
//              )
//
//            case "ISSUE_REPORTED" =>
//              println(
//                s"""
//❗ ISSUE REPORTED
//Issue ID: ${json.fields("taskId")}
//Event ID: ${json.fields("eventId")}
//Message: ${json.fields("message")}
//Reported At: ${json.fields("reportedAt")}
//                """
//              )
//
//            case "EVENT_DAY_ALERT" =>
//              println(
//                s"""
//📢 EVENT DAY ALERT
//Event ID: ${json.fields("eventId")}
//Name: ${json.fields("name")}
//Date: ${json.fields("date")}
//                """
//              )
//
//            case _ =>
//              println(s"⚠ Unknown event type: $event")
//          }
//
//        } catch {
//          case e: Exception => println(s"❌ JSON ERROR: ${e.getMessage}")
//        }
//      }
//    }
//  }
//}
