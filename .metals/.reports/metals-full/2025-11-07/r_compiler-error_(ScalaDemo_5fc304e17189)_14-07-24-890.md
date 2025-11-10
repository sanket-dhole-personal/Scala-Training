error id: 046B01E7E3DF6E9E826DC8A89D81964C
file://<WORKSPACE>/Day1.scala
### java.lang.IndexOutOfBoundsException: 0

occurred in the presentation compiler.



action parameters:
offset: 942
uri: file://<WORKSPACE>/Day1.scala
text:
```scala

// Veriables:-
//   There are 2 types of veriables 
//     1) var:- it is mutable 
//     2) val:- it is immutable

object Veriables{

    def main(args :Array[String])={
       val name : String="Sanket";
       var age : Int=25;
       println(name)  // Sanket
       println(age)  //25
    //    name="John";  // not possible becuase val is immutable
    //    println(name)
       age=30;
       println(age); // It works becuse of var is mutable 
       
    }
}

// Datatypes :- 

object Datatypes{

    def main(args: Array[String])={
        println("Premitive Datatypes")
        val byte :Byte =1;
        val short :Short = 2;
        val int: Int = 3;
        val long: Long=4;
        val  ch: Char='c';
        val bool: Boolean=true;
        val float: Float=1.1;
        val double: Double=2.2;

        val bigInt: BigInt=100;
        val bigDecimal:BigDecimal=100;

        println("Non-Premitive Datatypes")

        val s@@ String

        
    }
}
   
```


presentation compiler configuration:
Scala version: 3.7.3-bin-nonbootstrapped
Classpath:
<WORKSPACE>/.scala-build/ScalaDemo_c304e17189/classes/main [exists ], /opt/homebrew/Cellar/scala/3.7.3/libexec/maven2/org/scala-lang/scala3-library_3/3.7.3/scala3-library_3-3.7.3.jar [exists ], /opt/homebrew/Cellar/scala/3.7.3/libexec/maven2/org/scala-lang/scala-library/2.13.16/scala-library-2.13.16.jar [exists ], <HOME>/Library/Caches/Coursier/v1/https/repo1.maven.org/maven2/com/sourcegraph/semanticdb-javac/0.10.0/semanticdb-javac-0.10.0.jar [exists ], <WORKSPACE>/.scala-build/ScalaDemo_c304e17189/classes/main/META-INF/best-effort [missing ]
Options:
-Xsemanticdb -sourceroot <WORKSPACE> -Ywith-best-effort-tasty




#### Error stacktrace:

```
scala.collection.LinearSeqOps.apply(LinearSeq.scala:131)
	scala.collection.LinearSeqOps.apply$(LinearSeq.scala:128)
	scala.collection.immutable.List.apply(List.scala:79)
	dotty.tools.pc.InferCompletionType$.inferType(InferExpectedType.scala:94)
	dotty.tools.pc.InferCompletionType$.inferType(InferExpectedType.scala:62)
	dotty.tools.pc.completions.Completions.advancedCompletions(Completions.scala:523)
	dotty.tools.pc.completions.Completions.completions(Completions.scala:122)
	dotty.tools.pc.completions.CompletionProvider.completions(CompletionProvider.scala:139)
	dotty.tools.pc.ScalaPresentationCompiler.complete$$anonfun$1(ScalaPresentationCompiler.scala:197)
```
#### Short summary: 

java.lang.IndexOutOfBoundsException: 0