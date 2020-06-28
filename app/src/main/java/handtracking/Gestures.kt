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

    //K("k", "K", com.example.HandsTrackingApp.R.drawable.k),
    L("l", "L", com.example.HandsTrackingApp.R.drawable.l),

    //M("m", "M", com.example.HandsTrackingApp.R.drawable.m),
    //N("n", "N", com.example.HandsTrackingApp.R.drawable.n),
    O("o", "O", com.example.HandsTrackingApp.R.drawable.o),

    //P("p", "P", com.example.HandsTrackingApp.R.drawable.p),
    //Q("q", "Q", com.example.HandsTrackingApp.R.drawable.q),
    R("r", "R", com.example.HandsTrackingApp.R.drawable.r),

    //S("s", "S", com.example.HandsTrackingApp.R.drawable.s),
    //T("t", "T", com.example.HandsTrackingApp.R.drawable.t),
    U("u", "U", com.example.HandsTrackingApp.R.drawable.u),
    V("v", "V", com.example.HandsTrackingApp.R.drawable.v),
    W("w", "W", com.example.HandsTrackingApp.R.drawable.w),

    //X("x", "X", com.example.HandsTrackingApp.R.drawable.x),
    Y("y", "Y", com.example.HandsTrackingApp.R.drawable.y),

    //Z("z", "Z", com.example.HandsTrackingApp.R.drawable.z),
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