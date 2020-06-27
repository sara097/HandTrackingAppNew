package handtracking

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.HandsTrackingApp.R
import kotlinx.android.synthetic.main.activity_gestures_list.*

class GesturesListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestures_list)
        val gl = Gestures.values().map { it.label }
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, gl)
        list.adapter = adapter
        list.setOnItemClickListener { _, _, i, _ ->
            val intent = Intent(this, DetailsSignActivity::class.java)
            intent.putExtra("label", Gestures.values()[i].label)
            intent.putExtra("image", Gestures.values()[i].image)
            startActivity(intent)
        }
    }
}