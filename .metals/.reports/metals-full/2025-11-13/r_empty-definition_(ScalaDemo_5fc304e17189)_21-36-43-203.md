error id: file://<WORKSPACE>/Day6/Question6/MySQLCon.scala:prepareStatement.
file://<WORKSPACE>/Day6/Question6/MySQLCon.scala
empty definition using pc, found symbol in pc: prepareStatement.
semanticdb not found
empty definition using fallback
non-local guesses:
	 -conn/prepareStatement.
	 -conn/prepareStatement#
	 -conn/prepareStatement().
	 -scala/Predef.conn.prepareStatement.
	 -scala/Predef.conn.prepareStatement#
	 -scala/Predef.conn.prepareStatement().
offset: 3217
uri: file://<WORKSPACE>/Day6/Question6/MySQLCon.scala
text:
```scala
import java.sql.{Connection, DriverManager, ResultSet, Statement}
import scala.util.{Try, Success, Failure}
import scala.util.Using



object DatabaseExample1 {
  def main(args: Array[String]): Unit = {
    // Load the JDBC driver
    Class.forName("com.mysql.cj.jdbc.Driver")

    // Establish a connection
    val url = "jdbc:mysql://azuremysql8823.mysql.database.azure.com:3306/sanket"
    val username = "mysqladmin"
    val password = "Password@12345"
    val connection: Connection = DriverManager.getConnection(url, username, password)

    try {
      // Create a statement
      val statement: Statement = connection.createStatement()

      // Create a table
      val createVehicle =
        """
          CREATE TABLE Vehicles (
              vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
              license_plate VARCHAR(20),
              vehicle_type VARCHAR(20),
              owner_name VARCHAR(100)
            );
          """

      statement.execute(createVehicle)
      println("Vehicles Table created successfully.")


      val trafficSignals =
        """
          CREATE TABLE TrafficSignals (
                signal_id INT PRIMARY KEY AUTO_INCREMENT,
                location VARCHAR(100),
                status VARCHAR(10)
            );
          """

      statement.execute(trafficSignals)
      println("TrafficSignals Table created successfully.")

      val violations =
        """
          CREATE TABLE Violations (
                violation_id INT PRIMARY KEY AUTO_INCREMENT,
                vehicle_id INT,
                signal_id INT,
                violation_type VARCHAR(50),
                timestamp DATETIME,
                FOREIGN KEY (vehicle_id) REFERENCES Vehicles(vehicle_id),
                FOREIGN KEY (signal_id) REFERENCES TrafficSignals(signal_id)
            );
          """

      statement.execute(violations)
      println("Violations Table created successfully.")

    //   // Insert some data
    //   val insertSQL =
    //     """
    //       INSERT INTO employees (name, age)
    //        VALUES ('John Doe', 30),
    //            ('Jane Smith', 25)
    //       """

    //   statement.executeUpdate(insertSQL)
    //   println("Data inserted successfully.")

    //   // Query the data
    //   val query = "SELECT * FROM employees"
    //   val resultSet: ResultSet = statement.executeQuery(query)

    //   // Process the ResultSet
    //   println("Employees:")
    //   while (resultSet.next()) {
    //     val id = resultSet.getInt("id")
    //     val name = resultSet.getString("name")
    //     val age = resultSet.getInt("age")
    //     println(s"ID: $id, Name: $name, Age: $age")
    //   }
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      // Close Statement and Connection
      connection.close()
    }
  }
}

object TrafficDAO:

  // ---------- Create ----------
  def addVehicle(licensePlate: String, vehicleType: String, ownerName: String): Unit =
    val sql = "INSERT INTO Vehicles (license_plate, vehicle_type, owner_name) VALUES (?, ?, ?)"
    Using.Manager { use =>
    //   val conn = use(Database.getConnection())
      val conn = use(connection)

      val ps   = use(conn.prepar@@eStatement(sql))
      ps.setString(1, licensePlate)
      ps.setString(2, vehicleType)
      ps.setString(3, ownerName)
      val count = ps.executeUpdate()
      println(s"Inserted vehicle rows: $count")
    } match
      case Failure(e) => println(s"Error inserting vehicle: ${e.getMessage}")
      case Success(_)  => ()

  def addTrafficSignal(location: String, status: String): Unit =
    val sql = "INSERT INTO TrafficSignals (location, status) VALUES (?, ?)"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps   = use(conn.prepareStatement(sql))
      ps.setString(1, location)
      ps.setString(2, status)
      val count = ps.executeUpdate()
      println(s"Inserted traffic signal rows: $count")
    } match
      case Failure(e) => println(s"Error inserting signal: ${e.getMessage}")
      case Success(_)  => ()

  // ---------- Read ----------
  def viewVehicles(): Unit =
    val sql = "SELECT vehicle_id, license_plate, vehicle_type, owner_name FROM Vehicles"
    Using.resource(Database.getConnection()) { conn =>
      Using.resource(conn.createStatement()) { stmt =>
        Using.resource(stmt.executeQuery(sql)) { rs =>
          println("Vehicles:")
          while rs.next() do
            println(s"ID: ${rs.getInt("vehicle_id")}, Plate: ${rs.getString("license_plate")}, Type: ${rs.getString("vehicle_type")}, Owner: ${rs.getString("owner_name")}")
        }
      }
    }

  def viewSignals(): Unit =
    val sql = "SELECT signal_id, location, status FROM TrafficSignals"
    Using.resource(Database.getConnection()) { conn =>
      Using.resource(conn.createStatement()) { stmt =>
        Using.resource(stmt.executeQuery(sql)) { rs =>
          println("Traffic Signals:")
          while rs.next() do
            println(s"ID: ${rs.getInt("signal_id")}, Location: ${rs.getString("location")}, Status: ${rs.getString("status")}")
        }
      }
    }

  def viewViolations(): Unit =
    val sql =
      """SELECT v.violation_id, v.vehicle_id, v.signal_id, v.violation_type, v.timestamp,
               veh.license_plate, sig.location
         FROM Violations v
         LEFT JOIN Vehicles veh ON v.vehicle_id = veh.vehicle_id
         LEFT JOIN TrafficSignals sig ON v.signal_id = sig.signal_id
         ORDER BY v.timestamp DESC
      """
    Using.resource(Database.getConnection()) { conn =>
      Using.resource(conn.createStatement()) { stmt =>
        Using.resource(stmt.executeQuery(sql)) { rs =>
          println("Violations:")
          while rs.next() do
            println(
              s"Violation ID: ${rs.getInt("violation_id")}, Vehicle ID: ${rs.getInt("vehicle_id")}, Plate: ${rs.getString("license_plate")}, " +
              s"Signal ID: ${rs.getInt("signal_id")}, Signal Location: ${rs.getString("location")}, Type: ${rs.getString("violation_type")}, At: ${rs.getTimestamp("timestamp")}"
            )
        }
      }
    }

  // ---------- Update ----------
  def updateSignalStatus(signalId: Int, newStatus: String): Unit =
    val sql = "UPDATE TrafficSignals SET status = ? WHERE signal_id = ?"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps   = use(conn.prepareStatement(sql))
      ps.setString(1, newStatus)
      ps.setInt(2, signalId)
      val count = ps.executeUpdate()
      println(s"Updated signals rows: $count")
    } match
      case Failure(e) => println(s"Error updating signal: ${e.getMessage}")
      case Success(_)  => ()

  // ---------- Delete ----------
  def deleteVehicle(vehicleId: Int): Unit =
    val sql = "DELETE FROM Vehicles WHERE vehicle_id = ?"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps   = use(conn.prepareStatement(sql))
      ps.setInt(1, vehicleId)
      val count = ps.executeUpdate()
      println(s"Deleted vehicles rows: $count")
    } match
      case Failure(e) => println(s"Error deleting vehicle: ${e.getMessage}")
      case Success(_)  => ()

  def deleteViolation(violationId: Int): Unit =
    val sql = "DELETE FROM Violations WHERE violation_id = ?"
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      val ps   = use(conn.prepareStatement(sql))
      ps.setInt(1, violationId)
      val count = ps.executeUpdate()
      println(s"Deleted violation rows: $count")
    } match
      case Failure(e) => println(s"Error deleting violation: ${e.getMessage}")
      case Success(_)  => ()

  // ---------- Record violation (transaction-safe) ----------
  def recordViolation(vehicleId: Int, signalId: Int, violationType: String): Unit =
    // We will: start transaction, ensure vehicle and signal exist (optional), insert violation, commit/rollback
    Using.Manager { use =>
      val conn = use(Database.getConnection())
      conn.setAutoCommit(false)
      try
        // optional checks
        val checkVehicleSql = "SELECT COUNT(*) AS cnt FROM Vehicles WHERE vehicle_id = ?"
        val checkSignalSql  = "SELECT COUNT(*) AS cnt FROM TrafficSignals WHERE signal_id = ?"

        val cv = use(conn.prepareStatement(checkVehicleSql))
        cv.setInt(1, vehicleId)
        val cvRs = use(cv.executeQuery())
        val vehicleExists = if cvRs.next() then cvRs.getInt("cnt") > 0 else false

        val cs = use(conn.prepareStatement(checkSignalSql))
        cs.setInt(1, signalId)
        val csRs = use(cs.executeQuery())
        val signalExists = if csRs.next() then csRs.getInt("cnt") > 0 else false

        if !vehicleExists then
          throw new SQLException(s"Vehicle id $vehicleId does not exist")
        if !signalExists then
          throw new SQLException(s"Signal id $signalId does not exist")

        val insertSql = "INSERT INTO Violations (vehicle_id, signal_id, violation_type, timestamp) VALUES (?, ?, ?, ?)"
        val ps = use(conn.prepareStatement(insertSql))
        ps.setInt(1, vehicleId)
        ps.setInt(2, signalId)
        ps.setString(3, violationType)
        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()))
        val inserted = ps.executeUpdate()
        conn.commit()
        println(s"Violation recorded, rows inserted: $inserted")
      catch
        case e: Exception =>
          println(s"Error recording violation: ${e.getMessage}, rolling back")
          conn.rollback()
          throw e
      finally
        conn.setAutoCommit(true)
    } match
      case Failure(e) => println(s"Record violation failed: ${e.getMessage}")
      case Success(_) => ()

object TrafficMain:
  private def readLine(prompt: String): String =
    print(prompt); scala.io.StdIn.readLine()

  def main(args: Array[String]): Unit =
    println("Smart Traffic Management System")
    var continue = true
    while continue do
      println(
        """|
1. Add Vehicle
2. Add Traffic Signal
3. Record Violation
4. Update Signal Status
5. View Vehicles
6. View Traffic Signals
7. View Violations
8. Delete Vehicle
9. Delete Violation
0. Exit
""".stripMargin)
      val choice = Try(scala.io.StdIn.readInt()).getOrElse(-1)
      choice match
        case 1 =>
          val plate = readLine("License plate: ")
          val vtype = readLine("Vehicle type (car/bike/truck): ")
          val owner = readLine("Owner name: ")
          TrafficDAO.addVehicle(plate, vtype, owner)
        case 2 =>
          val loc = readLine("Location: ")
          val status = readLine("Status (green/yellow/red): ")
          TrafficDAO.addTrafficSignal(loc, status)
        case 3 =>
          val vehicleId = Try(readLine("Vehicle ID: ").toInt).getOrElse(-1)
          val signalId  = Try(readLine("Signal ID: ").toInt).getOrElse(-1)
          val vtype     = readLine("Violation type (speeding/signal jump): ")
          if vehicleId > 0 && signalId > 0 then TrafficDAO.recordViolation(vehicleId, signalId, vtype)
          else println("Invalid IDs")
        case 4 =>
          val signalId = Try(readLine("Signal ID: ").toInt).getOrElse(-1)
          val newStatus = readLine("New status: ")
          if signalId > 0 then TrafficDAO.updateSignalStatus(signalId, newStatus)
          else println("Invalid Signal ID")
        case 5 => TrafficDAO.viewVehicles()
        case 6 => TrafficDAO.viewSignals()
        case 7 => TrafficDAO.viewViolations()
        case 8 =>
          val id = Try(readLine("Vehicle ID to delete: ").toInt).getOrElse(-1)
          if id > 0 then TrafficDAO.deleteVehicle(id) else println("Invalid ID")
        case 9 =>
          val id = Try(readLine("Violation ID to delete: ").toInt).getOrElse(-1)
          if id > 0 then TrafficDAO.deleteViolation(id) else println("Invalid ID")
        case 0 =>
          println("Exiting.")
          continue = false
        case _ =>
          println("Invalid choice, try again.")








// scala-cli run MySQLCon.scala --classpath mysql-connector-j-8.0.32.jar

```


#### Short summary: 

empty definition using pc, found symbol in pc: prepareStatement.