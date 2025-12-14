package com.water.simulator

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import kafka.KafkaProducerService
import org.slf4j.LoggerFactory
import simulator.SimulatorActor

import scala.util.Try
import scala.io.StdIn

object SimulatorApp {
  private val log = LoggerFactory.getLogger(getClass)

  def start(): Unit = {
    val conf = ConfigFactory.load()
    val simConf = conf.getConfig("simulator.simulation")
    val numMeters = Try(simConf.getInt("numMeters")).getOrElse(10)
    val startId = Try(simConf.getInt("startHouseholdId")).getOrElse(1000)
    val intervalSeconds = Try(simConf.getInt("intervalSeconds")).getOrElse(10)

    log.info(s"Starting Simulator: numMeters=$numMeters startId=$startId intervalSeconds=$intervalSeconds")

    implicit val system: ActorSystem = ActorSystem("MeterSimulatorSystem")
    val producer = new KafkaProducerService()

    // create actors with meter ids WMTR-<id>
    val created = (0 until numMeters).map { i =>
      val hid = startId + i
      val mid = f"WMTR-${hid}%05d"
      system.actorOf(Props(new SimulatorActor(mid, hid, producer, intervalSeconds)), name = s"sim-$mid")
    }

    // simple shutdown handling: wait for ENTER
    log.info("Simulator running. Press ENTER to shutdown.")
    StdIn.readLine()
    log.info("Shutdown requested — stopping actors and closing producer")
    try {
      system.terminate()
    } catch {
      case ex: Exception => log.warn("Error terminating actor system", ex)
    }
  }
}

