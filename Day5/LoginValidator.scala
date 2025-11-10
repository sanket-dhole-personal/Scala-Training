object LoginValidator extends App{
    def validateLogin(userName:String,password:String):Either[String,String]={
        if(userName.isEmpty())Left("Username cannot be null")
        else if(password.isEmpty())Left("Password cannot be null")
        else Right("Login Successful")
    }

    println(validateLogin("","password123")) 
    println(validateLogin("user1","")) 
    println(validateLogin("user1","password123")) 
}