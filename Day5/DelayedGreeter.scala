
object DelayedGreeter extends App{

    def delayedMessage(delayMs: Int)(message: String): Unit = {
      Thread.sleep(delayMs)
      println(message)
    }

    var saveTime=delayedMessage(1000)
    println(saveTime("Hiii"))
    println(saveTime("Welcome to Scala"))
}