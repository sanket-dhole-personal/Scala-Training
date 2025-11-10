

object MystryOf extends App{

    def mystry(pre:Int,post:Int,list:List[Int]):List[Int]={

    pre +: list :+ post
        
    }
    var list:List[Int]= List(1,2,3,4)
    println(mystry(0,5,list))
}