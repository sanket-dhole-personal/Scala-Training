package service

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import model.EventNotification
import service.actors._

object EventCoordinatorActor {

  sealed trait Command
  final case class ProcessNotification(evt: EventNotification) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { ctx =>
      val alertActor    = ctx.spawn(AlertServiceActor(), "alertActor")
      val issueActor    = ctx.spawn(IssueServiceActor(), "issueActor")
      val reminderActor = ctx.spawn(ReminderServiceActor(), "reminderActor")
      val taskActor     = ctx.spawn(TaskServiceActor(), "taskActor")

      Behaviors.receiveMessage {
        case ProcessNotification(evt) =>
          evt.event match {
            case "EVENT_CREATED"     => alertActor    ! AlertServiceActor.SendEventCreatedAlert(evt)
            case "EVENT_DAY"         => alertActor    ! AlertServiceActor.SendEventDayAlert(evt)
            case "TASK_ASSIGNED"     => taskActor     ! TaskServiceActor.NotifyTaskAssignment(evt)
            case "TASK_UPDATED"      => taskActor     ! TaskServiceActor.UpdateTaskStatus(evt)
            case "ISSUE_REPORTED"    => issueActor    ! IssueServiceActor.NotifyIssue(evt)
            case "REMINDER"          => reminderActor ! ReminderServiceActor.SendReminder(evt)
            case other               => println(s"⚠ Unknown event type: $other")
          }
          Behaviors.same
      }
    }
}
