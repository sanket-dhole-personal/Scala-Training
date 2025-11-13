import java.sql.*
import java.time.*
import scala.util.{Using, Try, Success, Failure}

// -------------------- Database Connection --------------------
object Database:
  private val url = "jdbc:mysql://************:3306/sanket"
  private val username = "mysqladmin"
  private val password = "**********"

  Class.forName("com.mysql.cj.jdbc.Driver")

  def getConnection(): Connection =
    DriverManager.getConnection(url, username, password)
end Database

// -------------------- Database Setup --------------------
object DatabaseSetup:
  def createTables(): Unit =
    Using.resource(Database.getConnection()) { conn =>
      val stmt = conn.createStatement()

      val createVehicles =
        """CREATE TABLE IF NOT EXISTS Vehicles (
             vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
             license_plate VARCHAR(20),
             vehicle_type VARCHAR(20),
             owner_name VARCHAR(100)
           );"""
      stmt.execute(createVehicles)

      val createSignals =
        """CREATE TABLE IF NOT EXISTS TrafficSignals (
             signal_id INT PRIMARY KEY AUTO_INCREMENT,
             location VARCHAR(100),
             status VARCHAR(10)
           );"""
      stmt.execute(createSignals)

      val createViolations =
        """CREATE TABLE IF NOT EXISTS Violations (
             violation_id INT PRIMARY KEY AUTO_INCREMENT,
             vehicle_id INT,
             signal_id INT,
             violation_type VARCHAR(50),
             timestamp DATETIME,
             FOREIGN KEY (vehicle_id) REFERENCES Vehicles(vehicle_id),
             FOREIGN KEY (signal_id) REFERENCES TrafficSignals(signal_id)
           );"""
      stmt.execute(createViolations)

      println("✅ Tables created successfully.")
    }
end DatabaseSetup

// -------------------- DAO Layer --------------------
object TrafficDAO:

  def addVehicle(licensePlate: String, vehicleType: String, ownerName: String): Unit =
    val sql = "INSERT INTO Vehicles (license_plate, vehicle_type, owner_name) VALUES (?, ?, ?)"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps = use(conn.prepareStatement(sql))
      ps.setString(1, licensePlate)
      ps.setString(2, vehicleType)
      ps.setString(3, ownerName)
      val rows = ps.executeUpdate()
      println(s"✅ Vehicle added ($rows row).")
    } match
      case Failure(e) => println(s"❌ Error inserting vehicle: ${e.getMessage}")
      case Success(_) => ()

  def addTrafficSignal(location: String, status: String): Unit =
    val sql = "INSERT INTO TrafficSignals (location, status) VALUES (?, ?)"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps = use(conn.prepareStatement(sql))
      ps.setString(1, location)
      ps.setString(2, status)
      val rows = ps.executeUpdate()
      println(s"✅ Traffic signal added ($rows row).")
    } match
      case Failure(e) => println(s"❌ Error inserting signal: ${e.getMessage}")
      case Success(_) => ()

  def viewVehicles(): Unit =
    val sql = "SELECT * FROM Vehicles"
    Using.resource(Database.getConnection()) { conn =>
      val rs = conn.createStatement().executeQuery(sql)
      println("\n🚗 Vehicles:")
      while rs.next() do
        println(s"ID: ${rs.getInt("vehicle_id")}, Plate: ${rs.getString("license_plate")}, Type: ${rs.getString("vehicle_type")}, Owner: ${rs.getString("owner_name")}")
    }

  def viewSignals(): Unit =
    val sql = "SELECT * FROM TrafficSignals"
    Using.resource(Database.getConnection()) { conn =>
      val rs = conn.createStatement().executeQuery(sql)
      println("\n🚦 Traffic Signals:")
      while rs.next() do
        println(s"ID: ${rs.getInt("signal_id")}, Location: ${rs.getString("location")}, Status: ${rs.getString("status")}")
    }

  def viewViolations(): Unit =
    val sql =
      """SELECT v.violation_id, v.vehicle_id, v.signal_id, v.violation_type, v.timestamp,
               veh.license_plate, sig.location
         FROM Violations v
         LEFT JOIN Vehicles veh ON v.vehicle_id = veh.vehicle_id
         LEFT JOIN TrafficSignals sig ON v.signal_id = sig.signal_id
         ORDER BY v.timestamp DESC"""
    Using.resource(Database.getConnection()) { conn =>
      val rs = conn.createStatement().executeQuery(sql)
      println("\n⚠️ Violations:")
      while rs.next() do
        println(
          s"Violation ID: ${rs.getInt("violation_id")}, Vehicle Plate: ${rs.getString("license_plate")}, " +
          s"Signal Location: ${rs.getString("location")}, Type: ${rs.getString("violation_type")}, Time: ${rs.getTimestamp("timestamp")}"
        )
    }

  def updateSignalStatus(signalId: Int, newStatus: String): Unit =
    val sql = "UPDATE TrafficSignals SET status = ? WHERE signal_id = ?"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps = use(conn.prepareStatement(sql))
      ps.setString(1, newStatus)
      ps.setInt(2, signalId)
      val rows = ps.executeUpdate()
      println(s"✅ Signal updated ($rows row).")
    } match
      case Failure(e) => println(s"❌ Error updating signal: ${e.getMessage}")
      case Success(_) => ()

  def deleteVehicle(vehicleId: Int): Unit =
    val sql = "DELETE FROM Vehicles WHERE vehicle_id = ?"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps = use(conn.prepareStatement(sql))
      ps.setInt(1, vehicleId)
      val rows = ps.executeUpdate()
      println(s"✅ Vehicle deleted ($rows row).")
    } match
      case Failure(e) => println(s"❌ Error deleting vehicle: ${e.getMessage}")
      case Success(_) => ()

  def deleteViolation(violationId: Int): Unit =
    val sql = "DELETE FROM Violations WHERE violation_id = ?"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps = use(conn.prepareStatement(sql))
      ps.setInt(1, violationId)
      val rows = ps.executeUpdate()
      println(s"✅ Violation deleted ($rows row).")
    } match
      case Failure(e) => println(s"❌ Error deleting violation: ${e.getMessage}")
      case Success(_) => ()

  def recordViolation(vehicleId: Int, signalId: Int, violationType: String): Unit =
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      conn.setAutoCommit(false)
      try
        val insert = "INSERT INTO Violations (vehicle_id, signal_id, violation_type, timestamp) VALUES (?, ?, ?, ?)"
        val ps = use(conn.prepareStatement(insert))
        ps.setInt(1, vehicleId)
        ps.setInt(2, signalId)
        ps.setString(3, violationType)
        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()))
        ps.executeUpdate()
        conn.commit()
        println("✅ Violation recorded successfully.")
      catch
        case e: Exception =>
          conn.rollback()
          println(s"❌ Error recording violation: ${e.getMessage}")
      finally
        conn.setAutoCommit(true)
    }
end TrafficDAO

// -------------------- Main Program --------------------
@main def TrafficMain(): Unit =
  DatabaseSetup.createTables()
  println("\n🚦 Smart Traffic Management System 🚦")

  var continue = true
  while continue do
    println(
      """|
         |1. Add Vehicle
         |2. Add Traffic Signal
         |3. Record Violation
         |4. Update Signal Status
         |5. View Vehicles
         |6. View Traffic Signals
         |7. View Violations
         |8. Delete Vehicle
         |9. Delete Violation
         |0. Exit
         |""".stripMargin)

    val choice = Try(scala.io.StdIn.readInt()).getOrElse(-1)
    choice match
      case 1 =>
        val plate = scala.io.StdIn.readLine("License plate: ")
        val vtype = scala.io.StdIn.readLine("Vehicle type: ")
        val owner = scala.io.StdIn.readLine("Owner name: ")
        TrafficDAO.addVehicle(plate, vtype, owner)
      case 2 =>
        val loc = scala.io.StdIn.readLine("Location: ")
        val status = scala.io.StdIn.readLine("Status (green/yellow/red): ")
        TrafficDAO.addTrafficSignal(loc, status)
      case 3 =>
        val vid = Try(scala.io.StdIn.readLine("Vehicle ID: ").toInt).getOrElse(-1)
        val sid = Try(scala.io.StdIn.readLine("Signal ID: ").toInt).getOrElse(-1)
        val vtype = scala.io.StdIn.readLine("Violation type: ")
        if vid > 0 && sid > 0 then TrafficDAO.recordViolation(vid, sid, vtype)
        else println("❌ Invalid IDs.")
      case 4 =>
        val sid = Try(scala.io.StdIn.readLine("Signal ID: ").toInt).getOrElse(-1)
        val newStatus = scala.io.StdIn.readLine("New status: ")
        if sid > 0 then TrafficDAO.updateSignalStatus(sid, newStatus)
        else println("❌ Invalid Signal ID.")
      case 5 => TrafficDAO.viewVehicles()
      case 6 => TrafficDAO.viewSignals()
      case 7 => TrafficDAO.viewViolations()
      case 8 =>
        val id = Try(scala.io.StdIn.readLine("Vehicle ID to delete: ").toInt).getOrElse(-1)
        if id > 0 then TrafficDAO.deleteVehicle(id) else println("❌ Invalid ID.")
      case 9 =>
        val id = Try(scala.io.StdIn.readLine("Violation ID to delete: ").toInt).getOrElse(-1)
        if id > 0 then TrafficDAO.deleteViolation(id) else println("❌ Invalid ID.")
      case 0 =>
        println("👋 Exiting.")
        continue = false
      case _ =>
        println("❌ Invalid choice, try again.")
end TrafficMain
