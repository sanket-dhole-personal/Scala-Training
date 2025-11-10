object Veriable{
  def main(args : Array[String])={
    println("Veriables");

    val names : String ="Sanket";
    // val is immutable so its value not be changed
    // names = "Change";
    println(names);

    var id: Int =10;
    println(id);
    // we can change var value 
    id=20;
    println(id);
  }
}