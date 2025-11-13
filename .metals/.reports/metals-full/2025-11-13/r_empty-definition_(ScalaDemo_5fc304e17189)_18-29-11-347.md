file://<WORKSPACE>/Day6/Question5/LibraryMain.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -library/items/borrow.
	 -library/items/borrow#
	 -library/items/borrow().
	 -library/users/borrow.
	 -library/users/borrow#
	 -library/users/borrow().
	 -library/operations/borrow.
	 -library/operations/borrow#
	 -library/operations/borrow().
	 -borrow.
	 -borrow#
	 -borrow().
	 -scala/Predef.borrow.
	 -scala/Predef.borrow#
	 -scala/Predef.borrow().
offset: 223
uri: file://<WORKSPACE>/Day6/Question5/LibraryMain.scala
text:
```scala
package main

import library.items._
import library.users._
import library.operations._

object LibraryMain extends App:

  // Explicit member
  val alice = new Member("Alice")
  val book1 = Book("Scala Programming")
  borr@@ow(book1)(using alice) // or simply borrow(book1)(alice)

  // Using implicit default member
  val dvd1 = DVD("Inception")
  borrow(dvd1) // Default Member borrows 'Inception'

  // Using implicit conversion from String → Book
  borrow("Harry Potter")

  // Demonstrate sealed trait pattern matching
  val items: List[ItemType] = List(
    Book("FP in Scala"),
    Magazine("Science Today"),
    DVD("Matrix")
  )
  items.foreach(itemDescription)

```


#### Short summary: 

empty definition using pc, found symbol in pc: 