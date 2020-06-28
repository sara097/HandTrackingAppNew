package handtracking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.HandsTrackingApp.R
import com.google.mediapipe.components.PermissionHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openBtn.setOnClickListener {
            if (!PermissionHelper.cameraPermissionsGranted(this))
                PermissionHelper.checkAndRequestCameraPermissions(this)
            if (PermissionHelper.cameraPermissionsGranted(this)) {
                val intent = Intent(this, CollectGesturesDataActivity::class.java)
                startActivity(intent)
            }
        }
        openBtn2.setOnClickListener {
            if (!PermissionHelper.cameraPermissionsGranted(this))
                PermissionHelper.checkAndRequestCameraPermissions(this)
            if (PermissionHelper.cameraPermissionsGranted(this)) {
                val intent = Intent(this, RecognizeGesturesActivity::class.java)
                startActivity(intent)
            }
        }

        gesturesBtn.setOnClickListener {
            val intent = Intent(this, GesturesListActivity::class.java)
            startActivity(intent)
        }

        feedback_btn.setOnClickListener {
            val intent = Intent(this, RockPaperScissorsActivity::class.java)
            startActivity(intent)
        }


        PermissionHelper.checkAndRequestCameraPermissions(this)

    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}