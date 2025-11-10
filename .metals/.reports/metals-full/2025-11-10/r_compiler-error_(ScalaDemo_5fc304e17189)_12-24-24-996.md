error id: FB0D317163D139F4E6305E0425B8783E
file://<WORKSPACE>/Day5/ArrayMirror.scala
### dotty.tools.dotc.ast.Trees$UnAssignedTypeException: type of Ident(j) is not assigned

occurred in the presentation compiler.



action parameters:
offset: 143
uri: file://<WORKSPACE>/Day5/ArrayMirror.scala
text:
```scala

object ArrayMirror{

    def reverseArr(arr:Array[Int]):Array[Int]={
        var result:Array[Int]=new Array[Int](arr.length*2)
        var j[@@]
        for(i=arr.length-1 to i>0 by i--){
            arr(i)
        }
    }

    def main(args:Array[String])={
        var arr=Array(1,2,3,4,5)
        var mirroredArr=arr ++ arr.reverse
        println(mirroredArr.mkString(","))
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
dotty.tools.dotc.ast.Trees$Tree.tpe(Trees.scala:77)
	dotty.tools.dotc.util.Signatures$.applyCallInfo(Signatures.scala:208)
	dotty.tools.dotc.util.Signatures$.computeSignatureHelp(Signatures.scala:104)
	dotty.tools.dotc.util.Signatures$.signatureHelp(Signatures.scala:88)
	dotty.tools.pc.SignatureHelpProvider$.signatureHelp(SignatureHelpProvider.scala:46)
	dotty.tools.pc.ScalaPresentationCompiler.signatureHelp$$anonfun$1(ScalaPresentationCompiler.scala:498)
```
#### Short summary: 

dotty.tools.dotc.ast.Trees$UnAssignedTypeException: type of Ident(j) is not assigned