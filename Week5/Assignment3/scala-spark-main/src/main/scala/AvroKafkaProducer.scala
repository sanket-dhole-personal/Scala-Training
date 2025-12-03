import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.avro.{Schema}
import org.apache.avro.io.{DatumWriter, EncoderFactory}
import org.apache.avro.generic.GenericDatumWriter
import scala.util.Random
import java.io.ByteArrayOutputStream

object AvroKafkaStreamer {

  def main(args: Array[String]): Unit = {

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer")

    val topic = "avro_topic"
    val producer = new KafkaProducer[String, Array[Byte]](props)

    // Load schema from resources
    val schemaStream = getClass.getResourceAsStream("/record.avsc")
    val schemaString = scala.io.Source.fromInputStream(schemaStream).mkString
    val schema = new Schema.Parser().parse(schemaString)

    while (true) {

      // Create random record
      val record: GenericRecord = new GenericData.Record(schema)
      record.put("id", java.util.UUID.randomUUID().toString)
      record.put("value", Random.nextInt(100))
      record.put("timestamp", System.currentTimeMillis())

      // Avro serialization
      val out = new ByteArrayOutputStream()
      val writer: DatumWriter[GenericRecord] = new GenericDatumWriter[GenericRecord](schema)
      val encoder = EncoderFactory.get().binaryEncoder(out, null)
      writer.write(record, encoder)
      encoder.flush()
      out.close()

      val avroBytes = out.toByteArray()

      // Send to Kafka
      val message = new ProducerRecord[String, Array[Byte]](topic, record.get("id").toString, avroBytes)
      producer.send(message)

      println(s"Sent: $record")

      Thread.sleep(3000)
    }
  }
}
