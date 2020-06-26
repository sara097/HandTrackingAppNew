package handtracking

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.HandsTrackingApp.R
import com.google.mediapipe.components.*
import com.google.mediapipe.framework.AndroidAssetUtil
import com.google.mediapipe.glutil.EglManager


// Copyright 2019 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Main activity of MediaPipe basic app.  */
open class BasicActivity : AppCompatActivity() {

    companion object {
        const val TAG = "BasicActivity"
        const val FLIP_FRAMES_VERTICALLY = true
        const val BINARY_GRAPH_NAME = "multihandtrackinggpu.binarypb"
        const val INPUT_VIDEO_STREAM_NAME = "input_video"
        const val OUTPUT_VIDEO_STREAM_NAME = "output_video"
        const val OUTPUT_LANDMARKS_STREAM_NAME = "multi_hand_landmarks"
        var CAMERA_FACING = CameraHelper.CameraFacing.FRONT
    }

    init {
        // Load all native libraries needed by the app.
        System.loadLibrary("mediapipe_jni")
        System.loadLibrary("opencv_java3")
    }

    // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
    // frames onto a {@link Surface}.
    lateinit var processor: FrameProcessor

    // Handles camera access via the {@link CameraX} Jetpack support library.
    private lateinit var cameraHelper: CameraXPreviewHelper

    // {@link SurfaceTexture} where the camera-preview frames can be accessed.
    private var previewFrameTexture: SurfaceTexture? = null

    // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
    lateinit var previewDisplayView: CameraOverlaySurfaceView

    // Creates and manages an {@link EGLContext}.
    private lateinit var eglManager: EglManager

    // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
    // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
    private lateinit var converter: ExternalTextureConverter

    // ApplicationInfo for retrieving metadata defined in the manifest.
    private var appInfo: ApplicationInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognize_gestures)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        try {
            appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Cannot find application info: $e")
        }
        previewDisplayView = CameraOverlaySurfaceView(this)
        setupPreviewDisplayView()

        // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
        // binary graphs.
        AndroidAssetUtil.initializeNativeAssetManager(this)
        eglManager = EglManager(null)
        processor = FrameProcessor(
                this,
                eglManager.nativeContext,
                appInfo!!.metaData.getString("binaryGraphName"),
                appInfo!!.metaData.getString("inputVideoStreamName"),
                appInfo!!.metaData.getString("outputVideoStreamName"))
        processor.videoSurfaceOutput.setFlipY(FLIP_FRAMES_VERTICALLY)

//        processor = FrameProcessor(
//                this,
//                eglManager!!.nativeContext,
//                appInfo!!.metaData.getString("binaryGraphName"),
//                appInfo!!.metaData.getString("inputVideoStreamName"),
//                appInfo!!.metaData.getString("outputVideoStreamName"))
//        processor!!
//                .videoSurfaceOutput
//                .setFlipY(
//                        applicationInfo!!.metaData.getBoolean("flipFramesVertically", FLIP_FRAMES_VERTICALLY))
        PermissionHelper.checkAndRequestCameraPermissions(this)
    }


    override fun onResume() {
        super.onResume()
        converter = ExternalTextureConverter(eglManager.context)
        converter.setFlipY(FLIP_FRAMES_VERTICALLY)
        //applicationInfo!!.metaData.getBoolean("flipFramesVertically", FLIP_FRAMES_VERTICALLY))
        converter.setConsumer(processor)
        if (PermissionHelper.cameraPermissionsGranted(this))
            startCamera()

    }

    override fun onPause() {
        super.onPause()
        converter.close()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    protected fun cameraTargetResolution(): Size? {
        return null // No preference and let the camera (helper) decide.
    }

    fun startCamera() {
        cameraHelper = CameraXPreviewHelper()
        cameraHelper.setOnCameraStartedListener { surfaceTexture ->
            val viewGroup = previewDisplayView.parent as ViewGroup
            viewGroup.removeView(previewDisplayView)
            viewGroup.addView(previewDisplayView)
            previewFrameTexture = surfaceTexture
            previewDisplayView.visibility = View.VISIBLE
        }
        println(CAMERA_FACING)
        cameraHelper.startCamera(this, CAMERA_FACING,  /*surfaceTexture=*/null)

//        cameraHelper!!.startCamera(
//                this, cameraFacing,  /*surfaceTexture=*/null) //, cameraTargetResolution());
    }

    protected fun computeViewSize(width: Int, height: Int): Size {
        return Size(width, height)
    }

//    protected fun onPreviewDisplaySurfaceChanged(
//            holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//        // (Re-)Compute the ideal size of the camera-preview display (the area that the
//        // camera-preview frames get rendered onto, potentially with scaling and rotation)
//        // based on the size of the SurfaceView that contains the display.
//        val viewSize = computeViewSize(width, height)
//        val displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize)
//        val isCameraRotated: Boolean = cameraHelper.isCameraRotated()
//
//        // Connect the converter to the camera-preview frames as its input (via
//        // previewFrameTexture), and configure the output width and height as the computed
//        // display size.
//        converter.setSurfaceTextureAndAttachToGLContext(
//                previewFrameTexture,
//                if (isCameraRotated) displaySize.height else displaySize.width,
//                if (isCameraRotated) displaySize.width else displaySize.height)
//    }

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
//        previewDisplayView
//                .getHolder()
//                .addCallback(
//                        object : SurfaceHolder.Callback {
//                            override fun surfaceCreated(holder: SurfaceHolder) {
//                                processor!!.videoSurfaceOutput.setSurface(holder.surface)
//                            }
//
//                            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//                                onPreviewDisplaySurfaceChanged(holder, format, width, height)
//                            }
//
//                            override fun surfaceDestroyed(holder: SurfaceHolder) {
//                                processor!!.videoSurfaceOutput.setSurface(null)
//                            }
//                        })
    }
}