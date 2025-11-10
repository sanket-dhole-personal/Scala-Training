
object ShapeAnalizer extends App{
    case class Circle(r: Double)
    case class Rectangle(w: Double, h: Double)

    def area(shape: Any): Double = shape match {
       case Circle(r) => math.Pi * r * r           // Area of a circle
       case Rectangle(w, h) => w * h               // Area of a rectangle
       case _ => -1.0                              // Default case
    }


    println(area(Circle(3)))       // 28.274333882308138
    println(area(Rectangle(4, 5))) // 20.0
    println(area("triangle"))      // -1.0
    
  

}