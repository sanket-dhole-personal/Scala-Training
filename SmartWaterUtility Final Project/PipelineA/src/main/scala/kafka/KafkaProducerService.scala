package kafka

import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata, Callback}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

class KafkaProducerService {
  private val log = LoggerFactory.getLogger(getClass)
  private val conf = ConfigFactory.load()
  private val brokers = conf.getString("simulator.kafka.brokers")
  private val acks = conf.getString("simulator.kafka.acks")
  val topic: String = conf.getString("simulator.kafka.topic")

  private val props = new Properties()
  props.put("bootstrap.servers", brokers)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("acks", acks)

  private val producer = new KafkaProducer[String, String](props)

  def send(key: String, value: String): Unit = {
    val rec = new ProducerRecord[String, String](topic, key, value)
    producer.send(rec, new Callback {
      override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
        if (exception != null) {
          log.error("Failed to send message to kafka", exception)
        } else {
          // minimal debug: you can uncomment if noisy logs ok
          // log.debug(s"Sent to ${metadata.topic()}@${metadata.partition()}:${metadata.offset()}")
        }
      }
    })
  }

//  def close(): Unit = {
//    try {
//      producer.flush()
//      producer.close()
//      log.info("Kafka producer closed")
//    } catch {
//      case ex: Exception => log.warn("Error while closing producer", ex)
//    }
//  }
}
