package handtracking

import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark
import kotlin.math.pow
import kotlin.math.sqrt

class GestureCalculationHelper {

    companion object{

       fun areFingersTouching(point1: NormalizedLandmark, point2: NormalizedLandmark): Boolean {
           val distance = getEuclideanDistanceAB(point1.x.toDouble(), point1.y.toDouble(), point2.x.toDouble(), point2.y.toDouble())
           return distance < 0.1
       }

        fun areFingersNear(point1: NormalizedLandmark, point2: NormalizedLandmark): Boolean {
            val distance = getEuclideanDistanceAB(point1.x.toDouble(), point1.y.toDouble(), point2.x.toDouble(), point2.y.toDouble())
            return distance < 0.5 && distance > 0.1
        }

        private fun getEuclideanDistanceAB(a_x: Double, a_y: Double, b_x: Double, b_y: Double): Double {
            val dist = (a_x - b_x).pow(2.0) + (a_y - b_y).pow(2.0)
            return sqrt(dist)
        }

    }
}