
object StringDSL {
  implicit class RichString(val s: String) extends AnyVal {

    // Repeat string n times
    def *(n: Int): String = s * n

    // Concatenate with a space
    def ~(other: String): String = s + " " + other
  }
}

object ImplicitePlusOperator extends App {
  import StringDSL._

  println("Hi" * 3)        // HiHiHi
  println("Hello" ~ "World") // Hello World
  println("Scala" ~ "Rocks" ~ "Forever") // Scala Rocks Forever
}
