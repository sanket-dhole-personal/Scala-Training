package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import models.Issue
import dao.IssueDAO
import play.api.libs.functional.syntax.toFunctionalBuilderOps

@Singleton
class IssueController @Inject()(
                                 cc: ControllerComponents,
                                 issueDAO: IssueDAO
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  import JsonFormats._
  import play.api.libs.json.OFormat

  import play.api.libs.json._
  import java.sql.Timestamp

  import JsonFormats._
  import play.api.libs.json.OFormat

  // formatter for Timestamp
  implicit val timestampFormat: Format[Timestamp] = new Format[Timestamp] {
    override def writes(ts: Timestamp): JsValue = JsString(ts.toString)
    override def reads(json: JsValue): JsResult[Timestamp] =
      json.validate[String].map(Timestamp.valueOf)
  }

  // ---------------- JSON Format (NO timestamp parsing from client) ----------------
  implicit val issueReads: Reads[Issue] = (
    (__ \ "id").readNullable[Int] and
      (__ \ "eventId").read[Int] and
      (__ \ "raisedBy").read[String] and
      (__ \ "description").read[String] and
      (__ \ "priority").read[String] and
      (__ \ "status").read[String]
    )(
    (id, eventId, raisedBy, description, priority, status) =>
      Issue(id, eventId, raisedBy, description, priority, status, None, None)
  )

  implicit val issueWrites = Json.writes[Issue]


  def list() = Action.async {
    issueDAO.list().map(issues => Ok(Json.toJson(issues)))
  }

  def get(id: Int) = Action.async {
    issueDAO.findById(id).map {
      case Some(i) => Ok(Json.toJson(i))
      case None    => NotFound(Json.obj("error" -> s"Issue with id $id not found"))
    }
  }

  def create() = Action.async(parse.json) { request =>
    request.body.validate[Issue].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      i => {
        val toCreate = i.copy(id = None)
        issueDAO.create(toCreate).map(created => Created(Json.toJson(created)))
      }
    )
  }

  def update(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Issue].fold(
      errs => Future.successful(BadRequest(JsError.toJson(errs))),
      issueData => {
        val updatedIssue = issueData.copy(id = Some(id))

        issueDAO.update(id, updatedIssue).map {
          case 0  => NotFound(Json.obj("error" -> s"Issue with id $id not found"))
          case _  => Ok(Json.toJson(updatedIssue))
        }
      }
    )
  }


  def delete(id: Int) = Action.async {
    issueDAO.delete(id).map {
      case 0 => NotFound(Json.obj("error" -> s"Issue with id $id not found"))
      case _ => NoContent
    }
  }
}
