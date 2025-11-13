
trait Device{
    def turnOn(): Unit
    def turnOff(): Unit
    def status(): Unit={
        println("Device is operational.")
    }
}

trait Connectivity{
    def connect(network: String): Unit={
        println(s"Connecting to $network network.")
    }
    def disconnect(): Unit={
        println("Disconnecting from network.")
    }
}

trait EnergySaver{
    def activateEnergySaver(): Unit={
        println("Energy saver mode activated.")
    }
    def turnOff(): Unit={
        println("Device powered down to save energy")
    }
}

class SmartLight extends Device with Connectivity with EnergySaver with VoiceControl{
    override def turnOn(): Unit = {
        println("SmartLight is turned on.")
    }
    override def turnOff(): Unit = {
        super.turnOff()
        println("SmartLight is turned off.")
    }
}

class SmartThermostat extends Device with Connectivity{
    def turnOn(): Unit = {
        println("SmartThermostat is turned on.")
    }
    def turnOff(): Unit = {
        println("SmartThermostat is turned off.")
    }
}

trait VoiceControl{
    def voiceCommand(command: String): Unit = {
        println(s"Executing voice command: $command")
    }
    def turnOn(): Unit ={
        println("Voice control activated.")
    }
    def turnOff(): Unit ={
        println("Voice control deactivated.")
    }
}

object Main extends App {
    val smartLight = new SmartLight()
    smartLight.turnOn()
    smartLight.connect("Home WiFi")
    smartLight.voiceCommand("Dim the lights")
    smartLight.activateEnergySaver()
    smartLight.turnOff()
    smartLight.disconnect()

    println()

    val smartThermostat = new SmartThermostat()
    smartThermostat.turnOn()
    smartThermostat.connect("Home WiFi")
    smartThermostat.status()
    smartThermostat.turnOff()
    smartThermostat.disconnect()
}