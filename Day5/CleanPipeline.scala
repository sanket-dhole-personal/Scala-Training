
import scala.util.Try

object CleanPipeline extends App {
  val data = List("10", "20", "x", "30")

  val result = data
    .map(s => Try(s.toInt))
    .flatMap(_.toOption)
    .map(n => n * n)

  println(result)  // List(100, 400, 900)
}
