
def firstFun(parts:List[String]):String={
    for(part <- parts){
        println(s"Connected parts are $part")
    }
    "Assemled parts"
}

def secondFun(work:String): Unit={
    println(s"Penting $work with specific colors")
}

@main
def main1() :Unit={
    var list:List[String]=List("Wheel","Stering","Body");
    var assembly=firstFun.andThen(secondFun)
    assembly(list)
}