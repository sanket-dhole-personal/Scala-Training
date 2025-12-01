
import java.io._
import scala.util.Random
import java.time._
import java.time.format.DateTimeFormatter

object DataGenerate {

  def main(args: Array[String]): Unit = {

    val outFile = if (args.length > 0) args(0) else "urbanmove_trips.csv"
    val bw = new BufferedWriter(new FileWriter(outFile))

    val areas = Array(
      "MG Road", "Indira Nagar", "Koramangala", "Whitefield",
      "Marathahalli", "HSR Layout", "BTM", "Jayanagar"
    )

    val vehicleTypes = Array("AUTO", "TAXI", "BIKE")
    val paymentMethods = Array("CASH", "UPI", "CARD")

    val rand = new Random(42)  // deterministic seed
    val fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // CSV header
    bw.write("tripId,driverId,vehicleType,startTime,endTime,startLocation,endLocation,distanceKm,fareAmount,paymentMethod,customerRating\n")

    // Generate random timestamp within ~70 days
    def randomDateTime(): LocalDateTime = {
      val now = LocalDateTime.now()
      now.minusMinutes(rand.nextInt(100000)) // up to ~69 days
    }

    val total = 1000000
    var i = 1

    println(s"Generating $total synthetic trip records...")

    while (i <= total) {
      val start = randomDateTime()
      val durationMinutes = 5 + rand.nextInt(50)
      val end = start.plusMinutes(durationMinutes)

      val distance = BigDecimal(1 + rand.nextDouble() * 15)
        .setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

      val fare = BigDecimal(distance * (10 + rand.nextInt(10)))
        .setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

      val rating = BigDecimal(1.0 + rand.nextDouble() * 4.0)
        .setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

      val line =
        s"$i,${rand.nextInt(5000)},${vehicleTypes(rand.nextInt(vehicleTypes.length))}," +
          s"${start.format(fmt)},${end.format(fmt)}," +
          s"${areas(rand.nextInt(areas.length))},${areas(rand.nextInt(areas.length))}," +
          s"$distance,$fare,${paymentMethods(rand.nextInt(paymentMethods.length))},$rating\n"

      bw.write(line)

      if (i % 100000 == 0) {
        bw.flush()
        println(s"$i rows generated...")
      }
      i += 1
    }

    bw.close()
    println(s"✔ Done! Written $total rows to: $outFile")
  }
}
