package handtracking

enum class Gestures(
        val id: String,
        val label: String,
        val image: Int
) {
    A("a", "A", com.example.HandsTrackingApp.R.drawable.a),
    B("b", "B", com.example.HandsTrackingApp.R.drawable.b),
    C("c", "C", com.example.HandsTrackingApp.R.drawable.c),
    D("d", "D", com.example.HandsTrackingApp.R.drawable.d),
    E("e", "E", com.example.HandsTrackingApp.R.drawable.e),
    F("f", "F", com.example.HandsTrackingApp.R.drawable.f),
    G("g", "G", com.example.HandsTrackingApp.R.drawable.g),
    H("h", "H", com.example.HandsTrackingApp.R.drawable.h),
    I("i", "I", com.example.HandsTrackingApp.R.drawable.i),
    J("j", "J", com.example.HandsTrackingApp.R.drawable.j),
    L("l", "L", com.example.HandsTrackingApp.R.drawable.l),
    O("o", "O", com.example.HandsTrackingApp.R.drawable.o),
    R("r", "R", com.example.HandsTrackingApp.R.drawable.r),
    U("u", "U", com.example.HandsTrackingApp.R.drawable.u),
    V("v", "V", com.example.HandsTrackingApp.R.drawable.v),
    W("w", "W", com.example.HandsTrackingApp.R.drawable.w),
    Y("y", "Y", com.example.HandsTrackingApp.R.drawable.y),
    LIVE_LONG("live_long", "Live long and prosper.", com.example.HandsTrackingApp.R.drawable.live_long),
    ONE("one", "One", com.example.HandsTrackingApp.R.drawable.one),
    TWO("two", "Two", com.example.HandsTrackingApp.R.drawable.two),
    THREE("three", "Three", com.example.HandsTrackingApp.R.drawable.three),
    FOUR("four", "Four", com.example.HandsTrackingApp.R.drawable.four),
    FIVE("five", "Five", com.example.HandsTrackingApp.R.drawable.five),
    SIX("six", "Six", com.example.HandsTrackingApp.R.drawable.six),
    SEVEN("seven", "Seven", com.example.HandsTrackingApp.R.drawable.seven),
    EIGHT("eight", "Eight", com.example.HandsTrackingApp.R.drawable.eight),
    NINE("nine", "Nine", com.example.HandsTrackingApp.R.drawable.nine),
    BAD("bad", "Bad", com.example.HandsTrackingApp.R.drawable.bad),
    PENCIL("pencil", "Pencil", com.example.HandsTrackingApp.R.drawable.pencil),
    RESTROOM("restroom", "Restroom", com.example.HandsTrackingApp.R.drawable.restroom),
    LOVE("love", "I love you", com.example.HandsTrackingApp.R.drawable.love),
    OK("ok", "Good", com.example.HandsTrackingApp.R.drawable.ok),
    NO("no", "No", com.example.HandsTrackingApp.R.drawable.no),
    BYE("bye", "Goodbye", com.example.HandsTrackingApp.R.drawable.bye);

}