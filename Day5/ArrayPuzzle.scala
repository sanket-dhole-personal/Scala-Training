object ArrayPuzzle extends App{

    
    val animals = Map(
    "dog" -> "bark",
    "cat" -> "meow",
    "cow" -> "moo"
    )

  // 1️⃣ Add new entry
  val updatedAnimals = animals + ("lion" -> "roar")

  // 2️⃣ Retrieve sound of cow
  val cowSound = animals("cow")

  // 3️⃣ Safe access for tiger
  val tigerSound = animals.getOrElse("tiger", "unknown")

  println(updatedAnimals)
  println(s"Cow sound: $cowSound")
  println(s"Tiger sound: $tigerSound")
}

