package Week3.EventManagement.Event$minusmanagement


final class build$_ {
def args = build_sc.args$
def scriptPath = """Week3/EventManagement/Event-management/build.sc"""
/*<script>*/
import mill._
import $ivy.`com.lihaoyi::mill-contrib-playlib:`,  mill.playlib._

object hotelmanagement extends RootModule with PlayModule {

  def scalaVersion = "2.13.17"
  def playVersion = "3.0.9"
  def twirlVersion = "2.0.9"

  object test extends PlayTests
}

/*</script>*/ /*<generated>*//*</generated>*/
}

object build_sc {
  private var args$opt0 = Option.empty[Array[String]]
  def args$set(args: Array[String]): Unit = {
    args$opt0 = Some(args)
  }
  def args$opt: Option[Array[String]] = args$opt0
  def args$: Array[String] = args$opt.getOrElse {
    sys.error("No arguments passed to this script")
  }

  lazy val script = new build$_

  def main(args: Array[String]): Unit = {
    args$set(args)
    val _ = script.hashCode() // hashCode to clear scalac warning about pure expression in statement position
  }
}

export build_sc.script as `build`

