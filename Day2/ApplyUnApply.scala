class Sample4(val empId: Int,val empName : String);

class Sample3(val empId: Int,val empName : String);
object Sample3{
    def apply(empId : Int, empName : String) : Sample3= new Sample3(empId,empName);
    def unapply(smp : Sample3): Option[(Int,String)]= Some(smp.empId,smp.empName);
} 



case class Sample1(val empId: Int,val empName : String);


object Test1 extends App{

  var obj=Sample4(1,"John");
  var obj1=Sample1(1,"John");

 // We get error becuse unapply method not created
  var obj3=Sample3(3,"Sam");
  obj3 match
    case Sample3(id,name) => println(s"Simple class works with $id and $name");
    case _ => println("Default works");
  
  obj1 match
    case Sample1(id,name) => println(s"Simple class works with $id and $name");
    case _ => println("Default works");
  

}