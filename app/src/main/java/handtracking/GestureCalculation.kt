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

    fun gestureCalculationLetters(): String {
        return when {
            !thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "A"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "B"
            GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[12]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[16]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[20]) -> "C"
            firstFingerIsOpen && GestureCalculationHelper.isThumbTouchingFinger(landmarkList[4], landmarkList[8]) && GestureCalculationHelper.isThumbTouchingFinger(landmarkList[4], landmarkList[16]) && GestureCalculationHelper.isThumbTouchingFinger(landmarkList[4], landmarkList[20]) -> "D"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "H"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && !fourthFingerIsOpen -> "W"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "L"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "V"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "T"
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Y"
            !thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "G"
            !firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) -> "F"
            else -> {
                val info = ("thumbIsOpen " + thumbIsOpen + "firstFingerIsOpen" + firstFingerIsOpen
                        + "secondFingerIsOpen" + secondFingerIsOpen +
                        "thirdFingerIsOpen" + thirdFingerIsOpen + "fourthFingerIsOpen" + fourthFingerIsOpen)
                Log.d("TAG", "handGestureCalculator: == $info")
                "___"
            }
        }
    }

    fun gestureCalculation(): String {
        return when {
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) && !GestureCalculationHelper.isThumbNearFinger(landmarkList[12], landmarkList[16]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[16], landmarkList[20]) -> "Live long and prosper."
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && !GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) && !GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) -> "Jeden"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Dwa"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && !GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) -> "Trzy"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "Cztery"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && !GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) && !GestureCalculationHelper.isThumbNearFinger(landmarkList[12], landmarkList[16]) && !GestureCalculationHelper.isThumbNearFinger(landmarkList[16], landmarkList[20]) -> "Pięć"
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Źle"
            !thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Ołówek"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) -> "Toaleta"
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

    fun partGestureCalculation(): String {
        return when {
            !thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "goodbye_p_1"
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) -> "no_p_1"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && !GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) && !GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[12]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) -> "no_p_2"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[8], landmarkList[12]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[12], landmarkList[16]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[16], landmarkList[20]) -> "goodbye_p_2"

            else -> ""

        }
    }

    companion object {
        fun gestureFromParts(g: Pair<String, String>): String {
            val gest = when {
                (g.first == "no_p_1" && g.second == "no_p_2") || (g.first == "no_p_2" && g.second == "no_p_1") -> "Nie"
                (g.first == "goodbye_p_1" && g.second == "goodbye_p_2") || (g.first == "goodbye_p_2" && g.second == "goodbye_p_1") -> "Do widzenia."
                else -> "-"
            }
            return gest
        }
    }

}