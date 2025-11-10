
object OperatorOverload extends App{
    class Counter(val value: Int) {

           // Define + for adding two Counter objects
           def +(that: Counter): Int = this.value + that.value

           // Overload + for adding an Int to a Counter
           def +(that: Int): Int = this.value + that
    }

     val a = new Counter(5)
     val b = new Counter(7)

     println(a + b)   // 12
     println(a + 10)  // 15



}