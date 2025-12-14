package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import daos._
import services._

@Singleton
class HouseholdController @Inject()(
                                     householdDao: HouseholdDao,
                                     billingDao: BillingDao,
                                     jsonService: JsonReadingService,
                                     avroService: AvroSummaryService,
                                     cc: ControllerComponents
                                   ) extends AbstractController(cc) {

  def recentReadings(id: Long) = Action {
    Ok(Json.toJson(jsonService.readRecent(id)))
  }

  def dailyUsage(id: Long) = Action {
    Ok(Json.toJson(avroService.readDailyUsage(id)))
  }

  def billing(id: Long) = Action {
    Ok(Json.toJson(billingDao.getBills(id)))
  }
}
