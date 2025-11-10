
object PersonilizedCurry extends App{
    def calculate(method:String)(a:Int,b:Int):Int={
        method match {
            case "add" => a + b
            case "sub" => a - b
            case "mul" => a * b
            case "div" => a / b
            case _ => 0
        }
    }
    val addFunction=calculate("add")
    println(addFunction(10,5))  // 15
    val subFunction=calculate("sub")
    println(subFunction(10,5))  // 5                        
    val mulFunction=calculate("mul")
    println(mulFunction(10,5))  // 50
    val divFunction=calculate("div")
    println(divFunction(10,5))  // 2
}