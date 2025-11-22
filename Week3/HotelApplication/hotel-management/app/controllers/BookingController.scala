package controllers

import javax.inject._
import play.api.mvc._
import models.{Booking, BookingDAO}
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookingController @Inject()(bookingDAO: BookingDAO, cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  implicit val bookingFormat : OFormat[Booking] = Json.format[Booking]


  def getAllBookings = Action.async {
    bookingDAO.list().map(bookings => Ok(Json.toJson(bookings)))
  }

  def getBooking(id: Int) = Action.async {
    bookingDAO.getById(id).map {
      case Some(booking) => Ok(Json.toJson(booking))
      case None          => NotFound(Json.obj("error" -> "Booking not found"))
    }
  }

  def createBooking = Action.async(parse.json) { request =>
    request.body.validate[Booking].fold(
      _ => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),

      booking =>
        bookingDAO.create(booking)
          .map(_ => Created(Json.obj("message" -> "Booking created")))
          .recover {
            case ex: Exception =>
              Ok(Json.obj("message" -> ex.getMessage))   // Normal message, NOT an error
          }
    )
  }


  def updateBooking(id: Int) = Action.async(parse.json) { request =>
    request.body.validate[Booking].fold(
      errors => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON"))),
      booking => bookingDAO.update(booking).map(_ => Ok(Json.obj("message" -> "Booking updated")))
    )
  }

  def deleteBooking(id: Int) = Action.async {
    bookingDAO.delete(id).map(_ => Ok(Json.obj("message" -> "Booking deleted")))
  }

  def checkout(id: Int) = Action.async {
    bookingDAO.checkoutBooking(id).map { _ =>
      Ok(Json.obj("message" -> s"Checkout successful for booking $id"))
    }.recover {
      case e: Exception => BadRequest(Json.obj("error" -> e.getMessage))
    }
  }

}
