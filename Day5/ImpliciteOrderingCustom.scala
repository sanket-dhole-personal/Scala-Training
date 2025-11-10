


case class Person(name: String, age: Int)

object Person {
  // Implicit class adds comparison operators to Person
  implicit class PersonOrdering(val p1: Person) extends AnyVal {
    def <(p2: Person): Boolean = p1.age < p2.age
    def >(p2: Person): Boolean = p1.age > p2.age
    def <=(p2: Person): Boolean = p1.age <= p2.age
    def >=(p2: Person): Boolean = p1.age >= p2.age
  }
}

object ImpliciteOrderingCustom extends App {
  val p1 = Person("Ravi", 25)
  val p2 = Person("Meena", 30)

  println(p1 < p2)   // true
  println(p1 > p2)   // false
  println(p1 <= p2)  // true
  println(p1 >= p2)  // false

  // Works inside conditions too
  if (p2 > p1) println(s"${p2.name} is older than ${p1.name}")
}
