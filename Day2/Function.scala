object Function{
    def getName(name: String) : String={
        return name;
    }

    def defaultParam(name : String="John" ) ={
      println(s"Hii $name");
    }
    def default()={
        println("Without param method")
    }

    def defaultParam(name : String , id : Int)={
        println(s"Hii $name your id is $id ");
    }

    def main(args : Array[String]): Unit={
        var n: String =getName("Sanket");
        println(n)
        defaultParam();
        defaultParam("Rock")
        defaultParam("Sanket",20);
    }
}