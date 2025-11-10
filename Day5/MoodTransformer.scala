
object MoodTransformer extends App{
    def moodChanger(prefix: String): String => String = {
       word => s"$prefix-$word-$prefix"
    }
    val happyMood = moodChanger("happy")
    println(happyMood("day"))   // happy-day-happy

    



}