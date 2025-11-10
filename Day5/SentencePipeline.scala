
object SentencePipeline extends App{
  

   val trimSpaces: String => String = _.trim
   val toLower: String => String = _.toLowerCase
   val capitalizeFirst: String => String = s => s.head.toUpper + s.tail
  
   var processSentence:String =>String= trimSpaces andThen toLower andThen capitalizeFirst
   

   val messy = " HeLLo WOrld "
   println(processSentence(messy)) // "Hello world"

}
