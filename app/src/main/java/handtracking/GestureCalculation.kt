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

            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "B"
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Y"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && landmarkList[8].x < landmarkList[12].x && GestureCalculationHelper.areFingersTouching(landmarkList[12], landmarkList[8]) -> "R"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.areFingersTouching(landmarkList[12], landmarkList[8]) && landmarkList[8].x > landmarkList[12].x -> "U"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.areFingersNear(landmarkList[12], landmarkList[8]) -> "V"
            !thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && landmarkList[8].y > 0.6 -> "G"
            !secondFingerIsOpen && GestureCalculationHelper.areFingersNear(landmarkList[4], landmarkList[8]) && landmarkList[8].y > 0.4 && GestureCalculationHelper.areFingersNear(landmarkList[4], landmarkList[12]) && GestureCalculationHelper.areFingersNear(landmarkList[4], landmarkList[16]) && GestureCalculationHelper.areFingersNear(landmarkList[4], landmarkList[20]) -> "C"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[12]) && landmarkList[8].x >= 0.35 && landmarkList[12].y >= 0.48 && landmarkList[16].y >= 0.48 && landmarkList[20].y >= 0.48 -> "D"
            !thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "E"
            !firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[8]) -> "F"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && landmarkList[8].y > 0.6 -> "H"
            !thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen && landmarkList[20].y <= 0.5 -> "I"
            !thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen && landmarkList[20].y > 0.5 -> "J"
            GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[8]) && landmarkList[8].y > 0.4 && GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[12]) && GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[16]) && GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[20]) -> "O"
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "A"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && !fourthFingerIsOpen -> "W"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "L"


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
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.areFingersTouching(landmarkList[8], landmarkList[12]) && !GestureCalculationHelper.areFingersTouching(landmarkList[12], landmarkList[16]) && GestureCalculationHelper.areFingersTouching(landmarkList[16], landmarkList[20]) -> "Live long and prosper."
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Jeden"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Dwa"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && !GestureCalculationHelper.areFingersTouching(landmarkList[8], landmarkList[12]) -> "Trzy"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "Cztery"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && !GestureCalculationHelper.areFingersTouching(landmarkList[8], landmarkList[12]) && !GestureCalculationHelper.areFingersTouching(landmarkList[12], landmarkList[16]) && !GestureCalculationHelper.areFingersTouching(landmarkList[16], landmarkList[20]) -> "Pięć"
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Źle"
            !thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Ołówek"
            !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.areFingersTouching(landmarkList[8], landmarkList[12]) -> "Toaleta"
            thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Kocham Cię"
            GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[8]) && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && !firstFingerIsOpen -> "Dobrze!"
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
            thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[8]) && GestureCalculationHelper.areFingersTouching(landmarkList[8], landmarkList[12]) -> "no_p_1"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen && !GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[8]) && !GestureCalculationHelper.areFingersTouching(landmarkList[4], landmarkList[12]) && GestureCalculationHelper.areFingersTouching(landmarkList[8], landmarkList[12]) -> "no_p_2"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.areFingersTouching(landmarkList[8], landmarkList[12]) && GestureCalculationHelper.areFingersTouching(landmarkList[12], landmarkList[16]) && GestureCalculationHelper.areFingersTouching(landmarkList[16], landmarkList[20]) -> "goodbye_p_2"
            else -> ""
        }
    }

    fun rpsGestureCalculation(): String {
        return when {
            !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Rock!"
            thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "Paper!"
            firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Scissors!"
            else -> ""
        }
    }

    companion object {
        fun gestureFromParts(g: Pair<String, String>): String {
            return when {
                (g.first == "no_p_1" && g.second == "no_p_2") || (g.first == "no_p_2" && g.second == "no_p_1") -> "Nie"
                (g.first == "goodbye_p_1" && g.second == "goodbye_p_2") || (g.first == "goodbye_p_2" && g.second == "goodbye_p_1") -> "Do widzenia."
                else -> "-"
            }
        }
    }

}