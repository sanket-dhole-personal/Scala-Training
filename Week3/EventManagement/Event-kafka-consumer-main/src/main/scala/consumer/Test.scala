package consumer

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.scaladsl.Sink
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

object TestKafkaConsumer {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem[Nothing] =
      ActorSystem[Nothing](Behaviors.empty, "testKafka")

    val consumerSettings =
      ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId("test-group")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    Consumer
      .plainSource(consumerSettings, Subscriptions.topics("event-management"))
      .map(_.value())
      .runWith(Sink.foreach(msg => println(s"📌 RAW KAFKA => $msg")))
  }
}
