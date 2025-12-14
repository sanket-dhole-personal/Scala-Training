package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import daos._

@Singleton
class CustomerController @Inject()(
                                    customerDao: CustomerDao,
                                    householdDao: HouseholdDao,
                                    cc: ControllerComponents
                                  ) extends AbstractController(cc) {

  def getCustomer(id: Long) = Action {

    val cust = customerDao.getCustomer(id)
    val households =
      cust.map(c => householdDao.getHousehold(c.customer_id))

    Ok(Json.obj(
      "customer" -> cust,
      "households" -> households
    ))
  }
}
