
// // Veriables:-
// //   There are 2 types of veriables 
// //     1) var:- it is mutable 
// //     2) val:- it is immutable

// object Veriables{

//     def main(args :Array[String])={
//        val name : String="Sanket";
//        var age : Int=25;
//        println(name)  // Sanket
//        println(age)  //25
//     //    name="John";  // not possible becuase val is immutable
//     //    println(name)
//        age=30;
//        println(age); // It works becuse of var is mutable 
       
//     }
// }

// // Datatypes :- 

// object Datatypes{

//     def main(args: Array[String])={
//         println("Premitive Datatypes")
//         val byte :Byte =1;
//         val short :Short = 2;
//         val int: Int = 3;
//         val long: Long=4;
//         val  ch: Char='c';
//         val bool: Boolean=true;
//         val float: Float=1.1;
//         val double: Double=2.2;

//         val bigInt: BigInt=100;
//         val bigDecimal:BigDecimal=100;

//         println("Non-Premitive Datatypes")

//         val str: String="Hii"
//         val anyVal: AnyVal=10;             // Any val is used for primitives
//         val anyRef: AnyRef="Hello"         // any ref is used for non primitives

        
//     }
// }

// // Desesion Making Statements:- It is used for control flow
   

// object ConditionalStatements{
//  def main(args:Array[String])={
//   var age : Int=21;
//   if(age>20){
//     println("age is greter than 20");
//   }else{
//     println("age is less than 20")
//   }

//   var department : String ="Civil";

//   var check=department match{
//     case "IT" => println("IT");
//     case "Civil" => println("Civil")
//   }
//  }
// }

// // Array :-
// object demo{
//     def main(args:Array[String])={
//         var days = Array("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday" )
//         var fruits=new Array[String](5)
//         fruits(0)="Apple"
//         fruits(1)="Mango"
//         fruits(2)="Banana"
//         fruits(3)="Cherry"
//         println(fruits)
//         for(fruit <-fruits){
//             println(fruit)
//         }

//     }    
    
// }


// // Functions:-

// object Function{
//     def getName(name: String) : String={
//         return name;
//     }

//     def defaultParam(name : String="John" ) ={
//       println(s"Hii $name");
//     }
//     def default()={
//         println("Without param method")
//     }

//     def defaultParam(name : String , id : Int)={
//         println(s"Hii $name your id is $id ");
//     }

//     def main(args : Array[String]): Unit={
//         var n: String =getName("Sanket");
//         println(n)
//         defaultParam();
//         defaultParam("Rock")
//         defaultParam("Sanket",20);
//     }
// }


// Collection:- 

object Collections{

def main(args: Array[String])={
  val fruits: List[String] = List("Apple", "Banana", "Cherry")
  val cars: Set[String]= Set("Tata","Suzuki","Mahindra")

  println(fruits(0))
  println(fruits)    //List(Apple, Banana, Cherry)
  println(cars)     // Set(Tata, Suzuki, Mahindra)
  println(cars.contains("Suzuki"))  // true

  var map: Map[Int, String]=Map(1->"Red",2->"Blue")

  var tuple:(String,Int,Boolean)=("John",20,true)

  println(map)             //Map(1 -> Red, 2 -> Blue)
  println(tuple)           //(John,20,true)


}

}


   