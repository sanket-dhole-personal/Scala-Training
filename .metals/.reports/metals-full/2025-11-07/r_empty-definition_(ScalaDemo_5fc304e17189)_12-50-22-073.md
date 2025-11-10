error id: file://<WORKSPACE>/Hof.scala:scala/Array#
file://<WORKSPACE>/Hof.scala
empty definition using pc, found symbol in pc: scala/Array#
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -Array#
	 -scala/Predef.Array#
offset: 109
uri: file://<WORKSPACE>/Hof.scala
text:
```scala

object hof{

   def demo(firstparam :Int=>Int):Int={
    var no:Int= firstparam;
   }

   def main(args:Arra@@y[String])={
    println(demo(2 => 2*2));
   }

}
```


#### Short summary: 

empty definition using pc, found symbol in pc: scala/Array#