import scala.collection.mutable.ListBuffer
object Building2DMultiplication extends App{

    def multiplicationTable(n: Int): List[String] = {
        (for {
         i <- 1 to n
          j <- 1 to n
        } yield s"$i x $j = ${i * j}").toList
}

// Test
    val table = multiplicationTable(3)
    table.foreach(println)


}