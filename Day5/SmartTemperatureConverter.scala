
object SmartTemperatureConverter{

    def converterTemp(value:Double, scala:String):Double={
        var value1:Double=value
        if(scala=="C"){
            value1=value * 9/5 + 32
        }else if(scala=="F"){
            value1=((value-32)*5)/9
        }
        value1
        

    }
    
    def main(args:Array[String])={
       println(converterTemp(0,"C"))
       println(converterTemp(212,"F"))
       println(converterTemp(50,"X"))
    }
}