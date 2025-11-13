trait T1{
    def m1():Unit={
        println("m1 from T1")
    }
}

trait T2 extends T1{
    abstract override def m1():Unit={
        super.m1()
        println("m1 from T2")
    }
}

trait T3 extends T1{
    
    abstract override def m1(): Unit ={
        super.m1()
        println("m1 from T3 ")
    }
}

trait T4 extends T1{
    
    abstract override def m1(): Unit ={
        super.m1()  
        println("m1 from T4")
    }
}

object Main extends App{
    val T4=new T3 with T2 with T4{
        override def m1(): Unit = {
            super.m1()
            print("m1 from anonymous class ")
        }
    }
    T4.m1()
}