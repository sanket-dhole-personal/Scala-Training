package service.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import model.EventNotification
import utils.EmailHelper

object ReminderServiceActor {

  sealed trait Command
  final case class SendReminder(evt: EventNotification) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {
        case SendReminder(evt) =>
          val subject = "Event Reminder"
          val body =
            s"""
               |⏰ REMINDER
               |
               |Team Id : ${evt.teamId.getOrElse("")}
               |Message : ${evt.message.getOrElse("")}
               |
               |Deadline : ${evt.deadline.getOrElse("N/A")}
               |Reported At : ${evt.reportedAt.getOrElse("N/A")}
               |""".stripMargin
          evt.message.foreach(email => EmailHelper.sendEmail(email, subject, body))
      }
      Behaviors.same
    }
}
