
case class Rational(n: Int, d: Int) {
  require(d != 0, "Denominator cannot be zero")

  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  private val g = gcd(math.abs(n), math.abs(d))
  val num: Int = n / g
  val den: Int = d / g

  // Rational / Rational
  def /(that: Rational): Rational =
    Rational(this.num * that.den, this.den * that.num)

  override def toString: String = s"Rational($num,$den)"
}

object Rational {
  // Implicit conversion from Int to Rational
  implicit def intToRational(x: Int): Rational = Rational(x, 1)
}

object ChainedImplicite extends App {
  import Rational._

  val r = 1 / Rational(2, 3)
  println(r) // Rational(3,2)

  // Rational division
  val r2 = Rational(3, 4) / Rational(2, 5)
  println(r2) // Rational(15,8)

  // Normal Int division still works
  println(4 / 2) // 2  (no ambiguity!)
}
