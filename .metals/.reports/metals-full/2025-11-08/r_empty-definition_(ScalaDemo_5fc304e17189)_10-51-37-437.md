error id: file://<WORKSPACE>/Day4.scala:scala/collection/mutable/ListBuffer#toList().
file://<WORKSPACE>/Day4.scala
empty definition using pc, found symbol in pc: scala/collection/mutable/ListBuffer#toList().
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -list1/toList.
	 -list1/toList#
	 -list1/toList().
	 -scala/Predef.list1.toList.
	 -scala/Predef.list1.toList#
	 -scala/Predef.list1.toList().
offset: 342
uri: file://<WORKSPACE>/Day4.scala
text:
```scala
import scala.collection.mutable.ListBuffer
def myFilter1(list: List[Int],predicate:Int=>Boolean):List[Int]={
    if(list.isEmpty) None
    var list1=ListBuffer(0)
    // for(element <- list if predicate(element)) yield element 
    for(element <- list){
        if (predicate(element)){
           list1 += element
        } 
    }
    list1.@@toList
    
}

object myFilter{
    def main(args:Array[String])={
        var list:List[Int]=List(1,2,3,4,5,6,7,8,9)
        println(myFilter1(list,x=> x%2==0))
        println(myFilter1(list,_%2!=0))
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/collection/mutable/ListBuffer#toList().