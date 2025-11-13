error id: file://<WORKSPACE>/Day5/ChainingDepartments.scala:scala/Predef.String#
file://<WORKSPACE>/Day5/ChainingDepartments.scala
empty definition using pc, found symbol in pc: scala/Predef.String#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -scala/concurrent/String#
	 -scala/concurrent/duration/String#
	 -scala/util/String#
	 -String#
	 -scala/Predef.String#
offset: 217
uri: file://<WORKSPACE>/Day5/ChainingDepartments.scala
text:
```scala

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util._

object ChainingDepartments extends App {

  def getUser(id: Int): Future[Str@@ing] = Future {
    s"User$id"
  }

  def getOrders(user: String): Future[List[String]] = Future {
    List(s"$user-order1", s"$user-order2")
  }

  def getOrderTotal(order: String): Future[Double] = Future {
    scala.util.Random.between(10.0, 100.0)
  }

  val totalFuture = for {
    user   <- getUser(42)
    orders <- getOrders(user)
    totals <- Future.sequence(orders.map(getOrderTotal))
  } yield totals.sum

  totalFuture.onComplete {
    case Success(total) => println(f"Total amount for user 42 = $$${total}%.2f")
    case Failure(e)     => println(s"Failed: ${e.getMessage}")
  }

  Await.result(totalFuture, 3.seconds)
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/Predef.String#