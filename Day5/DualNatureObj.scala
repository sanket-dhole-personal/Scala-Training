
object Email{
    def apply(user:String,domain:String):String=s"$user@$domain"
    def unapply(email:String):Option[(String,String)]={
        var part:Array[String]=email.split("@")
        if (part.length==2) Some((part(0),part(1))) else None
        

    }
}
object DualNatureObj extends App{

    val e = Email("alice", "mail.com")
    println(e) // alice@mail.com
    e match {
    case Email(user, domain) => println(s"User: $user, Domain: $domain")
    case _ => println("Invalid email") 
    }
}

