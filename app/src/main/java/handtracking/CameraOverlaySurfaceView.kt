package handtracking

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceView

open class CameraOverlaySurfaceView(
        ctx: Context
) : SurfaceView(ctx) {

    var text: String = ""
    var movedGesture: String = ""

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canv: Canvas) {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.textSize = 100.0f // za duze
        paint.textAlign = Paint.Align.CENTER
        canv.drawText(text, 460.0f, 1405.0f, paint) //zle rozmieszczone
        canv.drawText(movedGesture, 460.0f, 1505.0f, paint) //zle rozmieszczone
    }
}