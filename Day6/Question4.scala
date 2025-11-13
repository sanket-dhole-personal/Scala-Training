trait Drone{
    def activate():Unit 
    def deActivate():Unit
    def status(): Unit={
        println("Drone is operational.")
    }
}

trait NavigationModule extends Drone{
    def flyTo(destination: String): Unit={
        println(s"Fly to $destination")
    }
    override def deActivate(): Unit={
        println("Navigation module deactivated.")
    }
}

trait DefenseModule extends Drone{
    def activateShield(): Unit={
        println("Shield activated.")
    }
    override def deActivate(): Unit={
        println("Defense module deactivated.")
    }
}

trait CommunicationModule extends Drone{
    def sendMessage(message: String): Unit={
        println(s"Sending message: $message")
    }
    override def deActivate(): Unit={
        println("Communication module deactivated.")
    }
}

class BasicDrone extends Drone{
    override def activate(): Unit = {
        println("BasicDrone is activated.")
    }

    override def deActivate(): Unit = {
        println("BasicDrone is deactivated.")
    }
}

object Main4 extends App {
    val myDrone=new BasicDrone with NavigationModule with DefenseModule;
    myDrone.activate()
    myDrone.status()
    myDrone.deActivate()
    myDrone.flyTo("Point A")
    myDrone.activateShield()

    val myDrone1=new BasicDrone with DefenseModule with NavigationModule with CommunicationModule;
    myDrone1.deActivate();
    
}