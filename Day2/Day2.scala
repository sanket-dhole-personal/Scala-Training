
// // Class and Object :-
// // There are two types of classes available in scala normal and case class 
// // Object is working like static content we directly call function and veriable by using class name

// class Student{                        // Normal class

//     var name:String ="Sanket";    
//     var id: Int =1;
// }
// class Student1(var name:String,var age: Int){   //Normal parameterised class
//     def info(): Unit={
//         println(s"$name has age $age")
//     }
// }

// class Student2(var id: Int=1,var name:String="Sanket"); // Normal parameterized class with default value


// case class Student3(var id: Int,var name:String)       // case classs

// object Test2{
//     def main(args: Array[String]): Unit={
//        var std=new Student();
//        var std1=Student();
//        var std2=new Student1("John",45);
//        var std3= Student1("John",45);
//        println(std2==std3)

//        var obj:Student=new Student()
//        var obj1:Student=Student();
//     }
// }

// Normal Class vs Case class:-
// 1 Normal class is mutable but case class is immutable
// 2 Normal class "==" check ref but case class check content
// 3 Normal class need to apply unapply method manually but inside case class they create by default
// 4 Normal class doesnot have copy method but inside case class copy method is present
// 5 Normal class is not serializable by defalut but case class is serialized by default
// 6 Normal class is used for General classes but case class is used for Modeling immutable data  

// class Sample(val name: String,val age:Int){

// println(s"$name age is $age");

// }


// class Simple(val id : Int =1,val name :String="Sanket");

// case class SimpleCase(val id: Int =2, val name : String="John");

// object Test{
//     def main(args: Array[String]):Unit={
//       var sample=new Sample("Sanket",30);
//       var sample1=Sample("John",45);
      


//       var obj1: Simple=Simple();
//       var obj2: Simple=Simple();

//       println(obj1);
  
//       var obj3 : SimpleCase= SimpleCase();
//       var obj4 : SimpleCase= SimpleCase();

//       println(obj3);

//       println(obj1 == obj2);
//       println(obj3 == obj4)

//     }
// }

// // Apply UnApply methods :-
// // Apply and UnApply method is written inside Componion Object
// // Apply mrthod is used for structuring object
// // Unapply method is used for Destructuring Object
// // if we create apply unApply method the we doesnot need to write new keyword at the time of object creation

// class Sample4(val empId: Int,val empName : String);

// class Sample3(val empId: Int,val empName : String);
// object Sample3{
//     def apply(empId : Int, empName : String) : Sample3= new Sample3(empId,empName);
//     def unapply(smp : Sample3): Option[(Int,String)]= Some(smp.empId,smp.empName);
// } 



// case class Sample1(val empId: Int,val empName : String);


// object Test1 extends App{

//   var obj=Sample4(1,"John");
//   var obj1=Sample1(1,"John");

//  // We get error becuse unapply method not created
//   var obj3=Sample3(3,"Sam");
//   obj3 match                              
//     case Sample3(id,name) => println(s"Simple class works with $id and $name");
//     case _ => println("Default works");
  
//   obj1 match
//     case Sample1(id,name) => println(s"Simple class works with $id and $name");
//     case _ => println("Default works");
  

// }


// // Componion Object:- 
//     // Composition object is use to store ststic element of class 
//     // composition object name is same as class name and we write apply and unapply method inside composition object 

// object ComponionDemo{
//     var name:String="Sanket"
//     def demo():Unit={
//         println("Demo method inside Componion Object")
//     }
// }
// @main
// def T1():Unit={
//     println(ComponionDemo.name)
//     ComponionDemo.demo()
// }


// // Auxaliry Constructor
//     // We create auxaliry constructor by using this keyword
//     // we write hard coded default value 
//     // It uses main constoctor for reference

// class Person(id:Int,name:String,discription:String){
//     def this(id:Int,name:String)={
//         this(id,name,"Default discription")
//     }
    
// }

// object p1{
//   def main(args:Array[String]):Unit={
//     var p1:Person=new Person(10,"John","Fighter")
//     var p2:Person=new Person(11,"Jack")
//     println(p1)
//     println(p2)
//   }  
// }

// // Methos with default parameter:-
// //   We apply default parameter inside paranthisis so if we not pass parameter that time it gives default parameter value

// def m1(firstName:String="John",lastName:String="Cena"):Unit={
//   println(s"First Name : $firstName, Last Name : $lastName")
// }
// object T2{
//   def main(args:Array[String])={
//     var p1=m1("Sanket","Dhole")
//     var p2=m1("Jack")
//     println(p1)
//     println(p2)
//   }

// }


// // Method Overloading:-
// //   We can create method with same name with diff parameter is known as method Overloading

// class Calculator{
//     def add(a:Int,b:Int):Int={
//         a + b
//     }
//     def add(a:Double,b:Double):Double={
//         a + b
//     }
//     def add(a:Int,b:Int,c:Int):Int={
//         a+b+c
//     }
// }

// object OverloadingExample extends App {
//   val calc = new Calculator()
//   println(calc.add(5, 10))       // Calls add(Int, Int)
//   println(calc.add(3.5, 2.1))    // Calls add(Double, Double)
//   println(calc.add(1, 2, 3))     // Calls add(Int, Int, Int)
// }


// Operator Overloading:-








