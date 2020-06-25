package handtracking

import android.util.Log
import com.google.mediapipe.formats.proto.LandmarkProto

class GestureCalculation (
        val landmarkList: List<LandmarkProto.NormalizedLandmark>,
        val hand: String = "L"
){

    var thumbIsOpen = false
    var firstFingerIsOpen = false
    var secondFingerIsOpen = false
    var thirdFingerIsOpen = false
    var fourthFingerIsOpen = false
    var pseudoFixKeyPoint = landmarkList[2].x

    init {
        if (pseudoFixKeyPoint < landmarkList[9].x)
            if (landmarkList[3].x < pseudoFixKeyPoint && landmarkList[4].x < pseudoFixKeyPoint)
                thumbIsOpen = true

        if (pseudoFixKeyPoint > landmarkList[9].x)
            if (landmarkList[3].x > pseudoFixKeyPoint && landmarkList[4].x > pseudoFixKeyPoint)
                thumbIsOpen = true

        pseudoFixKeyPoint = landmarkList[6].y
        if (landmarkList[7].y < pseudoFixKeyPoint && landmarkList[8].y < landmarkList[7].y)
            firstFingerIsOpen = true
        pseudoFixKeyPoint = landmarkList[10].y
        if (landmarkList[11].y < pseudoFixKeyPoint && landmarkList[12].y < landmarkList[11].y)
            secondFingerIsOpen = true
        pseudoFixKeyPoint = landmarkList[14].y
        if (landmarkList[15].y < pseudoFixKeyPoint && landmarkList[16].y < landmarkList[15].y)
            thirdFingerIsOpen = true
        pseudoFixKeyPoint = landmarkList[18].y
        if (landmarkList[19].y < pseudoFixKeyPoint && landmarkList[20].y < landmarkList[19].y)
            fourthFingerIsOpen = true
    }

    fun gestureCalculation(): String {
        return when {
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12])&& !GestureCalculationHelper.isThumbNearFinger(landmarkList[12], landmarkList[16]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[16], landmarkList[20]) -> "Live long and prosper."
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Jeden"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Dwa"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Trzy"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "Cztery"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "Pięć"
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Źle"
            !thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Ołówek"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12])  -> "Toaleta"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Kocham Cię <3"
            GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && !firstFingerIsOpen -> "Dobrze!"
            else -> {
                val info = ("thumbIsOpen " + thumbIsOpen + "firstFingerIsOpen" + firstFingerIsOpen
                        + "secondFingerIsOpen" + secondFingerIsOpen +
                        "thirdFingerIsOpen" + thirdFingerIsOpen + "fourthFingerIsOpen" + fourthFingerIsOpen)
                Log.d("TAG", "handGestureCalculator: == $info")
                "___"
            }
        }
    }

}