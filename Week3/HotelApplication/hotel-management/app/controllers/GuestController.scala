package controllers

import javax.inject._
import play.api.mvc._
import models.{Guest, GuestDAO}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GuestController @Inject()(guestDAO: GuestDAO, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val guestFormat: OFormat[Guest]  = Json.format[Guest]

  def getAllGuests = Action.async {
    guestDAO.list().map(guests => Ok(Json.toJson(guests)))
  }

  def getGuest(id: Int) = Action.async {
    guestDAO.getById(id).map {
      case Some(guest) => Ok(Json.toJson(guest))
      case None        => NotFound(Json.obj("error" -> "Guest not found"))
    }
  }

  def createGuest = Action.async(parse.json) { request =>
    request.body.validate[Guest].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),
      guest => guestDAO.create(guest.id, guest.fullName, guest.email, guest.phoneNo, guest.createdAt, guest.idProof).map(_ => Created(Json.obj("message" -> "Guest created")))
    )
  }

  def updateGuest(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Guest].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),
      guest => guestDAO.update(id, guest.fullName, guest.email, guest.phoneNo, guest.createdAt, guest.idProof).map(_ => Ok(Json.obj("message" -> "Guest updated")))
    )
  }

  def deleteGuest(id: Int) = Action.async {
    guestDAO.delete(id).map(_ => Ok(Json.obj("message" -> "Guest deleted")))
  }
}
