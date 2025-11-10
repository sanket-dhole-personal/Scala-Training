error id: file://<WORKSPACE>/Day5/UnApplyChainNested.scala:
file://<WORKSPACE>/Day5/UnApplyChainNested.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -that.
	 -that#
	 -that().
	 -scala/Predef.that.
	 -scala/Predef.that#
	 -scala/Predef.that().
offset: 167
uri: file://<WORKSPACE>/Day5/UnApplyChainNested.scala
text:
```scala

object UnApplyChainNested extends App{
    case class Address(city: String, pincode: Int)
case class Person(name: String, address: Address)

Write a pattern match tha@@t extracts the city and pincode directly from a Person object in a
single case statement.
val p = Person("Ravi", Address("Chennai", 600001))
p match {
case Person(_, Address(city, pin)) =>
println(s"$city - $pin")
case _ => println("No match")
}
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 