

case class Money(amount: Double) {

  private def roundToPrecision(value: Double)(implicit precision: Double): Double = {
    val rounded = Math.round(value / precision) * precision
    BigDecimal(rounded).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def +(that: Money)(implicit precision: Double): Money =
    Money(roundToPrecision(this.amount + that.amount))

  def -(that: Money)(implicit precision: Double): Money =
    Money(roundToPrecision(this.amount - that.amount))

  override def toString: String = f"Money(${amount}%.2f)"
}

object Money {
  // Default precision (optional)
  implicit val defaultPrecision: Double = 0.05
}

object PresitionOperator extends App {
  implicit val roundingPrecision: Double = 0.05

  val m1 = Money(10.23)
  val m2 = Money(5.19)

  println(m1 + m2) // Money(15.20)
  println(m1 - m2) // Money(5.05)

  {
    implicit val roundingPrecision: Double = 0.10
    println(m1 + m2) // Money(15.30)
  }
}
