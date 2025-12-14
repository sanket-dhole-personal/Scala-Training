package daos

import models._

import javax.inject._
import play.api.db._
import anorm._
import anorm.SqlParser._

import java.time.LocalDate

@Singleton
class BillingDao @Inject()(db: Database) {

  val parser: RowParser[BillingHistory] =
    long("bill_id") ~
      long("household_id") ~
      get[LocalDate]("billing_month") ~     // <-- FIX
      double("total_consumption_liters") ~
      double("total_amount") map {
      case a ~ b ~ c ~ d ~ e =>
        BillingHistory(a,b,c,d,e)
    }

  def getBills(householdId: Long): List[BillingHistory] =
    db.withConnection { implicit c =>
      SQL"SELECT * FROM billing_history WHERE household_id=$householdId"
        .as(parser.*)
    }
}
