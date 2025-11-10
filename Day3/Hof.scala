
object hof{

   def demo( no : Int, firstparam :Int=>Int):Int={
    var result :Int = firstparam(no);
    result
   }

   def threeFunc(no1 : Int, str:String,func :(Int,String)=>String) : String={
     val str1 : String=func(no1,str);
     str1
   }

   def main(args:Array[String])={
    println(demo(2,a=>a*4));
   }

}