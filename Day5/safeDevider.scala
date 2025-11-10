
object safeDevider extends App{
  def safeDivide(x: Int, y: Int): Option[Int] = {
    if (y == 0) None
    else Some(x / y)
  }

  val result = safeDivide(10, 2).getOrElse(-1)
  println(result) // -1
}

