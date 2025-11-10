

object TransformingNastedData extends App{

    def flattenDepartments(departments:List[(String, List[String])]): List[(String, String)] = {
         for {
            (dept, employees) <- departments    // pattern decomposition of each tuple
              emp <- employees                    // implicit flattening via flatMap
        } yield (dept, emp)
    }

    val departments = List(
            ("IT", List("Ravi", "Meena")),
             ("HR", List("Anita")),
            ("Finance", List("Vijay", "Kiran"))
        )

    println(flattenDepartments(departments))


}