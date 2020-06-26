package handtracking

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceView

class CameraOverlaySurfaceView(
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
        canv.drawText(text, 450.0f, 1605.0f, paint) //zle rozmieszczone
        canv.drawText(movedGesture, 450.0f, 1705.0f, paint) //zle rozmieszczone
    }
}