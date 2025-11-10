
object ApplyEvaluator extends App{
    object Evaluator {
    def apply(block: => Any): Unit = {
    println(s"Evaluating block...")
    val result = block
    println(s"Result = $result")
    }
    }
    Evaluator {
    val x = 5
    val y = 3
    x * y + 2
    }

}