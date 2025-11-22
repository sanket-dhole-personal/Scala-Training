package controllers

import javax.inject._
import play.api.mvc._
import models.{Notification, NotificationDAO}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NotificationController @Inject()(notificationDAO: NotificationDAO, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val notificationFormat: OFormat[Notification]  = Json.format[Notification]

  def getAllNotifications = Action.async {
    notificationDAO.list().map(notifications => Ok(Json.toJson(notifications)))
  }

  def getNotification(id: Int) = Action.async {
    notificationDAO.getById(id).map {
      case Some(notification) => Ok(Json.toJson(notification))
      case None               => NotFound(Json.obj("error" -> "Notification not found"))
    }
  }

  def createNotification = Action.async(parse.json) { request =>
    request.body.validate[Notification].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),
      notification => notificationDAO.create(notification).map(_ =>
        Created(Json.obj("message" -> "Notification created"))
      )
    )
  }

  def updateNotification(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Notification].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),
      notification =>
        notificationDAO.update(notification).map(_ =>
          Ok(Json.obj("message" -> "Notification updated"))
        )
    )
  }

  def deleteNotification(id: Int) = Action.async {
    notificationDAO.delete(id).map(_ => Ok(Json.obj("message" -> "Notification deleted")))
  }
}
