package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models.Event
import dao.EventDAO

@Singleton
class EventController @Inject()(
                                 cc: ControllerComponents,
                                 eventDAO: EventDAO
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JsonFormats._
  import play.api.libs.json.OFormat

  implicit val eventFormat: OFormat[Event] = Json.format[Event]

  def list() = Action.async {
    eventDAO.list().map(events => Ok(Json.toJson(events)))
  }

  def get(id: Int) = Action.async {
    eventDAO.get(id).map {
      case Some(event) => Ok(Json.toJson(event))
      case None        => NotFound(Json.obj("error" -> s"Event with id $id not found"))
    }
  }


  def create() = Action.async(parse.json) { request =>
    request.body.validate[Event].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      ev => {
        // ignore client-provided id if present
        val toCreate = ev.copy(id = None)
        eventDAO.create(toCreate).map(created => Created(Json.toJson(created)))
      }
    )
  }

  def update(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Event].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      ev => {
        val withId = ev.copy(id = Some(id))
        eventDAO.update(id, withId).map {
          case 0 => NotFound(Json.obj("error" -> s"Event with id $id not found"))
          case _ => Ok(Json.toJson(withId))
        }
      }
    )
  }

  def delete(id: Int) = Action.async {
    eventDAO.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> s"Event with id $id not found"))
      case _ => NoContent
    }
  }
}
