
object MergeGame extends App{

    val a = List(1, 2)
    val b = List(3, 4)
    val v = Vector(5, 6)

    val c1 = a ++ b
    val c2 = a ::: b
    val combined = a ++ b ++ v

    println(s"c1 = $c1")        // List(1, 2, 3, 4)
    println(s"c2 = $c2")        // List(1, 2, 3, 4)
    println(s"combined = $combined") // List(1, 2, 3, 4, 5, 6)


}