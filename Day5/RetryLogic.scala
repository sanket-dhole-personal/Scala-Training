
object RetryLogic extends App {

  def fetchData(): Int = {
    val n = scala.util.Random.nextInt(3)
    if (n == 0) throw new RuntimeException("Network fail")
    println(s"Fetched: $n")
    n
  }

  def retry[T](times: Int)(op: => T): Option[T] = {
    try {
      Some(op)
    } catch {
      case _: Exception =>
        if (times > 1) {
          println(s"Retrying... attempts left: ${times - 1}")
          retry(times - 1)(op)
        } else {
          println("All retries failed.")
          None
        }
    }
  }

  val data = retry(3)(fetchData())
  println(s"Final result: $data")
}
