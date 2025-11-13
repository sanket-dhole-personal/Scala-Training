file://<WORKSPACE>/Day6/Question5/LibraryItem.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -item.
	 -item#
	 -item().
	 -scala/Predef.item.
	 -scala/Predef.item#
	 -scala/Predef.item().
offset: 234
uri: file://<WORKSPACE>/Day6/Question5/LibraryItem.scala
text:
```scala
package library.operations
import library.items.ItemType
import library.users.{Member}


    implicit val member = new Member("Default User")

    def borrow(item:ItemType)(implicite member: Member): Unit ={
        member.borrowItem(@@item)
    }

```


#### Short summary: 

empty definition using pc, found symbol in pc: 