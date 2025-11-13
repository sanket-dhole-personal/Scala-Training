// File: FileDownloadSimulator.scala
// Scala 3-compatible, fully functional multithread example

class DownloadTask(val fileName: String, val downloadSpeed: Int) extends Runnable:
  override def run(): Unit =
    for percent <- 10 to 100 by 10 do
      Thread.sleep(downloadSpeed) // simulate download delay
      println(s"$fileName: $percent% downloaded")
    println(s"$fileName download completed!")
end DownloadTask


@main def FileDownloadSimulator(): Unit =
  println("📥 Starting File Download Progress Simulator...\n")

  // Create threads
  val t1 = new Thread(new DownloadTask("FileA.zip", 500))
  val t2 = new Thread(new DownloadTask("FileB.mp4", 300))
  val t3 = new Thread(new DownloadTask("FileC.pdf", 200))

  // Start them
  t1.start()
  t2.start()
  t3.start()

  // Wait for all to complete
  t1.join()
  t2.join()
  t3.join()

  println("\n✅ All downloads completed successfully!")
end FileDownloadSimulator
