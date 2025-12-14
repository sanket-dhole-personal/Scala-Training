package simulator

import model.MeterEvent

import scala.util.Random
import java.time.{LocalDateTime, ZoneId}
import scala.math.Ordered.orderingToOrdered

object MeterGenerator {

  private val rnd = new Random()

  def generateReading(meterId: String, householdId: Int): MeterEvent = {

    // Get hour of day for dynamic usage patterns
    val hour = LocalDateTime.now(ZoneId.of("Asia/Kolkata")).getHour

    // --- Base consumption ---
    var consumption = 5.0 + rnd.nextDouble() * 20.0 // 5–25 liters

    // --- Time-based variation logic ---
    consumption *= hour match {
      case h if h >= 7 && h < 9 => 1.40     // Morning peak +40%
      case h if h >= 18 && h < 21 => 1.50   // Evening peak +50%
      case h if h >= 0 && h < 5 => 0.50     // Night low usage -50%
      case _ => 1.0
    }

    // --- Pressure base (normal 2.0 ± 0.3) ---
    var pressure = 2.0 + rnd.nextGaussian() * 0.3

    var status = "OK"

    // ======== ANOMALIES (2% chance) ========
    val anomalyChance = rnd.nextInt(100) // 0–99 → 2% anomaly rate

    if (anomalyChance < 2) {
      val anomalyType = rnd.nextInt(2)

      anomalyType match {

        // ---------------- SPIKE ----------------
        case 0 =>
          consumption = 80 + rnd.nextDouble() * 120  // 80–200 liters
          status = "SPIKE"

        // -------------- PRESSURE DROP ----------
        case 1 =>
          pressure = 0.2 + rnd.nextDouble() * 0.4    // 0.2–0.6 bar
          status = "PRESSURE_DROP"
      }
    }

    // Round numbers
    val finalConsumption = BigDecimal(consumption).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble
    val finalPressure = BigDecimal(pressure).setScale(3, BigDecimal.RoundingMode.HALF_UP).toDouble

    // Build event
    MeterEvent(
      meter_id = meterId,
      household_id = householdId,
      consumption_liters = finalConsumption,
      pressure = finalPressure,
      device_status = status,
      timestamp = System.currentTimeMillis()
    )
  }
}
















//package simulator
//
//import model.MeterEvent
//
//import scala.util.Random
//
//object MeterGenerator {
//
//  private val rnd = new Random()
//
//  /** Generate a reading. Keeps it intentionally simple (5-25 liters + pressure jitter). */
//  def generateReading(meterId: String, householdId: Int): MeterEvent = {
//    val consumption = 5.0 + rnd.nextDouble() * 20.0           // 5 - 25 liters
//    val pressure = 2.0 + rnd.nextGaussian() * 0.3            // 2.0 +/- jitter
//    val status = "OK"                                        // simple status
//
//    MeterEvent(
//      meter_id = meterId,
//      household_id = householdId,
//      consumption_liters = BigDecimal(consumption)
//        .setScale(3, BigDecimal.RoundingMode.HALF_UP)
//        .toDouble,
//      pressure = BigDecimal(pressure)
//        .setScale(3, BigDecimal.RoundingMode.HALF_UP)
//        .toDouble,
//      device_status = status,
//      timestamp = System.currentTimeMillis()
//    )
//  }
//}
