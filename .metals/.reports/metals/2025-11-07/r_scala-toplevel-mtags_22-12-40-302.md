error id: file://<WORKSPACE>/Day2.scala:[680..680) in Input.VirtualFile("file://<WORKSPACE>/Day2.scala", "
// Class and Object :-
// There are two types of classes available in scala normal and case class 
// Object is working like static content we directly call function and veriable by using class name

class Student{                        // Normal class

    var name:String ="Sanket";    
    var id: Int =1;
}
class Student1(var name:String,var age: Int){   //Normal parameterised class
    def info(): Unit={
        println(s"$name has age $age")
    }
}

class Student2(var id: Int=1,var name:String="Sanket"); // Normal parameterized class with default value


case class Student3(var id: Int,var name:String)       // case classs

object 

// Normal Class vs Case class:-
")
file://<WORKSPACE>/file:<WORKSPACE>/Day2.scala
file://<WORKSPACE>/Day2.scala:25: error: expected identifier; obtained eof

^
#### Short summary: 

expected identifier; obtained eof