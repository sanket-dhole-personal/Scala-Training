package service.actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior
import model.EventNotification
import utils.EmailHelper

object AlertServiceActor {

  sealed trait Command
  final case class SendEventCreatedAlert(evt: EventNotification) extends Command
  final case class SendEventDayAlert(evt: EventNotification) extends Command

  def apply(): Behavior[Command] =
    Behaviors.receive { (_, msg) =>
      msg match {

        case SendEventCreatedAlert(evt) =>
          val subject = s"Event Created: ${evt.eventName.getOrElse("")}"
          val body =
            s"""
               |📌 EVENT CREATED
               |
               |Event Id : ${evt.eventId.getOrElse("")}
               |Event Name : ${evt.eventName.getOrElse("")}
               |
               |Event has been created successfully.
               |Get ready for the planning.
               |
               |Reported At : ${evt.reportedAt.getOrElse("N/A")}
               |""".stripMargin
          evt.message.foreach(email => EmailHelper.sendEmail(email, subject, body))

        case SendEventDayAlert(evt) =>
          val subject = s"Event Today: ${evt.eventName.getOrElse("")}"
          val body =
            s"""
               |🚨 EVENT DAY ALERT
               |
               |Event Id : ${evt.eventId.getOrElse("")}
               |Event Name : ${evt.eventName.getOrElse("")}
               |
               |Event starts today — all teams must be ready.
               |
               |Reported At : ${evt.reportedAt.getOrElse("N/A")}
               |""".stripMargin
          evt.message.foreach(email => EmailHelper.sendEmail(email, subject, body))
      }
      Behaviors.same
    }
}
