
// Blocked companion:-

import scala.collection.mutable.ListBuffer
def myFilter1(list: List[Int],predicate:Int=>Boolean):List[Int]={
    if(list.isEmpty) None
    // var list1=ListBuffer[Int]()
    for(element <- list if predicate(element)) yield element 
    // for(element <- list){
    //     if (predicate(element)){
    //        list1 += element
    //     } 
    // }
    // list1.toList
    
}

object myFilter{
    def main(args:Array[String])={
        var list:List[Int]=List(1,2,3,4,5,6,7,8,9)
        println(myFilter1(list,x=> x%2==0))
        println(myFilter1(list,_%2!=0))
    }
}



// // Implicit:- 
// object implicitDemo extends App{
//     // for Method 
//     implicit def intToString(a:Int):String={
//       a.toString()
//     }

//     var str:String=60
//     println(str)

//     //. for class
//     implicit class Cal(x:Int) extends AnyVal{
//        def square:Int=x*x
//        def triple:Int=x*x*x
//     }

//     val no:Int=5
//     println(s"Square : ${no.square}")
//     println(s"Square 5 : ${5.square}")
//     println(s"Triple : ${no.triple}")

//     // for veriable
//     def greet(str:String)(implicit imp:String)=println(s"$imp $str")

//     implicit var i:String="Hello"
//     var ab=greet("Scala")
    


// }
