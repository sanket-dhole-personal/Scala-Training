object DigitSumUsingRecursion{

    def digitSum(n:Int):Int={
        if(n==0) 0
        else n%10+digitSum(n/10)
        
    }

    def main(args:Array[String])={
        println(digitSum(12345))
    }
}