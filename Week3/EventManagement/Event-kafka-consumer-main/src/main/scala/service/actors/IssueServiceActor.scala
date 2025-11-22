package service.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import model.EventNotification
import utils.EmailHelper

object IssueServiceActor {

  sealed trait Command
  final case class NotifyIssue(evt: EventNotification) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {
        case NotifyIssue(evt) =>
          val subject = "⚠ Issue Reported"
          val body =
            s"""
               |An issue has been reported!
               |
               |Task ID: ${evt.taskId.getOrElse("")}
               |Event ID: ${evt.eventId}
               |Message: ${evt.message.getOrElse("")}
               |Reported At: ${evt.reportedAt.getOrElse("")}
            """.stripMargin

          EmailHelper.sendEmail("manager@example.com", subject, body)
      }
      Behaviors.same
    }
}
