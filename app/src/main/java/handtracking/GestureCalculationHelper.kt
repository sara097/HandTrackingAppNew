package handtracking

import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import kotlin.math.atan2
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class GestureCalculationHelper {

    companion object{

       fun areFingersNear(point1: NormalizedLandmark, point2: NormalizedLandmark): Boolean {
            val distance = getEuclideanDistanceAB(point1.x.toDouble(), point1.y.toDouble(), point2.x.toDouble(), point2.y.toDouble())
            return distance < 0.1
        }

        fun isThumbTouchingFinger(point1: NormalizedLandmark, point2: NormalizedLandmark): Boolean {
            val distance = getEuclideanDistanceAB(point1.x.toDouble(), point1.y.toDouble(), point2.x.toDouble(), point2.y.toDouble())
            return distance < 0.5 && distance > 0.1
        }

        fun getEuclideanDistanceAB(a_x: Double, a_y: Double, b_x: Double, b_y: Double): Double {
            val dist = (a_x - b_x).pow(2.0) + (a_y - b_y).pow(2.0)
            return sqrt(dist)
        }

        fun getAngleABC(a_x: Double, a_y: Double, b_x: Double, b_y: Double, c_x: Double, c_y: Double): Double {
            val ab_x = b_x - a_x
            val ab_y = b_y - a_y
            val cb_x = b_x - c_x
            val cb_y = b_y - c_y
            val dot = ab_x * cb_x + ab_y * cb_y // dot product
            val cross = ab_x * cb_y - ab_y * cb_x // cross product
            return atan2(cross, dot)
        }

        fun radianToDegree(radian: Double): Int {
            return floor(radian * 180.0 / Math.PI + 0.5).toInt()
        }


    }
}