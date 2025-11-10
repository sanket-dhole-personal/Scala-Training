
object Implicite extends App{
    implicit def intToDouble(x:Double):Int={
       x.toInt
    }
    var no:Int=4.3
    println(no)


    implicit class Cal(x:Int) extends AnyVal{
        def mul:Int=x * x
        def toString1:String=x.toString()
    }
    println(5.mul)
    println(5.toString1)


    def numb(s:String)(implicit n:Int):String={
        s"$s $n"
    }
    implicit var n:Int=10
    println(numb("Sanket"))
    
}