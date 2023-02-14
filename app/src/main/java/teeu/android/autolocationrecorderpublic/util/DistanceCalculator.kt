package teeu.android.autolocationrecorderpublic.util

import kotlin.math.*

private const val R = 6372.8 * 1000
class DistanceCalculator {
    companion object {
        fun getDistance(fromLat : Double, fromLng : Double, toLat : Double, toLng : Double): Int {
            val dLat = Math.toRadians(toLat - fromLat)
            val dLon = Math.toRadians(toLng - fromLng)
            val a = sin(dLat / 2).pow(2.0) +
                    sin(dLon / 2).pow(2.0) *
                    cos(Math.toRadians(fromLat)) *
                    cos(Math.toRadians(toLat))
            val c = 2 * asin(sqrt(a))
            return (R * c).toInt()
        }
    }
}