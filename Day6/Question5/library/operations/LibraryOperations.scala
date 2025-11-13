package library.operations

import library.items._
import library.users._
import scala.language.implicitConversions

   
  // 1️⃣ Borrow method using an implicit Member
  def borrow(item: ItemType)(implicit member: Member): Unit =
    member.borrowItem(item)

  // 2️⃣ Pattern matching description
  def itemDescription(item: ItemType): Unit = item match
    case Book(title)     => println(s"Book: '$title'")
    case Magazine(title) => println(s"Magazine: '$title'")
    case DVD(title)      => println(s"DVD: '$title'")


  // 4️⃣ Default implicit Member
  implicit val defaultMember: Member = new Member("Default Member")
  implicit def stringToBook(title: String): Book = Book(title)
