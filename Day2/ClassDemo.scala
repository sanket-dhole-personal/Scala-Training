class Sample(val name: String,val age:Int){

println(s"$name age is $age");

}


class Simple(val id : Int =1,val name :String="Sanket");

case class SimpleCase(val id: Int =2, val name : String="John");



object Test{
    def main(args: Array[String]):Unit={
      var sample=new Sample("Sanket",30);
      var sample1=Sample("John",45);
      


      var obj1: Simple=Simple();
      var obj2: Simple=Simple();

      println(obj1);
  
      var obj3 : SimpleCase= SimpleCase();
      var obj4 : SimpleCase= SimpleCase();

      println(obj3);

      println(obj1 == obj2);
      println(obj3 == obj4)

    }
}