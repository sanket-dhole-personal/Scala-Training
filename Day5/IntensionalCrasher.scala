object IntensionalCrasher extends App{
    def safeDevide:PartialFunction[Int,String]={
        case x if x!=0 => s"100 divided by $x is ${100/x}"
        // case x if x==0 => "Not Defined"
    }
    var safe=safeDevide.lift
    println(safe(10))
    println(safe(0))
}