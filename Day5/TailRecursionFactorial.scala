

object TailRecursionFactorial extends App{

    def factorial(n:Int):Int={

        @annotation.tailrec
        def factHelper(x:Int, accumulator:Int):Int={
            if(x<=1) accumulator
            else factHelper(x-1, x*accumulator)
        }

        factHelper(n,1)
    }

    println(factorial(5))
}

