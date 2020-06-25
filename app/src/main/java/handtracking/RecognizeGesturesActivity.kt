package handtracking

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.formats.proto.RectProto
import com.google.mediapipe.framework.PacketGetter

class RecognizeGesturesActivity : BasicActivity() {

    companion object {
        private const val TAG = "RecognizeGestures"
        private const val OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks"
        private const val OUTPUT_HAND_RECT = "multi_hand_rects"
    }

    private lateinit var multiHandLandmarks: List<NormalizedLandmarkList>
    private lateinit var gesture: TextView
    private lateinit var moveGesture: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        processor.addPacketCallback(OUTPUT_LANDMARKS_STREAM_NAME) { packet ->
            Log.d(TAG, "Received multi-hand landmarks packet.")
            multiHandLandmarks = PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser())
            Log.d(TAG, "[TS:" + packet.timestamp + "] " + getMultiHandLandmarksDebugString(multiHandLandmarks))
            previewDisplayView.text = handGestureCalculator(multiHandLandmarks) ?: "Cannot recognize."
            previewDisplayView.invalidate()
        }
//        processor.addPacketCallback(OUTPUT_HAND_RECT ){ packet ->
//            val normalizedRectsList: List<RectProto.NormalizedRect> = PacketGetter.getProtoVector(packet, RectProto.NormalizedRect.parser())
//            try {
//                runOnUiThread {
//                    // moveGesture.setText(handGestureMoveCalculator(normalizedRectsList));
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun getMultiHandLandmarksDebugString(multiHandLandmarks: List<NormalizedLandmarkList>): String {
        if (multiHandLandmarks.isEmpty()) return "No hand landmarks"
        var multiHandLandmarksStr = """Number of hands detected: ${multiHandLandmarks.size}""".trimIndent()
        for ((handIndex, landmarks) in multiHandLandmarks.withIndex()) {

            multiHandLandmarksStr += """	#Hand landmarks for hand[$handIndex]: ${landmarks.landmarkCount}"""
            for ((landmarkIndex, landmark) in landmarks.landmarkList.withIndex())
                multiHandLandmarksStr += """		Landmark [$landmarkIndex]: (${landmark.x}, ${landmark.y}, ${landmark.z})"""
        }
        return multiHandLandmarksStr
    }

    private fun handGestureCalculator(multiHandLandmarks: List<NormalizedLandmarkList>): String? {
        if (multiHandLandmarks.isEmpty()) return "No hand deal"
        //jesli są dwie zakładamy ze lewa i prawa
        //jesli jest jedna to tylko lewa.
        println(multiHandLandmarks.size)

        val leftLandmarks = if(multiHandLandmarks.size>0) multiHandLandmarks[0].landmarkList else return "___"
        println(multiHandLandmarks[0].landmarkCount)
        val rightLandmarks = if(multiHandLandmarks.size>1) multiHandLandmarks[1].landmarkList else null
        if(leftLandmarks.size<21) return "X"
        val leftSign = leftGestureCalculation(leftLandmarks)
        if(rightLandmarks != null){
            val rightSign = rightGestureCalculation(rightLandmarks)
            return when{
                leftSign == "___" -> rightSign
                rightSign == "___" -> leftSign
                leftSign == "Jeden" && rightSign == "Pięć" -> "Sześć"
                leftSign == "Dwa" && rightSign == "Pięć" -> "Siedem"
                leftSign == "Trzy" && rightSign == "Pięć" -> "Osiem"
                leftSign == "Cztery" && rightSign == "Pięć" -> "Dziewięć"
                else ->  "___"
        }
        } else return leftSign
//        for (landmarks in multiHandLandmarks) {
//            val landmarkList = landmarks.landmarkList
//            var pseudoFixKeyPoint = landmarkList[2].x
//            if (pseudoFixKeyPoint < landmarkList[9].x)
//                if (landmarkList[3].x < pseudoFixKeyPoint && landmarkList[4].x < pseudoFixKeyPoint)
//                    thumbIsOpen = true
//            if (pseudoFixKeyPoint > landmarkList[9].x)
//                if (landmarkList[3].x > pseudoFixKeyPoint && landmarkList[4].x > pseudoFixKeyPoint)
//                    thumbIsOpen = true
////            Log.d(TAG, """
////     pseudoFixKeyPoint == $pseudoFixKeyPoint
////     landmarkList.get(2).getX() == ${landmarkList[2].x}
////     landmarkList.get(4).getX() = ${landmarkList[4].x}
////     """.trimIndent())
//            pseudoFixKeyPoint = landmarkList[6].y
//            if (landmarkList[7].y < pseudoFixKeyPoint && landmarkList[8].y < landmarkList[7].y)
//                firstFingerIsOpen = true
//            pseudoFixKeyPoint = landmarkList[10].y
//            if (landmarkList[11].y < pseudoFixKeyPoint && landmarkList[12].y < landmarkList[11].y)
//                secondFingerIsOpen = true
//            pseudoFixKeyPoint = landmarkList[14].y
//            if (landmarkList[15].y < pseudoFixKeyPoint && landmarkList[16].y < landmarkList[15].y)
//                thirdFingerIsOpen = true
//            pseudoFixKeyPoint = landmarkList[18].y
//            if (landmarkList[19].y < pseudoFixKeyPoint && landmarkList[20].y < landmarkList[19].y)
//                fourthFingerIsOpen = true
//
//            // Hand gesture recognition
//            return when {
//                thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Jeden"
//                thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Dwa"
//                thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "Trzy"
//                !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "Cztery"
//                thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "Pięć"
//                !thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Kocham Cię <3"
//                GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && !firstFingerIsOpen -> "Dobrze!"
////                !thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "A"
////                !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen -> "B"
////                GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[12]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[16]) && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[20]) -> "C"
////                firstFingerIsOpen && GestureCalculationHelper.isThumbTouchingFinger(landmarkList[4], landmarkList[8]) && GestureCalculationHelper.isThumbTouchingFinger(landmarkList[4], landmarkList[16]) && GestureCalculationHelper.isThumbTouchingFinger(landmarkList[4], landmarkList[20]) -> "D"
////                !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "H"
////                !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && !fourthFingerIsOpen -> "W"
////                thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "L"
////                !thumbIsOpen && firstFingerIsOpen && secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "V"
////                thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "T"
////                thumbIsOpen && !firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && fourthFingerIsOpen -> "Y"
////                !thumbIsOpen && firstFingerIsOpen && !secondFingerIsOpen && !thirdFingerIsOpen && !fourthFingerIsOpen -> "G"
////                !firstFingerIsOpen && secondFingerIsOpen && thirdFingerIsOpen && fourthFingerIsOpen && GestureCalculationHelper.isThumbNearFinger(landmarkList[4], landmarkList[8]) -> "F"
//                else -> {
//                    val info = ("thumbIsOpen " + thumbIsOpen + "firstFingerIsOpen" + firstFingerIsOpen
//                            + "secondFingerIsOpen" + secondFingerIsOpen +
//                            "thirdFingerIsOpen" + thirdFingerIsOpen + "fourthFingerIsOpen" + fourthFingerIsOpen)
//                    Log.d(TAG, "handGestureCalculator: == $info")
//                    "___"
//                }
//
//            }
//        }
        return "___"
    }

    fun leftGestureCalculation(landmarkList: List<LandmarkProto.NormalizedLandmark>): String
            = GestureCalculation(landmarkList, "L").gestureCalculation()

    fun rightGestureCalculation(landmarkList: List<LandmarkProto.NormalizedLandmark>): String
            = GestureCalculation(landmarkList, "R").gestureCalculation()

    var previousXCenter = 0f
    var previousYCenter = 0f
    var previousAngle = 0f// angle between the hand and the x-axis. in radian
    var previous_rectangle_width = 0f
    var previousRectangleHeight = 0f
    var frameCounter = false

    private fun handGestureMoveCalculator(normalizedRectList: List<RectProto.NormalizedRect>): String? {
        val normalizedRect= normalizedRectList[0]
        val height: Float = normalizedRect.getHeight()
        val centerX: Float = normalizedRect.getXCenter()
        val centerY: Float = normalizedRect.getYCenter()
        if (previousXCenter != 0f) {
            val mouvementDistance = GestureCalculationHelper.getEuclideanDistanceAB(centerX.toDouble(), centerY.toDouble(),
                    previousXCenter.toDouble(), previousYCenter.toDouble())
            // LOG(INFO) << "Distance: " << mouvementDistance;
            val mouvementDistanceFactor = 0.02 // only large mouvements will be recognized.

            // the height is normed [0.0, 1.0] to the camera window height.
            // so the mouvement (when the hand is near the camera) should be equivalent to the mouvement when the hand is far.
            val mouvementDistanceThreshold = mouvementDistanceFactor * height
            if (mouvementDistance > mouvementDistanceThreshold) {
                val angle = GestureCalculationHelper.radianToDegree(GestureCalculationHelper.getAngleABC(centerX.toDouble(), centerY.toDouble(),
                        previousXCenter.toDouble(), previousYCenter.toDouble(), previousXCenter + 0.1,
                        previousYCenter.toDouble())).toDouble()
                // LOG(INFO) << "Angle: " << angle;
                if (angle >= -45 && angle < 45) {
                    return "Scrolling right"
                } else if (angle >= 45 && angle < 135) {
                    return "Scrolling up"
                } else if (angle >= 135 || angle < -135) {
                    return "Scrolling left"
                } else if (angle >= -135 && angle < -45) {
                    return "Scrolling down"
                }
            }
        }
        previousXCenter = centerX
        previousYCenter = centerY
        // 2. FEATURE - Zoom in/out
        if (previousRectangleHeight != 0f) {
            val heightDifferenceFactor = 0.03

            // the height is normed [0.0, 1.0] to the camera window height.
            // so the mouvement (when the hand is near the camera) should be equivalent to the mouvement when the hand is far.
            val heightDifferenceThreshold = height * heightDifferenceFactor
            if (height < previousRectangleHeight - heightDifferenceThreshold) {
                return "Zoom out"
            } else if (height > previousRectangleHeight + heightDifferenceThreshold) {
                return "Zoom in"
            }
        }
        previousRectangleHeight = height
        // each odd Frame is skipped. For a better result.
        frameCounter = !frameCounter
        if (frameCounter && multiHandLandmarks != null) {
            for (landmarks in multiHandLandmarks) {
                val landmarkList = landmarks.landmarkList
                val wrist = landmarkList[0]
                val MCP_of_second_finger = landmarkList[9]

                // angle between the hand (wirst and MCP) and the x-axis.
                val ang_in_radian = GestureCalculationHelper.getAngleABC(MCP_of_second_finger.x.toDouble(), MCP_of_second_finger.y.toDouble(),
                        wrist.x.toDouble(), wrist.y.toDouble(), wrist.x + 0.1, wrist.y.toDouble())
                val ang_in_degree = GestureCalculationHelper.radianToDegree(ang_in_radian)
                // LOG(INFO) << "Angle: " << ang_in_degree;
                if (previousAngle != 0f) {
                    val angleDifferenceTreshold = 12.0
                    if (previousAngle >= 80 && previousAngle <= 100) {
                        if (ang_in_degree > previousAngle + angleDifferenceTreshold) {
                            return "Slide left"
                        } else if (ang_in_degree < previousAngle - angleDifferenceTreshold) {
                            return "Slide right"
                        }
                    }
                }
                previousAngle = ang_in_degree.toFloat()
            }
        }
        return ""
    }


}