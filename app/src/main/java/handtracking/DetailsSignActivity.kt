package handtracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.HandsTrackingApp.R
import kotlinx.android.synthetic.main.activity_details_sign.*

class DetailsSignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_sign)
        val label = intent.extras?.getString("label") ?: "No info availabale"
        val image = intent.extras?.getInt("image") ?: 0
        gesture_name.text = label
        gesture_image.setImageResource(image)
    }
}