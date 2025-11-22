package services

import javax.inject._
import play.api.libs.json.Json
import com.google.inject.Singleton
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import scala.concurrent.{ExecutionContext, Future}
import models.{Event, Team, Task, Issue}

@Singleton
class EventKafkaProducer @Inject() ()(implicit ec: ExecutionContext) {

  private val topic = "event-management"

  private val props = new java.util.Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String, String](props)

  /** EVENT CREATED */
  def sendEventCreated(event: Event): Future[Unit] = Future {
    val json = Json.obj(
      "event" -> "EVENT_CREATED",
      "data" -> Json.obj(
        "id" -> event.id,
        "name" -> event.name,
        "type" -> event.eventType,
        "date" -> event.eventDate.toString,
        "guestCount" -> event.guestCount
      )
    ).toString()

    println("[KAFKA] EVENT_CREATED => " + json)
    producer.send(new ProducerRecord(topic, json))
  }

  /** TASK ASSIGNED TO TEAM */
  def sendTaskAssigned(event: Event, team: Team, task: Task): Future[Unit] = Future {
    val json = Json.obj(
      "event" -> "TASK_ASSIGNED",
      "eventId" -> event.id,
      "teamId" -> team.id,
      "task" -> Json.obj(
        "taskId" -> task.id,
        "taskName" ->task.taskName,
        "dueDate" -> task.dueDate.toString,
        "specialRequest" -> task.specialRequests
      ),
      "team" -> Json.obj(
        "name" -> team.name,
        "contact" -> team.phoneNo
      )
    ).toString()

    println("[KAFKA] TASK_ASSIGNED => " + json)
    producer.send(new ProducerRecord(topic, json))
  }

  /** REMINDER */
  def sendReminder(eventId: Int, teamId: Int, message: String): Future[Unit] = Future {
    val json = Json.obj(
      "event" -> "REMINDER",
      "eventId" -> eventId,
      "teamId" -> teamId,
      "message" -> message
    ).toString()

    println("[KAFKA] REMINDER => " + json)
    producer.send(new ProducerRecord(topic, json))
  }

  /** TASK PROGRESS UPDATE */
  def sendTaskProgressUpdate(taskId: Int, teamId: Int, status: String): Future[Unit] = Future {
    val json = Json.obj(
      "event" -> "TASK_PROGRESS_UPDATE",
      "taskId" -> taskId,
      "teamId" -> teamId,
      "status" -> status
    ).toString()

    println("[KAFKA] TASK_PROGRESS_UPDATE => " + json)
    producer.send(new ProducerRecord(topic, json))
  }

  /** ISSUE REPORTED BY TEAM */
  def sendIssueReported(issue: Issue): Future[Unit] = Future {
    val json = Json.obj(
      "event" -> "ISSUE_REPORTED",
      "taskId" -> issue.id,
      "eventId" -> issue.eventId,
      "message" -> issue.description,
      "reportedAt" -> issue.createdAt.toString
    ).toString()

    println("[KAFKA] ISSUE_REPORTED => " + json)
    producer.send(new ProducerRecord(topic, json))
  }

  /** EVENT DAY ALERT */
  def sendEventDayAlert(event: Event): Future[Unit] = Future {
    val json = Json.obj(
      "event" -> "EVENT_DAY_ALERT",
      "eventId" -> event.id,
      "name" -> event.name,
      "date" -> event.eventDate.toString
    ).toString()

    println("[KAFKA] EVENT_DAY_ALERT => " + json)
    producer.send(new ProducerRecord(topic, json))
  }
}
