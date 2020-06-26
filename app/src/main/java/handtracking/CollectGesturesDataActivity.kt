package handtracking

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.HandsTrackingApp.R
import com.google.mediapipe.components.*
import com.google.mediapipe.formats.proto.LandmarkProto
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.framework.Packet
import com.google.mediapipe.framework.PacketGetter
import com.google.mediapipe.glutil.EglManager
import kotlinx.android.synthetic.main.activity_hand_tracking.*
import java.util.*

class CollectGesturesDataActivity : AppCompatActivity() {


    init {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni")
        System.loadLibrary("opencv_java3")
    }

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private var previewFrameTexture: SurfaceTexture? = null

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    private lateinit var previewDisplayView: CameraOverlaySurfaceView

    // Creates and manages an {@link EGLContext}.
    private lateinit var eglManager: EglManager

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    private lateinit var processor: FrameProcessor

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private lateinit var converter: ExternalTextureConverter

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private lateinit var cameraHelper: CameraXPreviewHelper

    // ApplicationInfo for retrieving metadata defined in the manifest.
    private var appInfo: ApplicationInfo? = null

    private var gestureName = "test"
    private var gestureData =
            "time, hand, L1x, L1y, L1z, L2x, L2y, L2z, L3x, L3y, L3z, L4x, L4y, L4z, L5x, L5y, L5z, L6x, L6y, L6z, L7x, L7y, L7z" +
                    ", L8x, L8y, L8z, L9x, L9y, L9z, L10x, L10y, L10z, L11x, L11y, L11z, L12x, L12y, L12z, L13x, L13y, L13z" +
                    ", L14x, L14y, L14z, L15x, L15y, L15z, L16x, L16y, L16z, L17x, L17y, L17z, L18x, L18y, L18z, L19x, L19y, L19z" +
                    ", L20x, L20y, L20z, L21x, L21y, L21z\n" //3 sekundy na gest i spytac o nazwe gestu....
    private val handler = Handler()
    private var saveFlag = false
    private var collectFlag = false;
    private var timeElapsed = 0;
    private val interval = 1000;
    private val time = 4000;

    private fun createTimeHandler() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (saveFlag) {
                    timeElapsed++
                    if (timeElapsed == 1) {
                        collectFlag = true
                    }
                    if (timeElapsed == 4) {
                        collectFlag = false;
                        saveFlag = false;
                        timeElapsed = 0
                        gestureName = editText.text.toString()
                        FileOperations(this@CollectGesturesDataActivity, gestureName, gestureData).saveData()
                        gestureData =
                                "time, hand, L1x, L1y, L1z, L2x, L2y, L2z, L3x, L3y, L3z, L4x, L4y, L4z, L5x, L5y, L5z, L6x, L6y, L6z, L7x, L7y, L7z" +
                                        ", L8x, L8y, L8z, L9x, L9y, L9z, L10x, L10y, L10z, L11x, L11y, L11z, L12x, L12y, L12z, L13x, L13y, L13z" +
                                        ", L14x, L14y, L14z, L15x, L15y, L15z, L16x, L16y, L16z, L17x, L17y, L17z, L18x, L18y, L18z, L19x, L19y, L19z" +
                                        ", L20x, L20y, L20z, L21x, L21y, L21z\n"
                    }
                }
                handler.postDelayed(this, interval.toLong())
            }
        }, interval.toLong())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hand_tracking)
        previewDisplayView = CameraOverlaySurfaceView(this)
        createTimeHandler()
        setupPreviewDisplayView()
        try {
            appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(BasicActivity.TAG, "Cannot find application info: $e")
        }
        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        saveButton.setOnClickListener {
            saveFlag = true
        }
        AndroidAssetUtil.initializeNativeAssetManager(this)
        eglManager = EglManager(null)
        processor = FrameProcessor(
                this,
                eglManager.nativeContext,
                appInfo!!.metaData.getString("binaryGraphName"),
                appInfo!!.metaData.getString("inputVideoStreamName"),
                appInfo!!.metaData.getString("outputVideoStreamName"))
        processor.videoSurfaceOutput.setFlipY(FLIP_FRAMES_VERTICALLY)
        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME
        ) { packet: Packet ->
            Log.d(TAG, "Received multi-hand landmarks packet.")
            val multiHandLandmarks = PacketGetter.getProtoVector(packet, LandmarkProto.NormalizedLandmarkList.parser())
            //Rysuje to co chcemy, czyli info o landmarkach poki co
            if (saveFlag)
                gestureData = gestureData + getMultiHandLandmarksDebugString(multiHandLandmarks)
            previewDisplayView.text = getMultiHandLandmarksDebugString(multiHandLandmarks)
            previewDisplayView.invalidate()
        }
        PermissionHelper.checkAndRequestCameraPermissions(this)
    }

    override fun onResume() {
        super.onResume()
        converter = ExternalTextureConverter(eglManager.context)
        converter.setFlipY(FLIP_FRAMES_VERTICALLY)
        converter.setConsumer(processor)
        if (PermissionHelper.cameraPermissionsGranted(this))
            startCamera()
    }

    override fun onPause() {
        super.onPause()
        converter.close()
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

    private fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        cameraHelper.setOnCameraStartedListener { surfaceTexture: SurfaceTexture? ->
            previewFrameTexture = surfaceTexture
            // Make the display view visible to start showing the preview. This triggers the
            // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
            previewDisplayView.visibility = View.VISIBLE
        }
        cameraHelper.startCamera(this, CAMERA_FACING,  /*surfaceTexture=*/null)
    }

    private fun getMultiHandLandmarksDebugString(multiHandLandmarks: List<LandmarkProto.NormalizedLandmarkList>): String {
        if (multiHandLandmarks.isEmpty()) return "No hand landmarks"

        var multiHandLandmarksStr = """
            Number of hands detected: ${multiHandLandmarks.size}
            """.trimIndent()

        //Tylko dwie dłonie nas interesują. Póki co.
        var textToSave = ""
        for ((handIndex, landmarks) in multiHandLandmarks.withIndex()) {
            multiHandLandmarksStr += """	#Hand landmarks for hand[$handIndex]: ${landmarks.landmarkCount}"""
            var landmarkText = ""
            for ((landmarkIndex, landmark) in landmarks.landmarkList.withIndex())
                if (landmarkIndex == 20) landmarkText = landmarkText + "${landmark.x}, ${landmark.y}, ${landmark.z} \n"
                else landmarkText = landmarkText + "${landmark.x}, ${landmark.y}, ${landmark.z}, "

            textToSave = textToSave + "${Calendar.getInstance().time}, $handIndex, $landmarkText"

        }
        return textToSave
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val BINARY_GRAPH_NAME = "multihandtrackinggpu.binarypb"
        private const val INPUT_VIDEO_STREAM_NAME = "input_video"
        private const val OUTPUT_VIDEO_STREAM_NAME = "output_video"
        private const val OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks"
        private val CAMERA_FACING = CameraHelper.CameraFacing.FRONT

        // Flips the camera-preview frames vertically before sending them into FrameProcessor to be
        // processed in a MediaPipe graph, and flips the processed frames back when they are displayed.
        // This is needed because OpenGL represents images assuming the image origin is at the bottom-left
        // corner, whereas MediaPipe in general assumes the image origin is at top-left.
        private const val FLIP_FRAMES_VERTICALLY = true
    }

}
