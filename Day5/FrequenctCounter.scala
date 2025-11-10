

object FrequenctCounter extends App{
    val lines = List("Scala is powerful","Scala is concise","Functional programming is powerful")

    def wordFrequency(lines: List[String]): Map[String, Int] = {
        lines
          .flatMap(line => line.split("\\s+")) // Split each line into words
        //   .map(word => word.toLowerCase.replaceAll("[^a-z]", "")) // Normalize words
          .filter(_.nonEmpty) // Remove empty strings
          .groupBy(identity) // Group by word
        //   .mapValues(_.size) // Count occurrences
        //   .toMap
          .map { case (word, occurrences) => (word, occurrences.size) } 
    }
    println(wordFrequency(lines))
}