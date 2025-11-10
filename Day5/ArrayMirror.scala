
object ArrayMirror{

    def reverseArr(arr:Array[Int]):Array[Int]={
        var result:Array[Int]=new Array[Int](arr.length*2)
        var j :Int = 0
        for(ar <- arr){
            result(j)=ar
            j+=1
        }
        for(i<-arr.length-1 to 0 by -1){
            result(j)=arr(i)
            j+=1
        }
        result
    }

    def main(args:Array[String])={
        var arr=Array(1,2,3)
        println(reverseArr(arr).mkString(","))
    }
}