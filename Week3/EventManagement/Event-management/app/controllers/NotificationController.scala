package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models.Notification
import dao.NotificationDAO

@Singleton
class NotificationController @Inject()(
                                        cc: ControllerComponents,
                                        notificationDAO: NotificationDAO
                                      )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JsonFormats._
  import play.api.libs.json.OFormat

  implicit val notificationFormat: OFormat[Notification] = Json.format[Notification]

  def list() = Action.async {
    notificationDAO.list().map(ns => Ok(Json.toJson(ns)))
  }

  def get(id: Int) = Action.async {
    notificationDAO.get(id).map {
      case Some(n) => Ok(Json.toJson(n))
      case None    => NotFound(Json.obj("error" -> s"Notification with id $id not found"))
    }
  }

  def create() = Action.async(parse.json) { request =>
    request.body.validate[Notification].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      n => {
        val toCreate = n.copy(id = None)
        notificationDAO.create(toCreate).map(created => Created(Json.toJson(created)))
      }
    )
  }

  def delete(id: Int) = Action.async {
    notificationDAO.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> s"Notification with id $id not found"))
      case _ => NoContent
    }
  }
}
