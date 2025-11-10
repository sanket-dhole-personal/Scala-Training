
class CustomException(message:String,errorCode:Int) extends Exception{
     override def toString:String={s"ErrorCode= $errorCode , Message= $message"}
     override def getMessage:String={s"Message = $message"}
}

object test5{
    def main(args:Array[String]):Unit={
        try{
            throw new CustomException("CustomException Happen",404)
        }catch{
            case e: CustomException=>println(s"CustomException ${e.toString}")
            case e: Exception=>println(s"Exception ${e.toString()}")
        }
    }
}