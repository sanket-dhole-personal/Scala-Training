package controllers

import javax.inject._
import play.api.mvc._
import models.{Room, RoomDAO}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RoomController @Inject()(roomDAO: RoomDAO, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

//  implicit val roomFormat = Json.format[Room]
   implicit val roomFormat: OFormat[Room] = Json.format[Room]

  def getAllRooms = Action.async {
    roomDAO.list().map(rooms => Ok(Json.toJson(rooms)))
  }

  def getRoom(id: Int) = Action.async {
    roomDAO.getById(id).map {
      case Some(room) => Ok(Json.toJson(room))
      case None       => NotFound(Json.obj("error" -> "Room not found"))
    }
  }

  def createRoom = Action.async(parse.json) { request =>
    request.body.validate[Room].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),
      room => {
        roomDAO
          .create(room.id, room.roomNo, room.floor, room.category, room.price, room.isAvailable)
          .map(x => Created(Json.obj("message" -> "Room created" )))
      }
    )
  }




  def updateRoom(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Room].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),
      room => roomDAO.update(id, room.roomNo, room.floor, room.category, room.price, room.isAvailable)).map(_ => Ok(Json.obj("message" -> "Room updated"))
    )
  }

  def deleteRoom(id: Int) = Action.async {
    roomDAO.delete(id).map(_ => Ok(Json.obj("message" -> "Room deleted")))
  }
}
