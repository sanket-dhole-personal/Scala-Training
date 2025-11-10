@main
def ConditionalStatements : Unit ={
  var age : Int=21;
  if(age>20){
    println("age is greter than 20");
  }else{
    println("age is less than 20")
  }

  var department : String ="Civil";

  var check=department match{
    case "IT" => println("IT");
    case "Civil" => println("Civil")
  }

}