package daos

import models._
import javax.inject._
import play.api.db._
import anorm._
import anorm.SqlParser._

@Singleton
class CustomerDao @Inject()(db: Database) {

  val parser: RowParser[Customer] =
    long("customer_id") ~
      str("name") ~
      str("email") ~
      str("phone") map {
      case a ~ b ~ c ~ d =>
        Customer(a,b,c,d)
    }

  def getCustomer(id: Long): Option[Customer] =
    db.withConnection { implicit c =>
      SQL"SELECT * FROM customer WHERE customer_id=$id"
        .as(parser.singleOpt)
    }
}
