trait Robot{
    def start(): Unit

    def shutdown(): Unit
    def status(): Unit={
        println("Robot is operational.")
    }
}

trait SpeechModule{
    def speak(message: String): Unit={
        println(s"Robot says: $message")
    }
}

trait MovementModule{
    def moveForward(): Unit={
        println(s"Moving Forward")
    }
    def moveBackward(): Unit={
        println(s"Moving Backward")
    }
}

trait EnergySaver1 extends Robot{
    def activateEnergySaver():Unit={
        println("Energy saver mode activated.")
    }
    override def shutdown(): Unit={
        println("Robot is shutting down to save energy.")
    }
}

class BasicRobot extends Robot{
    override def start(): Unit = {
        println("BasicRobot is starting.")
    }

    override def shutdown(): Unit = {
        println("BasicRobot is shutting down.")
    }
}

object Main3 extends App {
    val myRobot=new BasicRobot with SpeechModule with MovementModule();
    myRobot.start()
    myRobot.status()
    myRobot.speak("Hello, I am a robot.")
    myRobot.moveForward()
    myRobot.shutdown()
    

    val myRobot1=new BasicRobot with EnergySaver1 with MovementModule();
    myRobot1.start()             // BasicRobot starting up
    myRobot1.moveBackward()      // Moving backward
    myRobot1.activateEnergySaver() // Energy saver mode activated
    myRobot1.shutdown()


}