
object FindMaxElement extends App{

    def findMax(arr:Array[Int]):Int={

        @annotation.tailrec
        def maxHelper(index:Int, currentMax:Int):Int={
            if(index>=arr.length) currentMax
            else{
                val newMax = if(arr(index)>currentMax) arr(index) else currentMax
                maxHelper(index+1, newMax)
            }
        }

        if(arr.isEmpty) throw new NoSuchElementException("Array is empty")
        maxHelper(1, arr(0))
    }

    val arr = Array(3, 5, 2, 8, 1)
    println(s"The maximum element in the array is: ${findMax(arr)}")
}