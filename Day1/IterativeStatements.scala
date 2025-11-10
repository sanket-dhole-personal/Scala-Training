object IterativeStatements{
  def main(args : Array[String])={
    var arr=new Array[String](5);
    arr(0)="0";
    for(a <- arr){
    println(a);
    }
    var list= Array("Str1","Str2","Str3","Str4");
    list.foreach(e=>println(e));

    var list1: Array[String] = Array("A","C","B");
    var i:Int=0;
    while(i<list1.length){
        println(list1(i));
        i += 1
    }

  }
    
}