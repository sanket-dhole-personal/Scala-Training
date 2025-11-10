

object Combining2Lists extends App{
    def studentSubjectPairs: List[(String, String)] = {
      
      var students= List("Asha", "Bala", "Chitra")
      var subjects= List("Math", "Physics")
      val result= students.flatMap { student =>
          subjects.withFilter(subject => student.length >= subject.length)
          .map(subject => (student, subject))
    
      }
        result
    }
    println(studentSubjectPairs)

}