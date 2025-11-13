trait Animal{
    def color():Unit={
        println("Animals have colors")
    }
}

trait Species{
    def typeOfSpecies(typeOfSpecies: String):Unit={
        println(s"$typeOfSpecies species of animals")
    }
}

trait Dog extends Species with Animal{
    override def color(): Unit = {
        super.color()
        println("Dogs can be of various colors")
    }

    override def typeOfSpecies(typeOfSpecies: String): Unit = {
        super.typeOfSpecies("German Shepherd")
        println("Dogs belong to the Canine species")
    }
}

trait Age extends Animal{
    def ageInfo(age: Int):Unit={
        super.color()
        println(s"Animal age up to $age years")
    }
}

class Info extends Dog with Age
object TraitDemo1 extends App{
    val dogInfo = new Info()
    // dogInfo.color()
    // dogInfo.typeOfSpecies("Mammal")
    // dogInfo.ageInfo(15)
    dogInfo.color()
}