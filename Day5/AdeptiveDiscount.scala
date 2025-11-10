object AdeptiveDiscount extends App{

    def discountStrategy(memberType: String): Double => Double={
        memberType match{
            case "Gold" => price => price * 0.8
            case "Silver" => price => price * 0.9
            case _ => price => price
        }
    }
    var goldDiscount=discountStrategy("Gold")
    println(goldDiscount(1000))
    var silverDiscount=discountStrategy("Silver")
    println(silverDiscount(1000))
    var regularDiscount=discountStrategy("Regular")
    println(regularDiscount(1000))

}