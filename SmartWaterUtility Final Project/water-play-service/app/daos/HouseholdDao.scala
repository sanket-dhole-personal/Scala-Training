package daos

import models._
import javax.inject._
import play.api.db._
import anorm._
import anorm.SqlParser._

@Singleton
class HouseholdDao @Inject()(db: Database) {

  val parser: RowParser[Household] =
    long("household_id") ~
      long("customer_id") ~
      str("address") ~
      str("district") ~
      str("state") ~
      str("pincode") ~
      str("meter_id") map {
      case a ~ b ~ c ~ d ~ e ~ f ~ g =>
        Household(a,b,c,d,e,f,g)
    }

  def getHousehold(id: Long): Option[Household] =
    db.withConnection { implicit c =>
      SQL"SELECT * FROM household WHERE household_id=$id"
        .as(parser.singleOpt)
    }
}
