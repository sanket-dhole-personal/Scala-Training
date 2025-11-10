


case class Vec2D(x: Double, y: Double) {

  // Vector addition
  def +(that: Vec2D): Vec2D = Vec2D(this.x + that.x, this.y + that.y)

  // Vector subtraction
  def -(that: Vec2D): Vec2D = Vec2D(this.x - that.x, this.y - that.y)

  // Scalar multiplication
  def *(scalar: Double): Vec2D = Vec2D(this.x * scalar, this.y * scalar)

  // Nice string output
  override def toString: String = s"Vec2D($x,$y)"
}

// Companion object
object Vec2D {
  // Implicit conversion: Int → IntOps
  implicit class IntOps(val scalar: Int) extends AnyVal {
    def *(v: Vec2D): Vec2D = Vec2D(v.x * scalar, v.y * scalar)
  }
}

// Test it out
object VectorAlgebra extends App{
  val v1 = Vec2D(2, 3)
  val v2 = Vec2D(4, 1)

  println(v1 + v2)  // Vec2D(6.0,4.0)
  println(v1 - v2)  // Vec2D(-2.0,2.0)
  println(v1 * 3)   // Vec2D(6.0,9.0)
  println(3 * v1)   // Vec2D(6.0,9.0)
}

