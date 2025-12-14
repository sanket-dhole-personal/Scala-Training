package simulator

import akka.actor.{Actor, Cancellable}

import kafka.KafkaProducerService
import model.MeterEvent
import org.slf4j.LoggerFactory
import simulator.MeterGenerator

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

class SimulatorActor(
                      meterId: String,
                      householdId: Int,
                      producer: KafkaProducerService,
                      intervalSeconds: Int
                    ) extends Actor {
  private val log = LoggerFactory.getLogger(getClass)
  implicit val ec: ExecutionContext = context.dispatcher
  private var tickTask: Option[Cancellable] = None

  override def preStart(): Unit = {
    // schedule periodic ticks
    tickTask = Some(
      context.system.scheduler.scheduleWithFixedDelay(
        initialDelay = 0.seconds,
        delay = intervalSeconds.seconds,
        receiver = self,
        message = "tick"
      )
    )
    log.info(s"SimulatorActor[$meterId] started (household=$householdId) interval=${intervalSeconds}s")
  }

  override def postStop(): Unit = {
    tickTask.foreach(_.cancel())
    log.info(s"SimulatorActor[$meterId] stopped")
  }

  def receive: Receive = {
    case "tick" =>
      val reading: MeterEvent = MeterGenerator.generateReading(meterId, householdId)
      val json = MeterEvent.toJson(reading)
      producer.send(reading.meter_id, json)
      // lightweight console output for dev
      println(s"[sim] sent: ${reading.meter_id} -> ${reading.consumption_liters}L @ ${reading.timestamp}")
  }
}


