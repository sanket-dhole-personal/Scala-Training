

case class Transaction(id:Int,amount:Long,description:String,total:Double)

object Transaction{
    def apply(id:Int,amount:Long,description:String)(block:Double=>Double):Transaction={
        var amo=block(amount)
        var total=amount+amo
        println(s"Processed amount for transaction is $id : $amo")
        new Transaction(id,amount,description,total)
    }
}

@main
def Test4():Unit ={
    var tr=Transaction(10,100,"Food"){amount=> amount*0.02}
    var tr1=Transaction(10,100,"Food"){amount=>{ if(amount>50){amount*0.03}else{amount}}}
    println(tr)
    println(tr1)
}