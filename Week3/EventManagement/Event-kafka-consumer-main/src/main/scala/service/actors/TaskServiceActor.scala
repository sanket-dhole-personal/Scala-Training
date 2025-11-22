package service.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import model.EventNotification
import utils.EmailHelper

object TaskServiceActor {

  sealed trait Command
  final case class NotifyTaskAssignment(evt: EventNotification) extends Command
  final case class UpdateTaskStatus(evt: EventNotification) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case NotifyTaskAssignment(evt) =>
          val subject = "New Task Assigned"
          val body =
            s"""
               |📝 NEW TASK ASSIGNED
               |
               |Task : ${evt.taskDescription.getOrElse("")}
               |Assigned To Team : ${evt.teamId.getOrElse("")}
               |Status : ${evt.status.getOrElse("")}
               |
               |Deadline : ${evt.deadline.getOrElse("N/A")}
               |""".stripMargin
          evt.message.foreach(email => EmailHelper.sendEmail(email, subject, body))

        case UpdateTaskStatus(evt) =>
          val subject = "Task Status Updated"
          val body =
            s"""
               |🔄 TASK STATUS UPDATED
               |
               |Task ID : ${evt.taskId.getOrElse("")}
               |Status : ${evt.status.getOrElse("")}
               |
               |Reported At : ${evt.reportedAt.getOrElse("N/A")}
               |""".stripMargin
          evt.message.foreach(email => EmailHelper.sendEmail(email, subject, body))
      }
      Behaviors.same
    }
}
