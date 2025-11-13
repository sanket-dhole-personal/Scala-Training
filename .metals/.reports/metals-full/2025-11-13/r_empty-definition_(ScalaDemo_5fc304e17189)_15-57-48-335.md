file://<WORKSPACE>/Day6/Question5/LibraryItem.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 684
uri: file://<WORKSPACE>/Day6/Question5/LibraryItem.scala
text:
```scala
package library.operations
import library.items.ItemType
import library.users.Member
import library.items.Book
import library.items.Magazine
import library.items.DVD

object LibraryOperations {
    implicit val defaultMember: Member = new Member("Default User")

    def borrow(item:ItemType)(implicit member: Member): Unit ={
        member.borrowItem(item) 
    }

    implicit def stringToBook(title: String): Book = {
        Book(title)
    }

    def itemDescription(item: ItemType): Unit = {
        item match {
            case Book(title) => println(s"Book Title: $title")
            case Magazine(title) => println(s"Magazine Title: $title"
            case DVD(title) => @@s"DVD Title: $title"
        }
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 