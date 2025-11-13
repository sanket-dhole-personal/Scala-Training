
abstract class Spacecraft(val name: String) {
    val fuelLevel: Double;
    def launch(): Unit

    def land(): Unit={
        println(s"$name is landing")
    }
}

class CargoShip extends Spacecraft("CargoShip") {
    override val fuelLevel: Double = 100.0

    override def launch(): Unit = {
        // super.launch()
        println(s"$name with fuel level $fuelLevel is launching for cargo delivery.")
    }

    override def land(): Unit = {
        super.land()
        println(s"$name has landed after delivering cargo.")
    }
}

class PassengerShip extends Spacecraft("PassengerShip") {
    override val fuelLevel: Double = 200.0

    override def launch(): Unit = {
        // super.launch()
        println(s"$name with fuel level $fuelLevel is launching for passenger transport.")
    }

    final override def land(): Unit = {
        super.land()
        println(s"$name has landed after transporting passengers.")
    }
}

final class LuxuryCruiser extends PassengerShip with Autopilot{
    override def launch(): Unit = {
        super.launch()
        println(s"$name is providing luxury services during the journey.")
    }

    def playEntertainment(): Unit = {
        println(s"$name is playing entertainment for passengers.")
    }

}

trait Autopilot {
    def autoNavigate(): Unit = {
        println("Autopilot engaged.")
    }
}

object Main1 extends App {
    val cargoShip = new CargoShip()
    cargoShip.launch()
    cargoShip.land()

    val passengerShip = new PassengerShip()
    passengerShip.launch()
    passengerShip.land()

    val luxuryCruiser = new LuxuryCruiser()
    luxuryCruiser.launch()
    luxuryCruiser.autoNavigate()
    luxuryCruiser.playEntertainment()
    luxuryCruiser.land()
}