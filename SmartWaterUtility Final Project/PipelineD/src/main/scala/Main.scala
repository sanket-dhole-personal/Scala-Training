import util.ConfigLoader
import batch.DailyReportsJob

object Main {
  def main(args: Array[String]): Unit = {

    val conf = ConfigLoader.load()

    val dateToProcess =
      if (args.nonEmpty) args(0)
      else "2025-12-12"  // default for testing

    DailyReportsJob.run(dateToProcess, conf)
  }
}
