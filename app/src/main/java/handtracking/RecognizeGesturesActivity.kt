package handtracking

import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import androidx.camera.core.CameraX
import androidx.core.view.GestureDetectorCompat
import com.google.mediapipe.components.CameraHelper
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList
import com.google.mediapipe.framework.PacketGetter
import kotlinx.android.synthetic.main.activity_recognize_gestures.*


class RecognizeGesturesActivity : BasicActivity(), GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    companion object {
        private const val TAG = "RecognizeGestures"
        private const val OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks"
        private const val OUTPUT_HAND_RECT = "multi_hand_rects"
        var wordsLetters = "w"
    }

    private lateinit var mDetector: GestureDetectorCompat

    override fun onDoubleTap(event: MotionEvent): Boolean {
        CAMERA_FACING =
                if (CAMERA_FACING == CameraHelper.CameraFacing.BACK) CameraHelper.CameraFacing.FRONT
                else CameraHelper.CameraFacing.BACK
        CameraX.unbindAll()
        startCamera()
        return true
    }

    private var multiHandLandmarks: List<NormalizedLandmarkList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        wlSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) wordsLetters = "l"
            else wordsLetters = "w"
        }

        processor.addPacketCallback(OUTPUT_LANDMARKS_STREAM_NAME) { packet ->
            Log.d(TAG, "Received multi-hand landmarks packet.")
            multiHandLandmarks = PacketGetter.getProtoVector(packet, NormalizedLandmarkList.parser())
            Log.d(TAG, "[TS:" + packet.timestamp + "] " + getMultiHandLandmarksDebugString(multiHandLandmarks))
            if (wordsLetters == "l") { //letters
                previewDisplayView.text = handGestureCalculatorLetters(multiHandLandmarks) ?: "___"
            } else {
                val g = handGestureCalculator(multiHandLandmarks) ?: "___"
                if (g != "___") gestureMoved = "..."
                previewDisplayView.text = if (g == "___") gestureMoved else g
            }
            previewDisplayView.invalidate()
        }
        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)
    }

    private fun getMultiHandLandmarksDebugString(multiHandLandmarks: List<NormalizedLandmarkList>?): String {
        if (multiHandLandmarks == null || multiHandLandmarks.isEmpty()) return "No hand landmarks"
        var multiHandLandmarksStr = """Number of hands detected: ${multiHandLandmarks.size}""".trimIndent()
        for ((handIndex, landmarks) in multiHandLandmarks.withIndex()) {

            multiHandLandmarksStr += """	#Hand landmarks for hand[$handIndex]: ${landmarks.landmarkCount}"""
            for ((landmarkIndex, landmark) in landmarks.landmarkList.withIndex())
                multiHandLandmarksStr += """		Landmark [$landmarkIndex]: (${landmark.x}, ${landmark.y}, ${landmark.z})"""
        }
        return multiHandLandmarksStr
    }

    var gestureParts = "" to ""
    var gestureMoved = ""

    private fun handGestureCalculatorLetters(multiHandLandmarks: List<NormalizedLandmarkList>?): String? {
        if (multiHandLandmarks == null || multiHandLandmarks.isEmpty()) return "No hand deal"
        val leftLandmarks = if (multiHandLandmarks.size > 0) multiHandLandmarks[0].landmarkList else return "___"
        if (leftLandmarks.size < 21) return "X"
        val leftSign = GestureCalculation(leftLandmarks, "L").gestureCalculationLetters()
        return leftSign
    }

    private fun handGestureCalculator(multiHandLandmarks: List<NormalizedLandmarkList>?): String? {
        if (multiHandLandmarks == null || multiHandLandmarks.isEmpty()) return "-"
        val leftLandmarks = if (multiHandLandmarks.size > 0) multiHandLandmarks[0].landmarkList else return "___"
        val rightLandmarks = if (multiHandLandmarks.size > 1) multiHandLandmarks[1].landmarkList else null
        if (leftLandmarks.size < 21) return "X"
        val leftSign = leftGestureCalculation(leftLandmarks)
        val g = GestureCalculation(leftLandmarks, "L").partGestureCalculation()

        if (g != "")
            if (g != gestureParts.first) {
                if (g != gestureParts.second && gestureParts.second != "") gestureParts = "" to ""
                if (gestureParts.first != "" && gestureParts.second == "") gestureParts = gestureParts.first to g
                if (gestureParts.first == "") gestureParts = g to ""
                gestureMoved = GestureCalculation.gestureFromParts(gestureParts)
            }

        if (rightLandmarks != null) {
            val rightSign = rightGestureCalculation(rightLandmarks)
            return when {
                leftSign == "___" -> rightSign
                rightSign == "___" -> leftSign
                leftSign == "Jeden" && rightSign == "Pięć" -> "Sześć"
                leftSign == "Dwa" && rightSign == "Pięć" -> "Siedem"
                leftSign == "Trzy" && rightSign == "Pięć" -> "Osiem"
                leftSign == "Cztery" && rightSign == "Pięć" -> "Dziewięć"
                else -> "___"
            }
        } else return leftSign
        return "___"
    }

    private fun leftGestureCalculation(landmarkList: List<LandmarkProto.NormalizedLandmark>): String = GestureCalculation(landmarkList, "L").gestureCalculation()

    private fun rightGestureCalculation(landmarkList: List<LandmarkProto.NormalizedLandmark>): String = GestureCalculation(landmarkList, "R").gestureCalculation()

    override fun onDoubleTapEvent(event: MotionEvent) = true
    override fun onTouchEvent(event: MotionEvent) = if (mDetector.onTouchEvent(event)) true else super.onTouchEvent(event)
    override fun onDown(event: MotionEvent) = true
    override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float) = true
    override fun onLongPress(event: MotionEvent) = Unit
    override fun onScroll(event1: MotionEvent, event2: MotionEvent, distanceX: Float, distanceY: Float) = true
    override fun onShowPress(event: MotionEvent) = Unit
    override fun onSingleTapUp(event: MotionEvent) = true
    override fun onSingleTapConfirmed(event: MotionEvent) = true


}