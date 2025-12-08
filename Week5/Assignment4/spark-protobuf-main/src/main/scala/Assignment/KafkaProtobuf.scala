package Assignment

import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.functions._


object KafkaProtobuf {
//  def main(args: Array[String]): Unit = {
//
//    val spark = SparkSession.builder()
//      .appName("Kafka Protobuf Example")
//      .master("local[*]")
//      .getOrCreate()
//
//    import spark.implicits._
//
//    // ===============================
//    // 1. Read Kafka Stream
//    // ===============================
//    val kafkaDF = spark.read
//      .format("kafka")
//      .option("kafka.bootstrap.servers", "localhost:9092")
//      .option("subscribe", "user-events")
//      .option("startingOffsets", "earliest")
//      .load()
//
//    val bytesDF = kafkaDF.select($"value")
//
//    // ===============================
//    // 2. Deserialize Protobuf
//    // ===============================
//    val decodeUdf = udf((bytes: Array[Byte]) => {
//      try {
//        val event = UserEvent.parseFrom(bytes)
//        Row(event.getUserId, event.getAction, event.getValue)
//      } catch {
//        case _: Throwable => Row(null, null, 0.0)
//      }
//    })
//
//    val schema = Seq("userId", "action", "value")
//
//    val eventDF = bytesDF
//      .withColumn("decoded", decodeUdf($"value"))
//      .selectExpr(
//        "decoded.userId as userId",
//        "decoded.action as action",
//        "decoded.value as value"
//      )
//
//    // ===============================
//    // 3. Event Count per Action
//    // ===============================
//    val actionCount = eventDF.groupBy("action").count()
//    actionCount.show(false)
//
//    // ===============================
//    // 4. Top 5 Users by Value
//    // ===============================
//    val topUsers = eventDF
//      .groupBy("userId")
//      .agg(sum("value").as("totalValue"))
//      .orderBy(desc("totalValue"))
//      .limit(5)
//
//    topUsers.show(false)
//
//    spark.stop()
//  }
}

