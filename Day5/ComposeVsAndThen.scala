
object ComposeVsAndThen extends App {
  val trim: String => String = _.trim
  val toInt: String => Int = _.toInt
  val doubleIt: Int => Int = _ * 2

  // Using andThen: left to right
  val pipeline1 = trim andThen toInt andThen doubleIt
  println(pipeline1(" 21 "))  // 42

  // Using compose: right to left
  val pipeline2 = doubleIt compose toInt compose trim
  println(pipeline2(" 21 "))  // 42

  // Difference in direction
  // f andThen g = g(f(x))
  // f compose g = f(g(x))
}
