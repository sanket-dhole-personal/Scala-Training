file://<WORKSPACE>/Day3.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -Curring.
	 -Curring#
	 -Curring().
	 -scala/Predef.Curring.
	 -scala/Predef.Curring#
	 -scala/Predef.Curring().
offset: 3373
uri: file://<WORKSPACE>/Day3.scala
text:
```scala

// Behavior of Lazy evaluation:-
  
// object lazyDemo extends App{
//     println("object starts")
//     val a={
//         println("Inside A")
//         "a veriable is created"
//     }
//     lazy val b={
//         println("Inside B")
//         "B is created"
//     }
//     println("Print statements start")
//     println(a);
//     println(b);
//     println("End")

// }

// // Tail Recursion:- recursive function is one where the recursive call is the last operation in the function.
// // This allows the compiler to optimize recursion and prevent stack overflow.

// object TailRecExample extends App {
//   def factorial(n: Int): Int = {
//     @annotation.tailrec
//     def loop(x: Int, acc: Int): Int = {
//       if (x <= 1) acc
//       else loop(x - 1, acc * x)
//     }
//     loop(n, 1)
//   }

//   println(factorial(5)) // 120
// }


// // Function Returning Function:

// object FunctionReturningFunction{
//   def multiplier(factor: Int): Int => Int = {
//     (x: Int) => x * factor
//   }

//   def main(args:Array[String])={  
//       val double = multiplier(2)
//        println(double(5)) // 10
//   }

// }


// // Function with keyword and veriable parameter

// def greet(name: String, msg: String = "Hi"): Unit =
//   println(s"$msg, $name!")

// def sumAll(nums: Int*): Int ={
//     var const:Int=0
//     for(num <- nums){
//             const=const+num

//     }
//     const
// }


// object t1 extends App{

//     greet(name = "Sanket", msg = "Hello")  //  Hello Sanket
//     greet(name = "John")                    //  Hi John

//     println(sumAll(1, 2, 3, 4,5)) // 15


// }

// // Higher Order Function(HOF):-
// // We pass function as a parameter

// object hof{

//    def demo( no : Int, firstparam :Int=>Int):Int={
//     var result :Int = firstparam(no);
//     result
//    }

//    def threeFunc(no1 : Int, str:String,func :(Int,String)=>String) : String={
//      val str1 : String=func(no1,str);
//      str1
//    }

//    def main(args:Array[String])={
//     println(demo(2,a=>a*4));
//    }

// }

// // Explore map, reduce, filter , foldleft, foldRight ,scanLeft,scanRight and collect methods


// object t3 extends App{
//   val nums = List(1, 2, 3, 4, 5)

//   // map – apply function to each element
//   println(nums.map(_ * 2)) // List(2,4,6,8,10)

//   // filter – keep elements matching condition
//   println(nums.filter(_ % 2 == 0)) // List(2,4)

//    // reduce – combine elements using function
//   println(nums.reduce(_ + _)) // 15
  
//    // foldLeft – combines from the left with initial value
//   println(nums.foldLeft(0)(_ + _)) // 15

//   // foldRight – combines from the right
//   println(nums.foldRight(0)(_ + _)) // 15

//   // scanLeft – like foldLeft but keeps intermediate results
//   println(nums.scanLeft(0)(_ + _)) // List(0,1,3,6,10,15)

//   // scanRight
//   println(nums.scanRight(0)(_ + _)) // List(15,14,12,9,5,0)

//   // collect – map + filter combined
//   println(nums.collect { case x if x % 2 == 0 => x * 10 }) // List(20,40)

// }


// // Partial Application:-
// // Creating a new function by fixing some parameters of another function.

// object t5{
//     def mul(a:Int,b:Int,c:Int):Int=a*b*c
//     def main(args:Array[String])={
//        var first=mul(2,_:Int,_:Int);
//        var second=first(3,_:Int)
//        println(second(4))
//     }
// }

Curring@@:-
  It is used for chaining Function with multiple parameter






```


#### Short summary: 

empty definition using pc, found symbol in pc: 