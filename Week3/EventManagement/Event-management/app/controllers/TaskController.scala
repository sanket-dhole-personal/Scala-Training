package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models.Task
import dao.TaskDAO

@Singleton
class TaskController @Inject()(
                                cc: ControllerComponents,
                                taskDAO: TaskDAO
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JsonFormats._
  import play.api.libs.json.OFormat

  implicit val taskFormat: OFormat[Task] = Json.format[Task]

  def list() = Action.async {
    taskDAO.list().map(tasks => Ok(Json.toJson(tasks)))
  }

  def get(id: Int) = Action.async {
    taskDAO.get(id).map {
      case Some(t) => Ok(Json.toJson(t))
      case None    => NotFound(Json.obj("error" -> s"Task with id $id not found"))
    }
  }

  def create() = Action.async(parse.json) { request =>
    request.body.validate[Task].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      t => {
        val toCreate = t.copy(id = None)
        taskDAO.create(toCreate).map(created => Created(Json.toJson(created)))
      }
    )
  }

  def update(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Task].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      t => {
        val withId = t.copy(id = Some(id))
        taskDAO.update(id, withId).map {
          case 0 => NotFound(Json.obj("error" -> s"Task with id $id not found"))
          case _ => Ok(Json.toJson(withId))
        }
      }
    )
  }


  def delete(id: Int) = Action.async {
    taskDAO.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> s"Task with id $id not found"))
      case _ => NoContent
    }
  }

  def updateStatus(taskId: Int) = Action.async(parse.json) { request =>
    (request.body \ "status").asOpt[String] match {
      case Some(status) =>
        taskDAO.updateStatus(taskId, status).map { rows =>
          if (rows > 0) Ok(Json.obj("success" -> true, "message" -> "Status updated"))
          else NotFound(Json.obj("success" -> false, "message" -> "Task not found"))
        }

      case None =>
        Future.successful(BadRequest(Json.obj("error" -> "Missing status")))
    }
  }
}
