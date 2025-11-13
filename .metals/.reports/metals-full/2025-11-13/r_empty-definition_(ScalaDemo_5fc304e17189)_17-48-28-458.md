file://<WORKSPACE>/Day6/Question5/LibraryMain.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -library/items/dvd1.
	 -library/items/dvd1#
	 -library/items/dvd1().
	 -library/users/dvd1.
	 -library/users/dvd1#
	 -library/users/dvd1().
	 -library/operations/dvd1.
	 -library/operations/dvd1#
	 -library/operations/dvd1().
	 -dvd1.
	 -dvd1#
	 -dvd1().
	 -scala/Predef.dvd1.
	 -scala/Predef.dvd1#
	 -scala/Predef.dvd1().
offset: 397
uri: file://<WORKSPACE>/Day6/Question5/LibraryMain.scala
text:
```scala
import library.items._
import library.users._
import library.operations._

object LibraryMain extends App {

  // Explicit member
  val alice = new Member("Alice")
  val book1: Book = Book("Scala Programming")
  LibraryOperations.borrow(book1)(alice)          // Alice borrows 'Scala Programming'

  // Using implicit default member
  val dvd1: DVD = DVD("Inception")
  LibraryOperations.borrow(dv@@d1)                  // Default Member borrows 'Inception'

  // Using implicit conversion from String to Book
  LibraryOperations.borrow("Harry Potter")        // Default Member borrows 'Harry Potter'

  // Demonstrate sealed trait pattern matching
  val items: List[ItemType] = List(Book("FP in Scala"), Magazine("Science Today"), DVD("Matrix"))
  items.foreach(itemDescription)
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 