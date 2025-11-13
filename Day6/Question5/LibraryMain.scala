package main

import library.items._
import library.users._
import library.operations._

object LibraryMain extends App:

  // Explicit member
  val alice = new Member("Alice")
  val book1 = Book("Scala Programming")
  borrow(book1)(using alice) // or simply borrow(book1)(alice)

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
