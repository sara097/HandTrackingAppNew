package handtracking

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraX
import androidx.core.view.GestureDetectorCompat
import com.example.HandsTrackingApp.R
import com.google.mediapipe.components.*
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager
import kotlinx.android.synthetic.main.activity_game.*


class RockPaperScissorsActivity : AppCompatActivity(), GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener {

    companion object {
        private const val TAG = "RecognizeGestures"
        private const val OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks"
        const val FLIP_FRAMES_VERTICALLY = true
        var CAMERA_FACING = CameraHelper.CameraFacing.FRONT
        var p1Score = 0
        var p2Score = 0
    }

    init {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni")
        System.loadLibrary("opencv_java3")
    }

    private lateinit var mDetector: GestureDetectorCompat

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    lateinit var processor: FrameProcessor

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private lateinit var cameraHelper: CameraXPreviewHelper

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private var previewFrameTexture: SurfaceTexture? = null


    // Creates and manages an {@link EGLContext}.
    private lateinit var eglManager: EglManager

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private lateinit var converter: ExternalTextureConverter

    // ApplicationInfo for retrieving metadata defined in the manifest.
    private var appInfo: ApplicationInfo? = null

    lateinit var previewDisplayView: CameraOverlaySurfaceView

    override fun onDoubleTap(event: MotionEvent): Boolean {
        CAMERA_FACING =
                if (CAMERA_FACING == CameraHelper.CameraFacing.BACK) CameraHelper.CameraFacing.FRONT
                else CameraHelper.CameraFacing.BACK
        CameraX.unbindAll()
        startCamera()
        return true
    }

    private var multiHandLandmarks: List<LandmarkProto.NormalizedLandmarkList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        try {
            appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(BasicActivity.TAG, "Cannot find application info: $e")
        }
        previewDisplayView = CameraOverlaySurfaceView(this)
        setupPreviewDisplayView()
        AndroidAssetUtil.initializeNativeAssetManager(this)
        eglManager = EglManager(null)
        processor = FrameProcessor(
                this,
                eglManager.nativeContext,
                appInfo!!.metaData.getString("binaryGraphName"),
                appInfo!!.metaData.getString("inputVideoStreamName"),
                appInfo!!.metaData.getString("outputVideoStreamName"))
        processor.videoSurfaceOutput.setFlipY(BasicActivity.FLIP_FRAMES_VERTICALLY)
        PermissionHelper.checkAndRequestCameraPermissions(this)
        restetBtn.setOnClickListener {
            p1Score = 0
            p2Score = 0
            scores1.text = p1Score.toString()
            scores2.text = p2Score.toString()
        }

        processor.addPacketCallback(OUTPUT_LANDMARKS_STREAM_NAME) { packet ->
            Log.d(TAG, "Received multi-hand landmarks packet.")
            multiHandLandmarks = PacketGetter.getProtoVector(packet, LandmarkProto.NormalizedLandmarkList.parser())
            Log.d(TAG, "[TS:" + packet.timestamp + "] " + getMultiHandLandmarksDebugString(multiHandLandmarks))

            val gestures = handGestureCalculator(multiHandLandmarks)
            if (before != gestures) {
                val res = resultCalculation(gestures)
                if (res.second == "Player 1") p1Score++
                if (res.second == "Player 2") p2Score++
                scores1.text = p1Score.toString()
                scores2.text = p2Score.toString()
                previewDisplayView.text = "P1: ${gestures.first}, P2: ${gestures.second}"
                previewDisplayView.movedGesture = "${res.first}, ${res.second ?: "-"}"
                previewDisplayView.invalidate()
                if (gestures != "X" to "X" || gestures != "" to "")
                    before = gestures
            }

        }

        mDetector = GestureDetectorCompat(this, this)
        mDetector.setOnDoubleTapListener(this)
    }

    var before = "" to ""

    override fun onResume() {
        super.onResume()
        converter = ExternalTextureConverter(eglManager.context)
        converter.setFlipY(BasicActivity.FLIP_FRAMES_VERTICALLY)
        converter.setConsumer(processor)
        if (PermissionHelper.cameraPermissionsGranted(this))
            startCamera()
    }

    override fun onPause() {
        super.onPause()
        converter.close()
    }

    private fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        cameraHelper.setOnCameraStartedListener { surfaceTexture ->
            val viewGroup = previewDisplayView.parent as ViewGroup
            viewGroup.removeView(previewDisplayView)
            viewGroup.addView(previewDisplayView)
            previewFrameTexture = surfaceTexture
            previewDisplayView.visibility = View.VISIBLE
        }
        cameraHelper.startCamera(this, BasicActivity.CAMERA_FACING,  /*surfaceTexture=*/null)
    }

    private fun setupPreviewDisplayView() {
        previewDisplayView.visibility = View.GONE
        val viewGroup = findViewById<ViewGroup>(R.id.preview_display_layout)
        viewGroup.addView(previewDisplayView)
        previewDisplayView.holder.addCallback(
                object : SurfaceHolder.Callback {
                    override fun surfaceCreated(holder: SurfaceHolder) = processor.videoSurfaceOutput.setSurface(holder.surface)
                    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                        // (Re-)Compute the ideal size of the camera-preview display (the area that the
                        // camera-preview frames get rendered onto, potentially with scaling and rotation)
                        // based on the size of the SurfaceView that contains the display.
                        val viewSize = Size(width, height)
                        val displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize)
                        // Connect the converter to the camera-preview frames as its input (via
                        // previewFrameTexture), and configure the output width and height as the computed
                        // display size.
                        converter.setSurfaceTextureAndAttachToGLContext(
                                previewFrameTexture, displaySize.width, displaySize.height)
                    }

                    override fun surfaceDestroyed(holder: SurfaceHolder) = processor.videoSurfaceOutput.setSurface(null)
                })

    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun getMultiHandLandmarksDebugString(multiHandLandmarks: List<LandmarkProto.NormalizedLandmarkList>?): String {
        if (multiHandLandmarks == null || multiHandLandmarks.isEmpty()) return "No hand landmarks"
        var multiHandLandmarksStr = """Number of hands detected: ${multiHandLandmarks.size}""".trimIndent()
        for ((handIndex, landmarks) in multiHandLandmarks.withIndex()) {

            multiHandLandmarksStr += """	#Hand landmarks for hand[$handIndex]: ${landmarks.landmarkCount}"""
            for ((landmarkIndex, landmark) in landmarks.landmarkList.withIndex())
                multiHandLandmarksStr += """		Landmark [$landmarkIndex]: (${landmark.x}, ${landmark.y}, ${landmark.z})"""
        }
        return multiHandLandmarksStr
    }


    fun resultCalculation(res: Pair<String, String>): Pair<String, String?> {
        return when {
            res.first == "X" -> " " to null
            res.first == "" -> "Two players needed!" to null
            res.first == "Scissors!" && res.second == "Scissors!" -> "Tie!" to null
            res.first == "Rock!" && res.second == "Rock!" -> "Tie!" to null
            res.first == "Paper!" && res.second == "Paper!" -> "Tie!" to null

            res.first == "Paper!" && res.second == "Rock!" -> "Paper!" to "Player 1"
            res.first == "Rock!" && res.second == "Paper!" -> "Paper!" to "Player 2"

            res.first == "Scissors!" && res.second == "Rock!" -> "Rock!" to "Player 2"
            res.first == "Rock!" && res.second == "Scissors!" -> "Rock!" to "Player 1"

            res.first == "Scissors!" && res.second == "Paper!" -> "Scissors!" to "Player 1"
            res.first == "Paper!" && res.second == "Scissors!" -> "Scissors!" to "Player 2"
            else -> " " to null
        }
    }

    private fun handGestureCalculator(multiHandLandmarks: List<LandmarkProto.NormalizedLandmarkList>?): Pair<String, String> {
        if (multiHandLandmarks == null || multiHandLandmarks.isEmpty()) return "" to ""
        val leftLandmarks = if (multiHandLandmarks.size > 0) multiHandLandmarks[0].landmarkList else null
        val rightLandmarks = if (multiHandLandmarks.size > 1) multiHandLandmarks[1].landmarkList else null
        if (leftLandmarks == null) return "" to ""
        if (rightLandmarks == null) return "" to ""
        if (leftLandmarks.size < 21) return "X" to "X"
        if (rightLandmarks.size < 21) return "X" to "X"
        val lg = GestureCalculation(leftLandmarks).rpsGestureCalculation()
        val rg = GestureCalculation(rightLandmarks).rpsGestureCalculation()

        return lg to rg
    }

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