package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import models.Team
import dao.TeamDAO

@Singleton
class TeamController @Inject()(
                                cc: ControllerComponents,
                                teamDAO: TeamDAO
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JsonFormats._
  import play.api.libs.json.OFormat

  implicit val teamFormat: OFormat[Team] = Json.format[Team]

  def list() = Action.async {
    teamDAO.list().map(teams => Ok(Json.toJson(teams)))
  }

  def get(id: Int) = Action.async {
    teamDAO.get(id).map {
      case Some(t) => Ok(Json.toJson(t))
      case None    => NotFound(Json.obj("error" -> s"Team with id $id not found"))
    }
  }

  def create() = Action.async(parse.json) { request =>
    request.body.validate[Team].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      tm => {
        val toCreate = tm.copy(id = None)
        teamDAO.create(toCreate).map(created => Created(Json.toJson(created)))
      }
    )
  }

  def update(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Team].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      tm => {
        val withId = tm.copy(id = Some(id))
        teamDAO.update(id, withId).map {
          case 0 => NotFound(Json.obj("error" -> s"Team with id $id not found"))
          case _ => Ok(Json.toJson(withId))
        }
      }
    )
  }

  def delete(id: Int) = Action.async {
    teamDAO.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> s"Team with id $id not found"))
      case _ => NoContent
    }
  }
}
