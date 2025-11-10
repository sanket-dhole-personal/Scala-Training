def func:PartialFunction[Int,String]={
    case x if x%2==0 => s"$x is Even"
}

def func1:PartialFunction[String,String]={
    case x => s"Hello $x"
}



@main 
def test3(): Unit={
    println(func(2))
    println(func.isDefinedAt(4))
    println(func1("Hello"))

    var list:List[Int]=List(1,2,3,4,5,6,7,8,9)
    println(list.collect(func))
}