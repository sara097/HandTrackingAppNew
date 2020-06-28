package handtracking

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceView

class CameraOverlaySurfaceViewGame(
        ctx: Context
) : SurfaceView(ctx) {

    var p1: String = ""
    var p2: String = ""
    var result: String = ""

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canv: Canvas) {
        val paint = Paint()
        paint.color = Color.WHITE
        paint.textSize = 100.0f // za duze
        paint.textAlign = Paint.Align.CENTER

        canv.drawText(p1, 450.0f, 1605.0f, paint) //zle rozmieszczone
        canv.drawText(p2, 550.0f, 1605.0f, paint) //zle rozmieszczone
        canv.drawText(result, 500.0f, 1705.0f, paint) //zle rozmieszczone
    }
}